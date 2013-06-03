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
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.couchdb.core.CouchData;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

@Singleton
public class ActivityDBHandler implements ActivityService
{
  @Override
  public Future<RestfulCollection<Activity>> getActivities( Set<UserId> userIds, GroupId groupId, String appId,
                                                            Set<String> fields, CollectionOptions options,
                                                            SecurityToken token ) throws ProtocolException
  {
    List<Activity> result = Lists.newArrayList();
    String _groupId = (groupId != null)? groupId.toString(): "@friends";

    if( _groupId.equals( "@friends" ))
    {

      List<Person> idSet = CouchDBUtils.getPeopleList( userIds, groupId, options, fields, token );
      if( userIds != null && userIds.size() > 0 )
      {
        for( UserId userId_ : userIds )
        {
          Person person = CouchDBUtils.getPerson(userId_, token);
          if( person != null )
          {
            idSet.add( person );
          }
        }
      }
      CouchData db = CouchDBUtils.getDBInstance();
      for( Person id : idSet )
      {
        List<Activity> activityList = db.getUserActivities( id.getId(), _groupId);
        if( activityList != null && activityList.size() > 0 )
        {
          for( Activity activity : activityList )
          {
            if( appId == null || activity.getAppId() == null )
            {
              result.add( activity );
            }
            else if( activity.getAppId().equals( appId ) )
            {
              result.add( activity );
            }
          }
        }
      }
    }
    else
    {
      CouchData db = CouchDBUtils.getDBInstance();
      result = db.getGroupActivities( _groupId);
    }

    Collections.sort( result, CouchDBUtils.ACTIVITY_COMPARATOR );
    return Futures.immediateFuture( new RestfulCollection<Activity>( result ) );
  }

  @Override
  public Future<RestfulCollection<Activity>> getActivities( UserId userId, GroupId groupId, String appId,
                                                            Set<String> fields, CollectionOptions options,
                                                            Set<String> activityIds, SecurityToken token )
    throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    List<Activity> result = Lists.newArrayList();
    List<Activity> activityList = db.getUserActivities( userId.getUserId( token ) );
    if( activityList != null && activityList.size() > 0 )
    {
      for( Activity activity : activityList )
      {
        if( activityIds.contains( activity.getId() ) )
        {
          result.add( activity );
        }
      }
    }
    return Futures.immediateFuture( new RestfulCollection<Activity>( result ) );
  }

  @Override
  public Future<Activity> getActivity( UserId userId, GroupId groupId, String appId, Set<String> fields,
                                       String activityId, SecurityToken token ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    Activity activity = db.getActivity( activityId, userId.getUserId( token ) );
    return Futures.immediateFuture( activity );
  }

  @Override
  public Future<Void> deleteActivities( UserId userId, GroupId groupId, String appId, Set<String> activityIds,
                                        SecurityToken token ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    for( String activityId : activityIds )
    {
      db.deleteActivity( activityId, userId.getUserId( token ) );
    }
    return Futures.immediateFuture( null );
  }

  @Override
  public Future<Void> createActivity( UserId userId, GroupId groupId, String appId, Set<String> fields,
                                      Activity activity, SecurityToken token ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    db.createActivity( activity );
    return Futures.immediateFuture( null );
  }

  @Override
  public Future<Void> addComment( UserId userId, String activityId, Set<String> fields, Activity comment,
                                  SecurityToken token ) throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    db.addCommentToActivity( comment, activityId );
    return Futures.immediateFuture( null );
  }

  @Override
  public Future<RestfulCollection<Activity>> getActivityComments( String activityId_, Set<String> fields,
                                                                  CollectionOptions options, SecurityToken token )
    throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    List<Activity> result = db.getActivityComments( activityId_ );
    Collections.sort( result, CouchDBUtils.ACTIVITY_COMMENTS_COMPARATOR );
    return Futures.immediateFuture( new RestfulCollection<Activity>( result ) );
  }
  @Override
  public Future<RestfulCollection<Activity>> getNewActivities(long lastUpdatedTime, Set<String> fields, CollectionOptions options, SecurityToken token)
      throws ProtocolException
  {
    CouchData db = CouchDBUtils.getDBInstance();
    List<Activity> result = db.getNewActivites( lastUpdatedTime );
    return Futures.immediateFuture( new RestfulCollection<Activity>( result ) );
  }
}
