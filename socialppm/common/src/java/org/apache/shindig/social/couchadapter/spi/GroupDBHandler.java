// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.inject.Singleton;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.couchadapter.utils.CouchDBUtils;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.GroupService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.couchdb.core.CouchData;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

@Singleton
public class GroupDBHandler implements GroupService
{
  @Override
  public Future<RestfulCollection<Group>> getGroups( UserId userId, CollectionOptions options, Set<String> fields,
                                                     SecurityToken token ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    List<Group> result = Lists.newArrayList();
    List<Group> groupList = db.getUserGroups( userId.getUserId( token ) );
    result.addAll( groupList );
    return Futures.immediateFuture( new RestfulCollection<Group>( result ) );
  }

  @Override
  public Future<RestfulCollection<Group>> getFollowings(UserId userId, CollectionOptions options,
    Set<String> fields, SecurityToken token) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    List<Group> result = Lists.newArrayList();
    List<Group> groupList = db.getUserFollowings( userId.getUserId( token ) );
    result.addAll( groupList );
    return Futures.immediateFuture( new RestfulCollection<Group>( result ) );
  }

  @Override
  public Future<Group> getGroup( UserId userId, String groupId, CollectionOptions options, Set<String> fields,
                                                     SecurityToken token ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    Group result = db.getGroup( groupId );
    return Futures.immediateFuture( result );
  }

  @Override
  public Future<Void> createGroup(UserId userId, Set<String> fields, Group group, SecurityToken token) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    List<Group> result = Lists.newArrayList();
    db.createGroup( group, userId.getUserId( token ) );
    return Futures.immediateFuture( null );
  }

  @Override
  public Future<Void> deleteGroup(UserId userId, GroupId groupId, SecurityToken token) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    String currentUserId = userId.getUserId( token );
    db.deleteGroup( currentUserId, groupId.toString() );
    return Futures.immediateFuture( null );
  }

  @Override
  public Future<Void> updateGroup(UserId userId, Group group, SecurityToken token) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    db.updateGroup( group );
    return Futures.immediateFuture( null );
  }

  @Override
  public Future<Void> addUserToGroup( GroupId groupId, String userId ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    db.addUserToGroup( userId, groupId.toString() );
    return Futures.immediateFuture( null );
  }

  @Override
  public Future<Void> removeGroupUser( GroupId groupId, Set<String> userIds ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    for( String userId : userIds )
    {
      db.removeGroupUser( groupId.toString(), userId);
    }
    return Futures.immediateFuture( null );
  }
}

