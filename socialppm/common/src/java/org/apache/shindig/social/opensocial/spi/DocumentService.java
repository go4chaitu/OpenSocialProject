// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.opensocial.spi;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Document;
import org.apache.shindig.social.opensocial.model.MediaItem;
import org.apache.shindig.social.opensocial.model.MediaLink;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * The ActivityService interface defines the service provider interface to retrieve activities from
 * the underlying SNS.
 */
public interface DocumentService
{

  Future<InputStream> getDocument( String docId_, String docName_ ) throws ProtocolException;

  Future<Map<String,String>> uploadDocument( Document document_ ) throws ProtocolException;

  Future<Void> deleteDocument( String docId_, String revId_ ) throws ProtocolException;
}
