/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.auth;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import com.niku.union.log.NikuLogger;
import com.niku.union.security.DefaultSecurityIdentifier;
import com.niku.union.security.SecurityIdentifier;
import com.niku.union.security.UserSessionController;
import com.niku.union.security.UserSessionControllerFactory;
import com.niku.union.utility.UtilityThreadLocal;
import com.niku.union.utility.caching.TransientCacheUtil;
import com.niku.union.web.DefaultWebRequest;
import com.niku.union.web.WebSession;
import com.niku.union.web.filter.RequestTenantIdExtractor;
import org.apache.shindig.common.Nullable;
import org.apache.shindig.common.logging.i18n.MessageKeys;
import org.apache.shindig.common.servlet.InjectedFilter;

import com.google.inject.name.Named;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter that attempts to authenticate an incoming HTTP request. It uses the guice injected
 * AuthenticationHandlers to try and generate a SecurityToken from the request. Once it finds a non
 * null token it passes that on the chain.
 * <p/>
 * If you wish to add a container specific type of auth system simply register an
 * additional handler.
 */
public class AuthenticationServletFilter extends InjectedFilter
{
  public static final String WWW_AUTHENTICATE_HEADER = "WWW-Authenticate";
  public static final String SOCIALPPM_SESSION_CACHE = "SocialPPMSessionCache";
  public static final String HTTP_SERVLET_REQUEST = "httpServletRequest";
  public static final String SESSION_COOKIE = "sessionCookie";

  //class name for logging purpose
  private static final String CLASSNAME = AuthenticationServletFilter.class.getName();
  private static final Logger LOG = Logger.getLogger( CLASSNAME, MessageKeys.MESSAGES );

  private String realm = "shindig";
  private List<AuthenticationHandler> handlers;

  @Inject(optional = true)
  public void setAuthenticationRealm( @Named("shindig.authentication.realm") String realm )
  {
    this.realm = realm;
  }

  @Inject
  public void setAuthenticationHandlers( List<AuthenticationHandler> handlers )
  {
    this.handlers = handlers;
  }

  public void destroy()
  {
  }

  public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
    throws IOException, ServletException
  {
    if( !(request instanceof HttpServletRequest && response instanceof HttpServletResponse) )
    {
      throw new ServletException( "Auth filter can only handle HTTP" );
    }

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;
    String authHeader = null;
    //Clarity user information
    initAuth( req, resp );

    try
    {
      for( AuthenticationHandler handler : handlers )
      {
        authHeader = handler.getWWWAuthenticateHeader( getRealm( req ) );
        SecurityToken token = handler.getSecurityTokenFromRequest( req );
        if( token != null )
        {
          AuthInfoUtil.setAuthTypeForRequest( req, handler.getName() );
          AuthInfoUtil.setSecurityTokenForRequest( req, token );
          callChain( chain, req, resp );
          return;
        }
        else
        {
          // Set auth header
          setAuthHeader( authHeader, resp );
        }
      }

      // We did not find a security token so we will just pass null
      callChain( chain, req, resp );
    }
    catch( AuthenticationHandler.InvalidAuthenticationException iae )
    {
      Throwable cause = iae.getCause();

      if( LOG.isLoggable( Level.INFO ) )
      {
        LOG.logp( Level.INFO, CLASSNAME, "doFilter", MessageKeys.ERROR_PARSING_SECURE_TOKEN, cause );
      }

      if( iae.getAdditionalHeaders() != null )
      {
        for( Map.Entry<String, String> entry : iae.getAdditionalHeaders().entrySet() )
        {
          resp.addHeader( entry.getKey(), entry.getValue() );
        }
      }
      if( iae.getRedirect() != null )
      {
        onRedirect( req, resp, iae );
      }
      else
      {
        // Set auth header
        setAuthHeader( authHeader, resp );
        onError( req, resp, iae );
      }
    }
  }

  /**
   * Override this to return container server specific realm.
   *
   * @return The authentication realm for this server.
   */
  protected String getRealm( HttpServletRequest request )
  {
    return realm;
  }

  /**
   * Override this to perform extra error processing. Headers will have already been set on the
   * response.
   *
   * @param req  the current http request for this filter
   * @param resp the current http response for this filter
   * @param iae  the exception that caused the error path
   * @throws IOException
   */
  protected void onError( HttpServletRequest req, HttpServletResponse resp,
                          AuthenticationHandler.InvalidAuthenticationException iae ) throws IOException
  {
    Throwable cause = iae.getCause();

    // For now append the cause message if set, this allows us to send any underlying oauth errors
    String message = (cause == null) ? iae.getMessage() : iae.getMessage() + cause.getMessage();
    resp.sendError( HttpServletResponse.SC_UNAUTHORIZED, message );
  }

  /**
   * Override this to perform extra processing on redirect. Headers will have already been set on
   * the response.
   *
   * @param req  the current http request for this filter
   * @param resp the current http response for this filter
   * @param iae  the exception that caused the redirect path
   * @throws IOException
   */
  protected void onRedirect( HttpServletRequest req, HttpServletResponse resp,
                             AuthenticationHandler.InvalidAuthenticationException iae ) throws IOException
  {
    resp.sendRedirect( iae.getRedirect() );
  }

  private void setAuthHeader( @Nullable String authHeader, HttpServletResponse response )
  {
    if( authHeader != null )
    {
      response.addHeader( WWW_AUTHENTICATE_HEADER, authHeader );
    }
  }

  private void callChain( FilterChain chain, HttpServletRequest request,
                          HttpServletResponse response ) throws IOException, ServletException
  {
    if( request.getAttribute( AuthenticationHandler.STASHED_BODY ) != null )
    {
      chain.doFilter( new StashedBodyRequestwrapper( request ), response );
    }
    else
    {
      chain.doFilter( request, response );
    }
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
      UtilityThreadLocal.init( RequestTenantIdExtractor
        .getTenantIdForRequest( (HttpServletRequest) request_, (HttpServletResponse) response_ ) );
      uri = ((HttpServletRequest) request_).getRequestURI();
      if( uri.indexOf( "(" ) > 0 )
      {
        //
        //  Sanitize the URI for logging
        //
        uri = uri.substring( 0, uri.indexOf( "(" ) );
      }
      DefaultWebRequest webRequest = new DefaultWebRequest( (HttpServletRequest) request_ );
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

  private static class StashedBodyRequestwrapper extends HttpServletRequestWrapper
  {
    final InputStream rawStream;
    ServletInputStream stream;
    BufferedReader reader;

    StashedBodyRequestwrapper( HttpServletRequest wrapped )
    {
      super( wrapped );
      rawStream = new ByteArrayInputStream(
        (byte[]) wrapped.getAttribute( AuthenticationHandler.STASHED_BODY ) );
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
      Preconditions.checkState( reader == null,
        "The methods getInputStream() and getReader() are mutually exclusive." );

      if( stream == null )
      {
        stream = new ServletInputStream()
        {
          public int read() throws IOException
          {
            return rawStream.read();
          }
        };
      }
      return stream;
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
      Preconditions.checkState( stream == null,
        "The methods getInputStream() and getReader() are mutually exclusive." );

      if( reader == null )
      {
        Charset charset = Charset.forName( getCharacterEncoding() );
        if( charset == null )
        {
          charset = Charsets.UTF_8;
        }
        reader = new BufferedReader( new InputStreamReader( rawStream, charset ) );
      }
      return reader;
    }
  }
}
