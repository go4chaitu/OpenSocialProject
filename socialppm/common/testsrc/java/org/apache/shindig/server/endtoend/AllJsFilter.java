// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.server.endtoend;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.common.servlet.InjectedFilter;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.features.FeatureRegistry;
import org.apache.shindig.gadgets.features.FeatureRegistryProvider;

import com.google.common.base.Joiner;
import com.google.inject.Inject;

public class AllJsFilter extends InjectedFilter {

  private String allFeatures;

  @Inject
  public void setFeatureRegistryProvider(FeatureRegistryProvider provider) {
    try {
      FeatureRegistry registry = provider.get(null);
      Set<String> allFeatureNames = registry.getAllFeatureNames();

      // TODO(felix8a): Temporary hack for caja
      HashSet<String> someFeatureNames = new HashSet<String>(allFeatureNames);
      someFeatureNames.remove("es53-guest-frame");
      someFeatureNames.remove("es53-guest-frame.opt");
      someFeatureNames.remove("es53-taming-frame");
      someFeatureNames.remove("es53-taming-frame.opt");

      allFeatures = Joiner.on(':').join(someFeatureNames);
    } catch (GadgetException e) {
      e.printStackTrace();
    }
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {
    if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
      throw new ServletException("Only HTTP!");
    }

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;

    String requestURI = req.getRequestURI();
    if (!requestURI.contains("all-features-please.js")) {
      chain.doFilter(request, response);
    } else {
      String newURI = requestURI.replace("all-features-please.js", allFeatures + ".js") + "?" + req.getQueryString();
      resp.sendRedirect(newURI);
    }
  }

  public void destroy() {
  }
}

