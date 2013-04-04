// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import java.util.Set;
import java.util.concurrent.Future;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class ActivityDBHandler implements ActivityService
{
  @Override
  public Future<RestfulCollection<Activity>> getActivities( Set<UserId> userIds, GroupId groupId, String appId,
                                                            Set<String> fields, CollectionOptions options,
                                                            SecurityToken token ) throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Future<RestfulCollection<Activity>> getActivities( UserId userId, GroupId groupId, String appId,
                                                            Set<String> fields, CollectionOptions options,
                                                            Set<String> activityIds, SecurityToken token )
    throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Future<Activity> getActivity( UserId userId, GroupId groupId, String appId, Set<String> fields,
                                       String activityId, SecurityToken token ) throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Future<Void> deleteActivities( UserId userId, GroupId groupId, String appId, Set<String> activityIds,
                                        SecurityToken token ) throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Future<Void> createActivity( UserId userId, GroupId groupId, String appId, Set<String> fields,
                                      Activity activity, SecurityToken token ) throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
