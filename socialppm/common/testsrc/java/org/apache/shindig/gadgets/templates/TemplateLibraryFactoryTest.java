// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.gadgets.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.uri.Uri;
import org.apache.shindig.gadgets.GadgetContext;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.http.HttpResponse;
import org.apache.shindig.gadgets.http.HttpResponseBuilder;
import org.apache.shindig.gadgets.http.RequestPipeline;

import org.junit.Test;

public class TemplateLibraryFactoryTest {

  public static final Uri SPEC_URL = Uri.parse("http://www.example.org/dir/g.xml");
  public static final Uri TEMPLATE_URL = Uri.parse("http://www.example.org/dir/template.xml");
  private static final String TEMPLATE_LIBRARY =
          "<Templates xmlns:my='#my'>" +
          "  <Namespace prefix='my' url='#my'/>" +
          "  <JavaScript>script</JavaScript>" +
          "  <Style>style</Style>" +
          "  <Template tag='my:Tag1'>external1</Template>" +
          "  <Template tag='my:Tag2'>external2</Template>" +
          "  <Template tag='my:Tag3'>external3</Template>" +
          "  <Template tag='my:Tag4'>external4</Template>" +
          "</Templates>";

  @Test
  public void testTemplateRequestAnonymousSecurityToken() throws GadgetException {
    CapturingPipeline pipeline = new CapturingPipeline();
    TemplateLibraryFactory factory = new TemplateLibraryFactory( pipeline, null );
    GadgetContext context = new GadgetContext() {
      @Override
      public Uri getUrl() {
        return SPEC_URL;
      }

      @Override
      public String getContainer() {
        return "default";
      }

      @Override
      public boolean getDebug() {
        return false;
      }

      @Override
      public boolean getIgnoreCache() {
        return true;
      }
    };

    factory.loadTemplateLibrary(context, TEMPLATE_URL);
    assertNotNull( pipeline.request );
    SecurityToken st = pipeline.request.getSecurityToken();
    assertNotNull( st );
    assertTrue( st.isAnonymous() );
    assertEquals( SPEC_URL.toString(), st.getAppUrl() );
  }

  private static class CapturingPipeline implements RequestPipeline {
    HttpRequest request;

    public HttpResponse execute(HttpRequest request) {
      this.request = request;
      return new HttpResponseBuilder().setHttpStatusCode( HttpResponse.SC_OK ).setResponseString( TEMPLATE_LIBRARY ).create();
    }
  }

}

