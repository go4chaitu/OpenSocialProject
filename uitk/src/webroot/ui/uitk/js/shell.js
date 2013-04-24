// Copyright (c) 2011.  CA Technologies, Inc.  All rights reserved

/**
 * A shell module.  This renders the page shell (header, nav, workspace)
 */
define( [ "jquery",
          "uif/js/clarity", 
          "uitk/js/uitk", 
          "uitk/js/util", 
          "uitk/js/automation",
          "uitk/js/dialog", 
          "uitk/js/tabs",
          "uitk/js/ubermenu",
          "uitk/js/grid/grid",
          "uitk/js/portlet",
          "uitk/js/log" ], function( $, $c ) {

  var A11Y_CSS = "ui/uitk/css/clarity_sr.css";
  var historyBackPointer = null;
  var skipBackUpdate = false;
  var setMissingIFrameAttrs = true; // used to decided whether to set attributes such as title, etc. on iFrame . 
  
  $c.uitk.shell = {};
  
  $c.uitk.shell.resetGlobals = function() {
    $c.uitk.ppm_workspace = null;
  };

  /**
   * Get the encoded value of text. The function uses an empty div but that is never appended to document.
   * @param {String} stringToEscape_ The string that needs to encoded
   */
  $c.uitk.shell.escapeHtml = function( stringToEscape_ ) {
    if( stringToEscape_ == null )
    {
      return "";
    }
    else
    {
      return $('<div/>').text(stringToEscape_).html();
    }
  };

  /**
   * Render the HTML passed, this is a result of HTML that was received that didn't come from Clarity at all.
   * @param {String} body_ the html body 
   * @param {Object} header_ the html header
   */
  $c.uitk.shell.rawPageRender = function( body_, header_ ) {

    //
    //  i - ignore case
    //  m - flag to include new line
    //  g - global
    //
    //  Search for single line links only
    //
    var linksRx = new RegExp( "<link[^>]*\/>", "img" );

    var links;
    if( header_ && header_.match )
    {
      links = header_.match( linksRx );
    }

    if( links && links.length )
    {
      //
      //  If we found links then append them to the head element
      //
      var len = links.length;
      for( var i=0; i < len; i++ ) 
      {
        var s = links[i];
        var eTag = s.indexOf( ">" );
        var hasRel = s.indexOf( 'rel=' );
        if( hasRel > 0 )
        {
          hasRel = s.indexOf( 'stylesheet', hasRel );
        }

        if( hasRel > 0 )
        {
          var srcPos = s.indexOf( "href" );
          if( srcPos > 0 ) 
          {
            // this is an external js script
            var ch = s.substring( srcPos+5, srcPos+6 );
            var ePos = s.indexOf( ch, srcPos+6 );
            $c.util.addCss( "hdr_link_id_" + i, s.substring( srcPos+6, ePos ) );
          }
          else 
          {
            // this must be an inline script
            $c.util.addCssInline( "hdr_link_id_" + i, s.substring( eTag + 1, s.length - 7 ) );
          }
        }
      }
    }

    //
    //  Clear the workspace variable as the workspace is gone, this will force the render method to redraw the menus etc
    //
    $c.uitk.shell.resetGlobals();
    //
    //  Update the ppm_app div with the HTML we received.
    //
    $( "#ppm_app" ).empty().html( body_ );
    //
    //  The next logical step here is to add an event handler on the form so that we get some notification that the form was
    //  submitted, which enables the setup of a callback handler, which (hopefully) will force the browser back into the 
    //  proper rendering cycle.
    //
    var impostorSubmitted = false;
    $( "form" ).each( function ( index ) {
      //
      //  Ensure the form has a name attribute set
      //  
      if( !$(this).attr( "name" ) || $(this).attr( "name" ) == '' )
      {
        $(this).attr( "name", "tmp_form_name" );
  }  
      var formName = $(this).attr( "name");
      $(this).submit( function () {
        //
        //  Override the submit function, this allows us to include the callback handler for the submit
        //
        if( !impostorSubmitted )
        {
          impostorSubmitted = true;
          window.rawSubmitForm( formName );
        }
        return false;
      });
    });
  }  
  /**
   * Render the shell.
   * @param {String} h the html contents of the workspace
   * @param {Object} labels the localized labels of the shell (header links, etc)
   * @param {Object} page the Page response JSO
   * @param {Function} omnibar the callback function to render the omnibar, defaults to internal renderOmniBar function
   */
  $c.uitk.shell.render = function( h, labels, page, omnibar ) {
    $c.log.startPerf( "render", "pageShell" );

    $c.uitk.grid.processing = false;
    // setup "global" jQ variables and their shortcuts
    var $ppm_app = $c.uitk.ppm_app;
    if( !$ppm_app )
      $ppm_app = $c.uitk.ppm_app = $( "#ppm_app" );
    
    var $ppm_wk = $c.uitk.ppm_workspace;
    if( !$ppm_wk )
      $ppm_wk = $c.uitk.ppm_workspace = $( "#ppm_workspace" );

    var isCSA = $c.app.id && $c.app.id == "nsa";

    // -------------  Begin main "rendering" --------------
    if( page && page.full ) {
      $ppm_app.addClass( "ppm_app_full" ).empty().html( h );
      $c.uitk.shell.resetGlobals();
    }
    else if( $ppm_wk.length < 1 || $c.app.langNeedsUpdate) {

      // Add lang attribute to html element for accessibility
      $( "html" ).attr( "lang", $c.session.lang );

      //set missing attributes on all iFrames that have tabindex=-1
	  $c.uitk.shell.setIFrameAttrs();
	  
      // add the screen reader css, if needed
      if( $c.session.screenReaderOpt ) {
        $c.util.addCss( "clarity_sr", A11Y_CSS );
      }
      
      // render the full page, e.g. the overview page after login, or if the language changed
      var con = $ppm_app.removeClass( "ppm_app_full" ).addClass( "ppm_page_bg" ).empty();
      if( page && page.headerFunc ) {
        page.headerFunc.apply( null, [con, labels, page] );
      }
      else {
        renderHeader( con, labels, isCSA );
        var ob = omnibar ? omnibar( con ) : renderOmniBar( con, labels, !isCSA, !isCSA, !isCSA );  // CSA turns off Favorites, Search, and Timesheets
        
        if( isCSA && page && page.id == "nsa.eula" )
          ob.hide();
      }

      // render tabs, if this page has them
      var tabs = $( '<div class="ppm_tabs" role="tablist"/>' ).appendTo( con );
      if( page && page.tabs )
      $c.uitk.tabs.render( tabs, labels, page.tabs );
      
      // Refresh button
      $( "#ppm_refresh" ).click( function(e){
        $( "#ppm_header_wait" ).show();
        $c.uitk.service.Navigation.navigate( window.location.hash );
      });

      // workspace
      $ppm_wk = $c.uitk.ppm_workspace = $( '<div id="ppm_workspace" class="ppm_workspace_bg" role="main"/>' ).appendTo( con )
        .position( {my: "left top", at: "left bottom", of: tabs, collision: "none none" } ).append( h );
      
      var bb = $( '<div id="ppm_workspace_bb" class="ppm_fixed_button_bar"/>' ).appendTo( con );
      
      // global keyboard short cuts
      $(document).keydown( function(e) {
        if( e.ctrlKey && e.altKey ) {
          var chCode = e.which;
          switch( chCode ) {
            case 72:  // h  for Home
              if( $( "#ppm_nav_app_menu" ).is( ":visible" ) )
                $( "#ppm_nav_app" ).mouseout();
              else
                $( "#ppm_nav_app" ).mouseover();
              break;
            case 65:  // a  for Admin
              if( $( "#ppm_nav_admin_menu" ).is( ":visible" ) )
                $( "#ppm_nav_admin" ).mouseout();
              else
                $( "#ppm_nav_admin" ).mouseover();
              break;
            case 70:  // f  for Favorites
              if( $( "#ppm_nav_favs_menu" ).is( ":visible" ) )
                $( "#ppm_nav_favs" ).mouseout();
              else
                $( "#ppm_nav_favs" ).mouseover();
              break;
            case 83:  // s  for Search
              $( "#ppm_search" ).click();
              break;
            case 86:  // v for History Stack (visited)
              if( $( "#ppm_history_menu" ).is( ":visible" ) )
              {
                $( "#ppm_history_menu" ).hide();
              }
              else
              {
                $( "#ppm_history_dd" ).focus().click(); 
              }
              break;
            case 36:  // Home  for Home
              $( "#ppm_home_go" ).click();
              break;
            case 66:  // b  for back 
              $( "#ppm_history_back" ).focus().click();
              break;
            case 116:  // F5 for Refresh
              $( "#ppm_refresh" ).click();
              break;
              /**
               * This code is for Keyboard Pagination for grids AV# CLRT--22397  
               * if searches the Previous or Next button as CTRL+ALT+ pageUP/DOWN is pressed 
               */
        	case 33: // Page UP  for paginate previous page  
        		$( "#prevPageButton" ).click();
        		break;
        	case 34: // Page down  for paginate next page
        		$( "#nextPageButton" ).click();
        		break;
        	/*
        	case 191: // Display keyboard navigation help
        		navigateToPage("union.keyboardNavHelp");                  
        		break;
        	*/
              
          }
        }
        /*
        else if( e.keyCode == 191 && e.shiftKey)
        {
        	navigateToTarget("MNP",null,"union.keyboardNavHelp");
              
        }
        */
        else if( e.keyCode == 112 ) {  // F1 for help
          $( "#ppm_header_help" ).click();
          e.preventDefault();
        }
      });
      
      $c.uitk.button.fixedButtonBar( {resizeCon: $(window), barCon: $ppm_wk, fixedBar: bb } );
    }
    else {
      
      // -------------  Regular page navigation:  render new page -------------------------
      
      if( isCSA && !$( "#ppm_omnibar" ).is( ":visible" ) )
        $( "#ppm_omnibar" ).show();

      //set missing attributes on all iFrames that have tabindex=-1
	  $c.uitk.shell.setIFrameAttrs();
	  
      // update tabs, if this page has them
      var tabs = $( ".ppm_tabs" );
      $c.uitk.tabs.render( tabs.empty(), labels, page.tabs );
      
      // update workspace with new content, adjust postion for show/hide of page tabs
      $ppm_wk.empty().position( {my: "left top", at: "left bottom", of: tabs, collision: "none none" } ).html( h );
            
      setTimeout( function() {
        $c.uitk.button.fixedButtonBar( {resizeCon: $(window), barCon: $ppm_wk, fixedBar: $( "#ppm_workspace_bb" ) } );
      }, 1 );
    }
    // initialize grids.
    setTimeout(function() {$c.uitk.grids = $c.uitk.grid.init('Workspace', $ppm_wk );},950);
    //$c.uitk.grids = $c.uitk.grid.init('Workspace', $ppm_wk );
    // focus the first form element on page
    $c.util.focusFirst( $ppm_wk );
    $c.util.bindPageKeyDownElement($ppm_wk);
    // Use focus to force screen readers to read error messages  
    $c.util.focusScreenReadersOnErrorMessage( $ppm_wk );
    
    if( page ) {
    var title = (page.context && page.context.length > 0 ? page.context.replace( /&amp;nbsp;/g, " " ) + " - " : "") + page.title;
    document.title = "CA Clarity PPM :: " + title;
    }
    
    $( "#ppm_header_wait" ).hide();
    $c.log.stopPerf( "render", "pageShell" );
  };

  /** Find all iframes that have "tabindex=-1" attribute and missing attributes to these iframe elements
   *  attributes set -  'title'
   * 
   * Added in V13.1 for Accessibility
   */
  $c.uitk.shell.setIFrameAttrs = function() {
      if(setMissingIFrameAttrs == true) {
    	  $('iframe[tabindex="-1"]').each(
	    	 function(index){
	    	   var attr = $(this).attr('title'); 
	    	   if(typeof attr == 'undefined' || attr == false) {
	    	      $(this).attr( "title", "No user content");	
	    	   }
	    	 }
          );

    	  // update the flag so that iFrame title is added only once per session or until user browser-refreshes the
          // window. Note, on each page/dialogs, including login page, same five iframes (Gantt, Grid, ObjectAcitons, and 
          // Container) are loaded and hence once tile of these iframe is set then there is no need to set again. If 
    	  // different iframes are loaded on different pages, or if the these iframes become dynamic, or any other req
    	  // changes then get rid of this flag in which case titles will set set on almost every action.
    	  setMissingIFrameAttrs = false;
      }
  }
  
  /**
   * Render the header portion of the shell.
   * @param {Object} con the jQ object of the container of header
   */
  function renderHeader( con, labels, isCSA ) {
  var automationLink = '';
	  if( $c.automation.enabled ) {
	    automationLink = '<a href="javascript:clarity.automation.configure()" class="ppm_header_links_div ppm_automation_link_div">Automation</a>';
	}    
	  
	$( '<div class="ppm_header" role="banner"><table role="presentation" cellspacing="0" cellpadding="0"><tr><td><img id="ppm_header_logo" src="' + $c.uitk.SPACER_SRC + '" alt="CA"/></td><td id="ppm_header_product"><span/></td></tr></table>' +
    '<div class="ppm_header_links">' +
    '<img src="' + $c.uitk.IMAGE_BASE + 'uitk/images/wait_header.gif" id="ppm_header_wait" style="display: none; margin-right: 6px;" alt="wait"/>' +
    '<img src="' + $c.uitk.IMAGE_BASE + 'uitk/images/s.gif" id="ppm_header_user_img" alt=""/>' +
    (isCSA ? '' : '<span id="ppm_header_user">' + $c.session.fullname + '</span>' ) +
    '<a id="ppm_header_logout" href="#">' + labels.logout + '</a>' +
    (isCSA || !$c.app.hasCapa ? '' : '<a id="ppm_header_learn" href="#" class="ppm_header_links_div">' + labels.learn + '</a>' ) +
    '<a id="ppm_header_help" href="#" class="ppm_header_links_div">' + labels.help + '</a>' +
    '<a id="ppm_header_about" href="#" class="ppm_header_links_div">' + labels.about + '</a>' +
    automationLink +
    '</div>' +
    '</div>' ).appendTo( con );
    
    //logout link
    $( "#ppm_header_logout" ).click( function(e) {
      e.preventDefault();
      $c.uitk.service.Navigation.navigate( "#action:security.logoutAction" );
    });

    //learn link
    if( $c.app.hasCapa ) {
      $( "#ppm_header_learn" ).click( function(e) {
        e.preventDefault();
        require( ["uitk/js/help"], function() {
          $c.uitk.help.showCapa();
        });
      });
    }
    
    //help link
    $( "#ppm_header_help" ).click( function(e) {
      e.preventDefault();
      require( ["uitk/js/help"], function() {
        $c.uitk.help.showHelp();
      });
    });
    
    //about link
    $( "#ppm_header_about" ).click( function(e) {
      e.preventDefault();
      $c.uitk.xhr( {
        dataType: "html",
        url: $c.uitk.SERVLET_Q + "&action=mainnav.about",
        success: function( data ) {
          // strip off everything but actual content.
          var s = data.indexOf( '<!-- //start_content:' );
          var e = data.indexOf( '//end_content -->' );
          var h = data.substring( s+21, e );
          $c.uitk.dialog.render( { id:"ppm_about_dialog", title: labels.about, content: h, labels: labels} );
        }
      } );
      
    });
    // Updates the automation links if they exist
    $c.automation.updateAutomationLink();    
  };

  /**
   * Builds the HTML for the Omnibar
   * @param {Object} con the jQuery object that should contain the OmniBar
   * @param {Object} labels the translated i18n labels
   * @param {Boolean} hasSearch true if Search is enabled
   * @param {Boolean} hasFavorites true if Favorites is enabled
   * @param {Boolean} hasTimesheet true if Timesheet icon is enabled
   */
  function renderOmniBar( con, labels, hasSearch, hasFavorites, hasTimesheet ) {
    var w = $( '<div id="ppm_omnibar" role="navigation">' +
        '<button id="ppm_history_back" title="' + labels.back + '" alt="' + labels.back + '"><img src="' + $c.uitk.SPACER_SRC + '" alt="' + labels.back + '"/></button>' +
        '<span id="ppm_history_dd" title="' + labels.recentPages + '" alt="' + labels.recentPages + '" role="button" aria-pressed="false" tabindex="0"><img src="' + $c.uitk.SPACER_SRC + '" alt="' + labels.recentPages + '"/><div id="ppm_history_menu" tabindex="0"/></span>' +
        '<span id="ppm_nav_app" class="ppm_nav_menu" role="menu" tabindex="0" alt="' + labels.home + '" title="' + labels.home + '">' + labels.home + '</span>' +
        '<img src="' + $c.uitk.SPACER_SRC + '" class="ppm_omnibar_div" alt=""/>' +
        '<span id="ppm_nav_admin" class="ppm_nav_menu" role="menu" tabindex="0" alt="' + labels.administration + '" title="' + labels.administration + '">' + labels.administration + '</span>' +
        '<img src="' + $c.uitk.SPACER_SRC + '" class="ppm_omnibar_div" alt=""/>' +
        (hasFavorites ? '<span id="ppm_nav_favs" class="ppm_nav_menu" role="menu" tabindex="0" alt="' + labels.favorites + '" title="' + labels.favorites + '">' + labels.favorites + '</span>' +
        '<img src="' + $c.uitk.SPACER_SRC + '" class="ppm_omnibar_div" alt=""/>' : '') +
        '<div style="float:right; margin-right: 8px;">' +
        ' <button id="ppm_claritysn" class="ppm_omnibar_button" title="' + labels.claritysn + '" alt="' + labels.claritysn + '"><img src="' + $c.uitk.SPACER_SRC + '" alt="' + labels.claritysn + '"/></button>' +
        ' <button id="ppm_refresh" class="ppm_omnibar_button" title="' + labels.refresh + '" alt="' + labels.refresh + '"><img src="' + $c.uitk.SPACER_SRC + '" alt="' + labels.refresh + '"/></button>' +
        ' <button id="ppm_home_go" class="ppm_omnibar_button" title="' + labels.home + '" alt="' + labels.home + '"><img src="' + $c.uitk.SPACER_SRC + '" alt="' + labels.home + '"/></button>' +
        (hasTimesheet ? ' <button id="ppm_timesheet" class="ppm_omnibar_button" title="' + labels.timesheet + '" alt="' + labels.timesheet + '"><img src="' + $c.uitk.SPACER_SRC + '" alt="' + labels.timesheet + '"/></button>' : '' ) +
        renderSearch( hasSearch, labels ) +
        '</div>' +
        '</div>' ).appendTo( con );
    
    // render the uber menus
    uberMenus( labels, hasFavorites );

    // History/Back buttons
    var back = $( "#ppm_history_back" );
    var historyDD = $( "#ppm_history_dd" );
    var historyMenu = $( "#ppm_history_menu" );
            
    historyMenu.keypress( function(e) {
      $c.handleEnter( e, historyMenu );
    });
    
    historyDD.blur( function( e ) {
      var th = this;
      // only hide if focus is not moving amongst children or click on button
      // the setTimeout here allows the focusout event to complete, otherwise documenet.activeElement will always be BODY
      setTimeout( function() {
        if( !$c.util.sameOrChild( th, document.activeElement ) ) {
          historyDD.attr( "aria-pressed", "false" );
          historyMenu.hide();
        }
      }, 0 );
    });

    back.click( function(e) {
      if( historyBackPointer ) {
        var u = historyBackPointer.attr( "href" );
        if( u && u != '' ) {
          $c.uitk.service.Navigation.navigate( u );
          skipBackUpdate = true;
        }
        historyBackPointer = historyBackPointer.next();
      }
    });
    
    back.keypress( function(e) {
      $c.handleEnter( e, back );
    });
    
    historyDD.click( function(e) {
      if( 'true' == $(this).attr( "aria-pressed" ) )
      {
        $(this).attr( "aria-pressed", "false" );
        historyMenu.hide();
      }
      else
      {
        $(this).attr( "aria-pressed", "true" );
        historyMenu.show().position( {my: "left top", at: "left bottom", of: back, offset: "0 -6", collision: "none none" } );
      }
    });

    historyDD.keypress( function(e) {
      $c.handleEnter( e, $( "#ppm_history_dd" ) );
    });
    
    // Search buttons/input
    if( hasSearch ) {
      $( "#ppm_search" ).click( function(e){
        $( "#ppm_search_con" ).toggle();
        $( "#ppm_search_text:visible" ).focus().select();            
      });

      $( "#ppm_search_submit" ).click( function(e) {
        window.submitForm( "search.basicSearchForm", "search.basicSearchValidate" );
        $( "#ppm_search_con" ).hide();
      });

      $( "#ppm_search_text" ).keypress( function(e) {
        $c.handleEnter( e, $( "#ppm_search_submit" ) );
      });

      $( "#ppm_search_advanced" ).click( function(e) {
        $( "#ppm_search_con" ).hide();
      });
    }

    // Timesheets
    if( hasTimesheet ) {
      $( "#ppm_timesheet" ).click( function(e) {
        $c.uitk.service.Navigation.navigate( "#action:timeadmin.currentTimesheet" );        
      });
    }

    $( "#ppm_claritysn" ).click( function(e) {
          //$( "ppm_workspace" ).
//        var req = new XMLHttpRequest();
//        req.open("GET", "/niku/socialppm/samplecontainer/examples/claritysn/index.html", true);
//        req.send(null);
//        var page = req.responseText;
//        document.getElementById("ppm_workspace").innerHTML = page;
      $('#ppm_workspace').load('/niku/socialppm/samplecontainer/examples/claritysn/index.html');
      });

    // Home button
    $( "#ppm_home_go" ).click( function(e){
      $c.uitk.service.Navigation.navigate( "#action:homeActionId" );
    });
    
    return w;
  };
  
  function renderSearch( hasSearch, labels ) {
    var ret = '';
    if( hasSearch ) {
      ret = '<span id="ppm_search_con" role="search">' +
        ' <a id="ppm_search_advanced" href="#action:search.advancedSearchForm&includeFileContents=true&objectTypeId=4&objectTypeId=7&objectTypeId=3&objectTypeId=1&objectTypeId=11&allUsers=true&backAction=homeActionId" title="' + labels.advanced + '">' + labels.advanced + '</a>' + 
        ' <div id="ppm_search_box">' +
        ' <form style="display: inline" name="search.basicSearchForm" method="POST" onsubmit="return false;">' +            
        '  <input type="text" id="ppm_search_text" name="searchKeywords" title="' + labels.search + '"/>' +
        ' </form>' +
        ' <button id="ppm_search_submit" title="' + labels.search + '" alt="' + labels.search + '"><img src="' + $c.uitk.SPACER_SRC + '" alt="' + labels.search + '"/></button>' +
        '</div></span>' +
        '<button id="ppm_search" class="ppm_omnibar_button" title="' + labels.search + '" alt="' + labels.search + '"><img src="' + $c.uitk.SPACER_SRC + '" alt="' + labels.search + '"/></button>';
    }
    return ret;
  };
  
  /**
   * Builds the HTML for the refresh button in a ubermenu
   * @param {String} id the id of the source of the menu
   * @param {Object} m the menu itself
   * @param {Object} labels the i18n labels
   * @param {Boolean} hasFavorites true to show Favorites menu
   */
  function renderRefreshUbermenu( id, m, labels, hasFavorites ) {
    return $( '<button class="ppm_umenu_button ppm_umenu_refresh">' + labels.refresh + '</button>' ).click( function() {
      $c.uitk.shell.refreshUbermenus( id, m, labels, hasFavorites );
    } );
  };
  
  /**
   * Refreshes all ubermenus with new data from the server.  Closes the current menu as well
   * @param {String} id the id of the source of the currently open menu
   * @param {Object} m the currently open menu itself
   * @param {Object} labels the i18n labels
   * @param {Boolean} hasFavorites true to show Favorites menu
   */
  $c.uitk.shell.refreshUbermenus = function ( id, m, labels, hasFavorites ) {
    $c.uitk.ubermenu.close( id, m );
    $( "#ppm_nav_app, #ppm_nav_admin, #ppm_nav_favs" ).removeClass( "ppm_nav_menu_hover" );
    $( "#ppm_nav_app_menu, #ppm_nav_admin_menu, #ppm_nav_favs_menu" ).remove();
    uberMenus( labels, hasFavorites, true );        
  };
  
  /**
   * Builds the HTML for all the uber menus
   * @param {Object} labels the i18n labels
   * @param {Boolean} hasFavorites true if Favorites is available
   * @param {Boolean} mask true to mask the xhr call to the server
   * @returns {Object} the jQuery object for all the uber menus, initially all hidden
   */
  function uberMenus( labels, hasFavorites, mask ) {
    $c.uitk.xhr( {
      data: '{GetNavigatorMenuBean: {UserName: ' + $c.session.username + '}}',
      url: $c.uitk.SERVICES_REL_PATH + "GlobalNavigationService/GetNavigatorMenuBean",
      success: function( data ) {
        // menu model objects
        var menus = data.GetNavigatorMenuBeanResponse.Response.Result.NavigatorMenuResponse.navigatorEntryList.NavigatorEntry;

        // application menu            
        var m = $c.uitk.ubermenu.render( "ppm_nav_app_menu", menus[0], $( "#ppm_nav_app" ), labels );
        var buttons = $( "<div/>" )
        .append(
          $( '<button id="ppm_nav_set_home" class="ppm_umenu_button">' + labels.setAsHome + '</button>' ).click( function() {
            $c.uitk.xhr( {
              data: "{setasHomePage: {Url: " + encodeURIComponent( window.location.href ) + "}}",
              url: $c.uitk.SERVICES_REL_PATH + "HomePageService/setasHomePage",
              success: function(){ $c.uitk.ubermenu.close( "ppm_nav_app_menu", m ); }
            }, true );
          } ) )
        .append(
          $( '<button id="ppm_nav_reset_home" class="ppm_umenu_button">' + labels.resetHome + '</button>' ).click( function() {
            $c.uitk.xhr( {
              data: "{resetHome: {}}",
              url: $c.uitk.SERVICES_REL_PATH + "HomePageService/resetHome",
              success: function(){ $c.uitk.ubermenu.close( "ppm_nav_app_menu", m ); }
            }, true );
          } ) )
        .append( renderRefreshUbermenu( "ppm_nav_app_menu", m, labels, hasFavorites ) );
        
        m.append( buttons );
        
        // administration menu
        var hasAdmin = menus[1] && menus[1].portletCode == "union.adminLeftNav";
        if( hasAdmin ) {
          $( "#ppm_nav_admin" ).css( "display", "inline-block" );
          $( "#ppm_nav_admin + img" ).show();
          m = $c.uitk.ubermenu.render( "ppm_nav_admin_menu", menus[1], $( "#ppm_nav_admin" ), labels );
          buttons = $( "<div/>" ).append( renderRefreshUbermenu( "ppm_nav_admin_menu", m, labels, hasFavorites ) );
          m.append( buttons );
        }
        
        // favs menu
        if( hasFavorites ) {
          var menu = hasAdmin ? menus[2] : menus[1];
          var configAction = menu.childNavEntries.NavigatorEntry.shift(); //remove config link, not needed here
          var config = configAction.actionCode;
          var len = configAction.parameterList.NavigatorParameter.length;
          for( var i=0; i < len; i++ )
            config += "&" + configAction.parameterList.NavigatorParameter[i].parameterCode + "=" + configAction.parameterList.NavigatorParameter[i].parameterValue;
          
          var m = $c.uitk.ubermenu.render( "ppm_nav_favs_menu", menu, $( "#ppm_nav_favs" ), labels );
          var buttons = $( "<div/>" )
          .append(
            $( '<button class="ppm_umenu_button">' + labels.addCurrent + '</button>' ).click( function() {
              var name = $c.uitk.shell.escapeHtml( document.title.substring( 18 ) ); // strip off "CA Clarity PPM :: "
              $c.uitk.xhr( {
                data: '{addFavorites: {Url: "' + $c.uitk.shell.getFavoritesLink() + '", LinkName: "' + name + '"}}',
                url: $c.uitk.SERVICES_REL_PATH + "AddtoFavoritesService/addFavorites",
                success: function() { 
                  $c.uitk.shell.refreshUbermenus( "ppm_nav_favs_menu", m, labels, hasFavorites );
                }
              }, true );
            } ) )
          .append(
            $( '<button class="ppm_umenu_button">' + labels.configure + '</button>' ).click( function() {
              $c.uitk.ubermenu.close( "ppm_nav_favs_menu", m );
              $c.uitk.service.Navigation.navigate( "#action:" + config );
            } ) )
          .append( renderRefreshUbermenu( "ppm_nav_favs_menu", m, labels, hasFavorites ) );
          m.append( buttons );
        }
      }
    }, mask );
  };
  
  /**
   * Builds a string link in the format the Favorites service expects of the current window location from the current href
   * @returns {String} a link for the Favorites service
   */
  $c.uitk.shell.getFavoritesLink = function() {
    return $c.uitk.shell.formatHrefForFavorites( window.location.href );
  };

  /**
   * Builds a string link in the format the Favorites service expects of the current window location
   * @param {String} url_ The URL to encode
   * @returns {String} a link for the Favorites service
   */
  $c.uitk.shell.formatHrefForFavorites = function( url_ ) {
    var sPos = url_.indexOf( "/" + $c.uitk.SERVLET );
    var hPos = url_.indexOf( "#action" );
    var link = url_.substring( 0 , sPos ) + "/app?action=" + url_.substring( hPos + 8 );  // favorites service only likes legacy URLs
    return encodeURIComponent( link );
  };
  
  /**
   * Adds an entry to workspace history
   * @param {String} n the name/title of the entry
   * @param {String} u the url of the entry
   * @returns nothing
   */
  $c.uitk.shell.addHistory = function( n, u, pc ) {
    if( n && u ) {
      var url = $c.util.normalizeLegacyUrl( u );
      var escName = $c.uitk.shell.escapeHtml( n );
      var escPc = $c.uitk.shell.escapeHtml( pc );
      var a = $( '<a href="' + url + '" title="' + escName  + '" data-ppm_page_context="' + escPc + '">' + escName + '</a>' );
      $( "#ppm_history_menu" ).prepend( a ).children( ":gt(19)" ).remove();
      if( !skipBackUpdate )
        historyBackPointer = a.next();
      else
        skipBackUpdate = false;
    }
  };
  
  $c.uitk.shell.setFirstHistory = function( n, u, pc ) {
    if( n && u ) {
      var url = $c.util.normalizeLegacyUrl( u );
      var escName = $c.uitk.shell.escapeHtml(n);
      var escPc = $c.uitk.shell.escapeHtml(pc);
      $( "#ppm_history_menu a:first" ).attr( { 'href': url, 'title': escName, 'data-ppm_page_context': escPc } ).html( escName );
      skipBackUpdate = false;
    }    
  };
} );
