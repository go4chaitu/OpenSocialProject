// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.inject.Singleton;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.couchadapter.utils.CouchDBUtils;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.MessageCollection;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.MessageService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.couchdb.core.CouchData;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Implementation of supported services backed by a JSON DB.
 */
@Singleton
public class MessageDBHandler implements MessageService
{
  @Override
  public Future<RestfulCollection<MessageCollection>> getMessageCollections( UserId userId, Set<String> fields,
                                                                             CollectionOptions options,
                                                                             SecurityToken token )
    throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    List<MessageCollection> result = Lists.newArrayList();
    List<MessageCollection> msgCollectionList = db.getMessageCollections( userId.getUserId( token ) );
    if( msgCollectionList != null )
    {
      for( MessageCollection msgCollection : msgCollectionList )
      {
        if( msgCollection != null )
        {
          List<Message> msgs = db.getMessages( userId.getUserId( token ), msgCollection.getId() );
          msgCollection.setTotal( msgs != null ? msgs.size() : 0 );
          msgCollection.setUnread( msgs != null ? msgs.size() : 0 );
          result.add( msgCollection );
        }
      }
    }
    return Futures.immediateFuture( new RestfulCollection<MessageCollection>( result ) );
  }

  @Override
  public Future<MessageCollection> createMessageCollection( UserId userId, MessageCollection msgCollection,
                                                            SecurityToken token ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    if( msgCollection != null )
    {
      db.createMessageCollection( msgCollection, userId.getUserId( token ) );
    }

    return Futures.immediateFuture( msgCollection );
  }

  @Override
  public Future<Void> modifyMessageCollection( UserId userId, MessageCollection msgCollection, SecurityToken token )
    throws ProtocolException
  {
    return Futures.immediateFuture( null );
  }

  @Override
  public Future<Void> deleteMessageCollection( UserId userId, String msgCollId, SecurityToken token )
    throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    db.deleteMessageCollection( userId.getUserId( token ), msgCollId );
    return Futures.immediateFuture( null );
  }

  @Override
  public Future<RestfulCollection<Message>> getMessages( UserId userId, String msgCollId, Set<String> fields,
                                                         List<String> msgIds, CollectionOptions options,
                                                         SecurityToken token ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    List<Message> result = Lists.newArrayList();
    if( msgIds != null && msgIds.size() > 0 )
    {
      for( String msgId : msgIds )
      {
        List<Message> msgList = db.getMessages( userId.getUserId( token ), msgCollId, msgId  );
        result.addAll( msgList );
      }
    }
    else
    {
      List<Message> msgList = db.getMessages( userId.getUserId( token ), msgCollId  );
      result.addAll( msgList );
    }

    return Futures.immediateFuture( new RestfulCollection<Message>( result ) );
  }

  @Override
  public Future<Void> createMessage( UserId userId, String appId, String msgCollId, Message message,
                                     SecurityToken token ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    db.createMessage( message );
    return Futures.immediateFuture( null );
  }

  @Override
  public Future<Void> deleteMessages( UserId userId, String msgCollId, List<String> ids, SecurityToken token )
    throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    if( ids != null && ids.size() > 0 )
    {
      for( String id : ids )
      {
        db.deleteMessage( userId.getUserId( token ), id );
      }
    }
    else if( msgCollId != null )
    {
      List<Message> msgList = db.getMessages( userId.getUserId( token ), msgCollId );
      if( msgList != null )
      {
        for( Message msg : msgList )
        {
          if( msg != null )
          {
            db.deleteMessage( userId.getUserId( token ), msg.getId() );
          }
        }
      }
    }

    return Futures.immediateFuture( null );
  }

  @Override
  public Future<Void> modifyMessage( UserId userId, String msgCollId, String messageId, Message message,
                                     SecurityToken token ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    db.updateMessage( userId.getUserId( token ), message );
    return Futures.immediateFuture( null );
  }
}
