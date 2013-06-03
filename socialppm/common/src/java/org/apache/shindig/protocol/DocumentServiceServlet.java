// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.protocol;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.niku.union.utility.Base64;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang3.StringUtils;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.Nullable;
import org.apache.shindig.common.servlet.HttpUtil;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.social.core.model.DocumentImpl;
import org.apache.shindig.social.couchadapter.utils.CouchDBUtils;
import org.apache.shindig.social.opensocial.model.Document;
import org.couchdb.core.CouchData;
import org.couchdb.impl.CouchDataImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet used to process REST requests (/rest/* etc.)
 */
public class DocumentServiceServlet extends HttpServlet
{

  private static final Logger LOG = Logger.getLogger( DocumentServiceServlet.class.getName() );
  private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
  private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB

  protected static final String X_HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";

  @Override
  protected void doGet(HttpServletRequest servletRequest,
      HttpServletResponse servletResponse)
      throws ServletException, IOException {
    System.out.println(servletRequest.getRequestURI());
  }

  @Override
  protected void doPut(HttpServletRequest servletRequest,
      HttpServletResponse servletResponse)
      throws ServletException, IOException {
  }

  @Override
  protected void doDelete(HttpServletRequest servletRequest,
      HttpServletResponse servletResponse)
      throws ServletException, IOException {
  }

  @Override
  protected void doPost(HttpServletRequest servletRequest,
      HttpServletResponse servletResponse)
      throws ServletException, IOException {
      uploadFile( servletRequest );
  }

  private void uploadFile( HttpServletRequest req ) {

    // Check that we have a file upload request
    boolean isMultipart = ServletFileUpload.isMultipartContent(req);
    if( isMultipart )
    {
      // Create a new file upload handler
      FileItemFactory factory = new DiskFileItemFactory();
      RequestContext ctx = new ServletRequestContext(req);
      ServletFileUpload upload = new ServletFileUpload(factory);
      // sets maximum size of upload file
      upload.setFileSizeMax(MAX_FILE_SIZE);

      // sets maximum size of request (include file + form data)
      upload.setSizeMax(MAX_REQUEST_SIZE);
      // Parse the request
      try
      {
        FileItemIterator iter = upload.getItemIterator(ctx);
        while (iter.hasNext()) {
            FileItemStream item = iter.next();
            String name = item.getFieldName();
            InputStream stream = item.openStream();
            if (item.isFormField()) {
                System.out.println("Form field " + name + " with value "
                    + Streams.asString( stream ) + " detected.");
            } else {
                System.out.println("File field " + name + " with file name "
                    + item.getName() + " detected.");
                // Process the input stream
              Document document = new DocumentImpl();
              document.setInputStream( stream );
              document.setAttachmentName( item.getName() );
              document.setContentType( item.getContentType() );
              CouchData db = CouchDBUtils.getDBInstance();
              db.uploadDocument( document );
            }
        }
      }
      catch( IOException ex )
      {
        ex.printStackTrace();
      }
      catch( FileUploadException ex )
      {
        ex.printStackTrace();
      }

    }
  }
}
