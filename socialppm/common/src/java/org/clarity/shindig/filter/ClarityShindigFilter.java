// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.clarity.shindig.filter;

import com.niku.union.log.NikuLogger;
import com.niku.union.performance.PerformanceMonitor;
import com.niku.union.security.DefaultSecurityIdentifier;
import com.niku
  .union.security.SecurityIdentifier;
import com.niku.union.security.UserSessionController;
import com.niku.union.security.UserSessionControllerFactory;
import com.niku.union.utility.UtilityThreadLocal;
import com.niku.union.utility.caching.TransientCacheUtil;
import com.niku.union.web.DefaultWebRequest;
import com.niku.union.web.WebSession;
import com.niku.union.web.filter.RequestTenantIdExtractor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Filter used to add authentication information for social ppm to use.
 *
 * @author sarch04
 * Date: 11/14/12
 * Time: 5:04 PM
 */
public class ClarityShindigFilter implements Filter
{
  public static final String SOCIALPPM_SESSION_CACHE = "SocialPPMSessionCache";
  public static final String HTTP_SERVLET_REQUEST = "httpServletRequest";
  public static final String SESSION_COOKIE = "sessionCookie";

  private static final NikuLogger LOG = NikuLogger.getLogger( ClarityShindigFilter.class );

  private String _encoding;

  public void init( FilterConfig config_ ) throws ServletException
  {
    _encoding = config_.getInitParameter( "requestEncoding" );

    if( _encoding == null )
    {
      _encoding = "UTF-8";
    }
  }

  public void doFilter( ServletRequest request_, ServletResponse response_, FilterChain next_ )
    throws IOException, ServletException
  {
    //
    //  Need to set the encoding before the MT stuff kicks in as it gets parameters, and if the encoding's not set then form parameters
    //  get read wrongly.
    //
    request_.setCharacterEncoding( _encoding );
    // Initialize thread.
    if(LOG.isDebugEnabled())
    {
      LOG.debug( "Initializing request Thread." );
    }
    initAuth( request_, response_ );
    next_.doFilter( request_, response_ );
  }

  public void destroy()
  {
  }


  public boolean initAuth( ServletRequest request_, ServletResponse response_ )
  {
      UtilityThreadLocal.clearAll();
      TransientCacheUtil.addToTransientCache( SOCIALPPM_SESSION_CACHE, HTTP_SERVLET_REQUEST, request_ );
      boolean isValid = false;
      boolean errorSent = false;
      String uri = "unknown";
      try
      {
        UtilityThreadLocal.init( RequestTenantIdExtractor.getTenantIdForRequest( (HttpServletRequest)request_, (HttpServletResponse)response_));
        uri = ((HttpServletRequest) request_).getRequestURI();
        if( uri.indexOf( "(" ) > 0 )
      {
        //
        //  Sanitize the URI for logging
        //
        uri = uri.substring( 0, uri.indexOf( "(" ) );
      }
        DefaultWebRequest webRequest = new DefaultWebRequest( (HttpServletRequest)request_ );
        Cookie cookie = webRequest.getCookie( WebSession.SESSION_ID );
        SecurityIdentifier secId = null;
        if( cookie != null )
        {
          TransientCacheUtil.addToTransientCache( SOCIALPPM_SESSION_CACHE, SESSION_COOKIE, cookie );
          //
          //  Get the session ID and use it
          //
          UserSessionController usController = UserSessionControllerFactory.getInstance();

          //
          // Get a security identifier. If the session is invalid a dummy security is returned.
          //
          secId = usController.get( cookie.getValue() );

          //
          // Set the SecurityIdentifier
          //
          UtilityThreadLocal.setSecurityIdentifier( secId );
        }
        else if( uri.contains( "login" ) )
        {
          //
          //  Only login is allowed
          //
          secId = getLoggedOutSecurityIdentifier();
          //
          //  There are some cases where the request doesn't already have a session - like login - handle
          //  that here
          //
        }
        if( secId == null || !secId.isUserLoggedIn() )
        {
          if( uri.endsWith( "login" ) )
          {
            //
            //  Null out the security identifier as we're trying to login with an expired session
            //
            secId = getLoggedOutSecurityIdentifier();
            isValid = true;
          }
          else
          {
            //
            //  Ooops not authenticated - bye bye
            //
            errorSent = true;
          }
        }
        else
        {
          UtilityThreadLocal.setActionId( "odata." + uri.substring( uri.indexOf( "odata/" ) + 6 ) );
          isValid = true;
        }
      }
      catch( Exception e )
      {
        LOG.info( "Login problem!!" + e.toString() );
        errorSent = true;
      }
      return isValid;
  }

  private SecurityIdentifier getLoggedOutSecurityIdentifier()
  {
    SecurityIdentifier secId = new DefaultSecurityIdentifier();
    secId.setUserId( -666 );
    secId.setUserName( "admin" );
    secId.setUniqueName( "admin" );
    return secId;
  }
}
