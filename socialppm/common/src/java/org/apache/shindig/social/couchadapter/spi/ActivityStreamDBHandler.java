// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.inject.Singleton;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.couchadapter.utils.CouchDBUtils;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.ActivityStreamService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.couchdb.core.CouchData;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

@Singleton
public class ActivityStreamDBHandler implements ActivityStreamService
{

  @Override
  public Future<RestfulCollection<ActivityEntry>> getActivityEntries( Set<UserId> userIds, GroupId groupId,
                                                                      String appId, Set<String> fields,
                                                                      CollectionOptions options, SecurityToken token )
    throws ProtocolException
  {
    List<Person> idSet = CouchDBUtils.getPeopleList( userIds, groupId, options, fields, token );
    CouchData db = CouchDBUtils.getDBInstance();
    List<ActivityEntry> result = Lists.newArrayList();
    for( Person id : idSet )
    {
      List<ActivityEntry> activityEntryList = db.getUserActivityEntries( id.getId() );

      for( ActivityEntry activityEntry : activityEntryList )
      {
        result.add( activityEntry );
      }
    }
    return Futures.immediateFuture( new RestfulCollection<ActivityEntry>( result ) );
  }

  @Override
  public Future<RestfulCollection<ActivityEntry>> getActivityEntries( UserId userId, GroupId groupId, String appId,
                                                                      Set<String> fields, CollectionOptions options,
                                                                      Set<String> activityEntryIds,
                                                                      SecurityToken token )
    throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    List<ActivityEntry> result = Lists.newArrayList();
    List<ActivityEntry> activityEntryList = db.getUserActivityEntries( userId.getUserId( token ) );

    for( ActivityEntry activityEntry : activityEntryList )
    {
      if( activityEntryIds.contains( activityEntry.getId() ) )
      {
        result.add( activityEntry );
      }
    }
    return Futures.immediateFuture( new RestfulCollection<ActivityEntry>( result ) );
  }

  @Override
  public Future<ActivityEntry> getActivityEntry( UserId userId, GroupId groupId, String appId, Set<String> fields,
                                                 String activityEntryId, SecurityToken token ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    ActivityEntry activityEntry = db.getActivityEntry( activityEntryId, userId.getUserId( token ) );
    return Futures.immediateFuture( activityEntry );
  }

  @Override
  public Future<Void> deleteActivityEntries( UserId userId, GroupId groupId, String appId, Set<String> activityEntryIds,
                                             SecurityToken token ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    for( String activityEntryId : activityEntryIds )
    {
      db.deleteActivityEntry( activityEntryId, userId.getUserId( token ) );
    }
    return Futures.immediateFuture( null );
  }

  @Override
  public Future<ActivityEntry> updateActivityEntry( UserId userId, GroupId groupId, String appId, Set<String> fields,
                                                    ActivityEntry activityEntry, String activityEntryId,
                                                    SecurityToken token )
    throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    db.updateActivityEntry( activityEntry, userId.getUserId( token ) );
    return Futures.immediateFuture( activityEntry );
  }

  @Override
  public Future<ActivityEntry> createActivityEntry( UserId userId, GroupId groupId, String appId, Set<String> fields,
                                                    ActivityEntry activityEntry, SecurityToken token )
    throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    db.createActivityEntry( activityEntry, userId.getUserId( token ) );
    return Futures.immediateFuture( activityEntry );
  }
}
