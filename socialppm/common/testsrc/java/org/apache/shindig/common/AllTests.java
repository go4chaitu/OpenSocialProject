// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.common;

import junit.framework.Test;
import junit.framework.TestSuite;

import junitx.util.DirectorySuiteBuilder;
import junitx.util.SimpleTestFilter;

/**
 * Run all gadgets tests.
 */
public class AllTests extends TestSuite {

  public static Test suite() throws Exception {
    DirectorySuiteBuilder builder = new DirectorySuiteBuilder(
      new SimpleTestFilter());
    return builder.suite("target/test-classes");
  }
}
