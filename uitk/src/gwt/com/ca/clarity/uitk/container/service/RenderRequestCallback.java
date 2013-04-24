package com.ca.clarity.uitk.container.service;

import com.ca.clarity.uitk.UILogger;
import com.ca.clarity.uitk.container.UIBuilder;
import com.ca.clarity.uitk.container.event.EventBus;
import com.ca.clarity.uitk.container.event.EventIdType;
import com.ca.clarity.uitk.service.Navigation.Location;
import com.ca.clarity.uitk.container.service.ResponseJSO.Page;
import com.ca.clarity.uitk.event.handler.EventHandler;
import com.ca.clarity.uitk.i18n.I18n;
import com.ca.clarity.uitk.utility.Util;
import com.ca.clarity.uitk.widgets.model.jso.JSArray;
import com.extjs.gxt.ui.client.util.Format;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * @author Kedar Sadekar
 * Date: Apr 10, 2008
 */
public class RenderRequestCallback extends BaseRequestCallback
{
  private JSONObject labels;

  @Override
  public void onResponseReceived( String html_, JSArray<String> widgetIds_, Page page_ )
  {
    UILogger.startPerf( "onResponseReceived", "RenderRequestCallback" );
    NavigationContextManager.getInstance().setRendering( true );

    UIBuilder ui = UIBuilder.getInstance( true );
    ui.clear();
    ui.setUserTheme();

    //  I wish we had a way to localize js files :(  todo
    //  Note that there is a case when the UI needs refreshing, eg on a language update when these need to be refreshed, even
    //  after these are cached.
    //
    if( ui.needLanguageRefresh() || ui.needLocaleRefresh() )
    {
      UIBuilder.clearI18nCaches();
    }
    if( labels == null || ui.needLanguageRefresh() )
    {
      labels = new JSONObject();
      labels.put( "about", new JSONString( I18n.i18n( "About" ).getString() ) );
      labels.put( "addCurrent", new JSONString( I18n.i18n( "Add Current" ).getString() ) );
      labels.put( "addToFav", new JSONString( I18n.i18n( "Add To Favorites" ).getString() ) );
      labels.put( "administration", new JSONString( I18n.i18n( "Administration" ).getString() ) );
      labels.put( "advanced", new JSONString( I18n.i18n( "Advanced" ).getString() ) );
      labels.put( "back", new JSONString( I18n.i18n( "Back" ).getString() ) );
      labels.put( "close", new JSONString( I18n.i18n( "Close" ).getString() ) );
      labels.put( "configure", new JSONString( I18n.i18n( "Configure" ).getString() ) );
      labels.put( "favorites", new JSONString( I18n.i18n( "Favorites" ).getString() ) );
      labels.put( "help", new JSONString( I18n.i18n( "Help" ).getString() ) );
      labels.put( "home", new JSONString( I18n.i18n( "Home" ).getString() ) );
      labels.put( "logout", new JSONString( I18n.i18n( "Logout" ).getString() ) );
      labels.put( "learn", new JSONString( I18n.i18n( "Learn" ).getString() ) );
      labels.put( "minimize", new JSONString( I18n.i18n( "Minimize" ).getString() ) );
      labels.put( "maximize", new JSONString( I18n.i18n( "Maximize" ).getString() ) );
      labels.put( "next", new JSONString( I18n.i18n( "Next" ).getString() ) );
      labels.put( "previous", new JSONString( I18n.i18n( "Previous" ).getString() ) );
      labels.put( "search", new JSONString( I18n.i18n( "Search" ).getString() ) );
      labels.put( "setAsHome", new JSONString( I18n.i18n( "Set as Home" ).getString() ) );
      labels.put( "recentPages", new JSONString( I18n.i18n( "Recent Pages" ).getString() ) );
      labels.put( "refresh", new JSONString( I18n.i18n( "Refresh" ).getString() ) );
      labels.put( "resetHome", new JSONString( I18n.i18n( "Reset Home" ).getString() ) );
      labels.put( "restore", new JSONString( I18n.i18n( "Restore" ).getString() ) );
      labels.put( "timesheet", new JSONString( I18n.i18n( "Current Timesheet" ).getString() ) );
      labels.put( "claritysn", new JSONString( I18n.i18n( "Clarity Social Network" ).getString() ) );
    }

    Location loc = getLocation() == null ? setLocation( Location.Workspace ) : getLocation();

    // Both the modal and MNP contexts are reset when they are closed, so only need to reset Workspace and ODP
    if ( loc == Location.Workspace )
    {
      NavigationContextManager.getInstance().resetContext( loc );
    }

    NavigationContextManager.getInstance().setContext( loc, getURL(), getParentLocation(), "", getTarget(), isNewModal(), getData() );
    NavigationContextManager.getInstance().setRenderContext( loc );
    NavigationContextManager.getInstance().setEventContext( loc );
    NavigationContext ctx = NavigationContextManager.getInstance().getNavigationContext( loc );

    ctx.setPageTitle( Format.htmlDecode( page_.getTitle() ) );
    ctx.setPageContext( Util.removeTitleTags( Format.htmlDecode( page_.getContext() ) ) );
    ctx.setPageID( page_.getId() );

    switch( loc )
    {
      case MNP:
        ui.setMNP( null, page_, html_, labels.getJavaScriptObject(), getParentLocation() );
        break;
      case Modal:
        ui.setModalPopup( null, page_, html_, labels.getJavaScriptObject(), getParentLocation(), isNewModal() );
        break;
      default:
        JavaScriptObject func = "projmgr.wbsEditor".equals( page_.getId() ) ? getGanttHeaderFunc() : null;
        renderPage( html_, labels.getJavaScriptObject(), page_, func );
        break;
    }

    if( loc == Location.Workspace || loc == Location.MNP )
    {
      addHistory( loc, ctx, isAddHistory() );
    }    
    // ##### Uncomment the following like to display memory leak in debug.
    // See http://cawiki.ca.com/display/GOV/GWT+leak+debugging
    //DOM.showit();

    NavigationContextManager.getInstance().setRendering( false );
    UILogger.stopPerf( "onResponseReceived", "RenderRequestCallback" );
    
    invokeNativePageLoadHandlers();
  }


  /**
   * Adds the history to the top of the list
   * @param loc the location being processed
   * @param context the current nav context
   * @param setSelected whether to update the first entry based on page context 
   */
  private static void addHistory( Location loc, NavigationContext ctx, boolean setSelected )
  {
    boolean addToHistory = true;
    boolean topContextMatches = false;
    
    // Do not add to history if the URL matches the top history navigation context, or if the page context matches
    JsArrayString topEntry = null;
    try
    {
      if( loc == Location.MNP )
        topEntry = getFirstMnpHistory();
      else
        topEntry = getFirstHistory();
    }
    catch( JavaScriptException e )                 
    {
      String moduleName = GWT.getModuleName();
      if( "com.ca.clarity.test.JUnit".equals( moduleName ) )
      {
        // for testing purposes use context URL as can't delegate up to JQuery APIs.
        topEntry.set( 0, ctx.getUrl() );
      }
      else 
      {
        throw e;
      }
    }
    
    String sanTitle = Util.removeTitleTags( ctx.getFormattedPageTitle() );
    String topContext = topEntry.get( 1 );
    topContextMatches = topContext != null && ctx.getPageContext() != null && topContext.length() > 0 && topContext.equals( ctx.getPageContext() );
    addToHistory = !topContextMatches && ( topEntry.get( 0 ) == null || ( !topEntry.get( 0 ).equals( ctx.getUrl() ) && !topEntry.get( 2 ).equals( sanTitle ) ) );
    
    if( addToHistory )
    {
      if( loc == Location.MNP )
        addMnpHistory( sanTitle, ctx.getUrl(), ctx.getPageContext() );
      else
        addHistory( sanTitle, ctx.getUrl(), ctx.getPageContext() );
    }
    else if( topContextMatches && setSelected )
    {
      if( loc == Location.MNP )
        setMnpFirstHistory( sanTitle, ctx.getUrl(), ctx.getPageContext() );
      else
        setFirstHistory( sanTitle, ctx.getUrl(), ctx.getPageContext() );
    }
  }

  /** Set the first history entry in the History drop down */
  private static native void setFirstHistory( String n, String url, String pc ) /*-{
    $wnd.clarity.uitk.shell.setFirstHistory( n, url, pc );
  }-*/;
  
  /** Set the first history entry in the MNP History drop down */
  private static native void setMnpFirstHistory( String n, String url, String pc ) /*-{
    $wnd.clarity.uitk.dialog.setFirstHistory( n, url, pc );
  }-*/;
  
  /** Add entry to the History drop down */
  private static native void addHistory( String n, String url, String pc ) /*-{
    $wnd.clarity.uitk.shell.addHistory( n, url, pc );
  }-*/;
  
  /** Add entry to the MNP History drop down */
  private static native void addMnpHistory( String n, String url, String pc ) /*-{
    $wnd.clarity.uitk.dialog.addMnpHistory( n, url, pc );
  }-*/;

  /** Get the first history url and page context from the History widget */
  public static native JsArrayString getFirstHistory() /*-{
    var ar = [];
    var f = $wnd.$( "#ppm_history_dd a:first" );
    if( f && f.length ) {
      ar[0] = f.attr( "href" );
      ar[1] = f.data( "ppm_page_context" );
      ar[2] = f.text();
    }
    return ar;
  }-*/;

  /** Get the first history url and page context from the MNP History widget */
  public static native JsArrayString getFirstMnpHistory() /*-{
    var ar = [];
    var f = $wnd.$( "#ppm_mnp_history_menu > a:first" );
    if( f && f.length ) {
      ar[0] = f.attr( "href" );
      ar[1] = f.data( "ppm_page_context" );
      ar[2] = f.text();
    }
    return ar;
  }-*/;
  
  private static void addHandlerIfNoneRegistered( EventIdType.Events event, EventHandler handler )
  {
    EventBus eb = EventBus.getInstance();
    if( !eb.hasHandler( event ) )
      eb.registerHandler( event, handler );
  }

  /** Return the Gantt header function */
  private static native JavaScriptObject getGanttHeaderFunc() /*-{
    return $wnd.clarity.gantt.header;
  }-*/;

  /** Render the page received by this callback */
  private static native void renderPage( String h, JavaScriptObject labels, Page page, JavaScriptObject func ) /*-{
    if( page && func )
      page.headerFunc = func;

    $wnd.clarity.uitk.shell.render( h, labels, page );
  }-*/;
  
  // for hooks
  private static native void invokeNativePageLoadHandlers() /*-{
    $wnd.invokeNativePageLoadHandlers();
  }-*/;
}
