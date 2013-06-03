// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.inject.Singleton;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.couchadapter.utils.CouchDBUtils;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Document;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.DocumentService;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.couchdb.core.CouchData;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

@Singleton
public class DocumentDBHandler implements DocumentService
{

  @Override
  public Future<InputStream> getDocument( String docId_, String docName_ ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    InputStream inputStream = db.getDocument( docId_, docName_ );
    return Futures.immediateFuture( inputStream );
  }

  @Override
  public Future<Map<String,String>> uploadDocument( Document document_ ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    Map<String,String> resultMap = db.uploadDocument( document_ );
    String docId = resultMap.get("docId");
    return Futures.immediateFuture( resultMap );
  }

    @Override
  public Future<Void> deleteDocument( String docId_, String revId_ ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    db.deleteDocument( docId_, revId_ );
    return Futures.immediateFuture( null );
  }
}
