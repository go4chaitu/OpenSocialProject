// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.auth;

import org.apache.shindig.common.EasyMockTestCase;
import org.apache.shindig.common.servlet.HttpServletResponseRecorder;

import com.google.common.collect.ImmutableList;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;

public class AuthenticationServletFilterTest extends EasyMockTestCase {
  private static final String TEST_AUTH_HEADER = "Test Authentication Header";

  private AuthenticationServletFilter filter;

  private HttpServletRequest request;
  private HttpServletResponse response;
  private HttpServletResponseRecorder recorder;
  private FilterChain chain;
  private AuthenticationHandler nullStHandler;

  @Before
  public void setup() {
    request = mock(HttpServletRequest.class);
    response  = mock(HttpServletResponse.class);
    recorder = new HttpServletResponseRecorder(response);
    chain = mock(FilterChain.class);
    filter = new AuthenticationServletFilter();
    nullStHandler = new NullSecurityTokenAuthenticationHandler();
  }

  @Test(expected = ServletException.class)
  public void testDoFilter_BadArgs() throws Exception {
    filter.doFilter(null, null, null);
  }

  @Test
  public void testNullSecurityToken() throws Exception {
    filter.setAuthenticationHandlers(ImmutableList.<AuthenticationHandler>of(nullStHandler));
    filter.doFilter(request, recorder, chain);
    assertEquals(TEST_AUTH_HEADER,
        recorder.getHeader(AuthenticationServletFilter.WWW_AUTHENTICATE_HEADER));
  }

  private static class NullSecurityTokenAuthenticationHandler implements AuthenticationHandler {
    public String getName() {
      return "TestAuth";
    }

    public SecurityToken getSecurityTokenFromRequest(HttpServletRequest request)
        throws InvalidAuthenticationException {
      return null;
    }

    public String getWWWAuthenticateHeader(String realm) {
      return TEST_AUTH_HEADER;
    }
  }
}
