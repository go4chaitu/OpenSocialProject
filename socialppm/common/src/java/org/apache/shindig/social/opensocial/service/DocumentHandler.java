// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.opensocial.service;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;
import org.apache.shindig.config.ContainerConfig;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.model.Document;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.ClarityService;
import org.apache.shindig.social.opensocial.spi.DocumentService;
import org.apache.shindig.social.opensocial.spi.GroupId;

import java.util.Set;
import java.util.concurrent.Future;

/**
 * Rest/RPC handler for all clarity data related requests
 */
@Service(name = "document", path="/{documentId}/{documentName}")
public class DocumentHandler
{

  private final DocumentService service;
  private final ContainerConfig config;

  @Inject
  public DocumentHandler( DocumentService service, ContainerConfig config ) {
    this.service = service;
    this.config = config;
  }

  @Operation(httpMethods = "GET")
  public Future<?> get(SocialRequestItem request) throws ProtocolException {
    String docId = request.getParameter( "documentId" );
    String docName = request.getParameter( "documentName" );
    return service.getDocument( docId, docName );
  }
  @Operation(httpMethods = "PUT", bodyParam = "document")
  public Future<?> put(SocialRequestItem request) throws ProtocolException {
    service.uploadDocument( request.getTypedParameter("document", Document.class) );
    return Futures.immediateFuture( null );
  }

  @Operation(httpMethods = "POST", bodyParam = "document")
  public Future<?> post(SocialRequestItem request) throws ProtocolException {
    service.uploadDocument( request.getTypedParameter("document", Document.class) );
    return Futures.immediateFuture( null );
  }

  @Operation(httpMethods="DELETE")
  public Future<?> delete(SocialRequestItem request)
        throws ProtocolException {
    String docId = request.getParameter( "docId" );
    String revId = request.getParameter( "revId" );
    service.deleteDocument( docId, revId );
    return Futures.immediateFuture( null );
  }
}
