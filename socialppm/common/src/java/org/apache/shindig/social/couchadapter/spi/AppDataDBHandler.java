// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.DataCollection;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class AppDataDBHandler implements AppDataService
{
  @Override
  public Future<DataCollection> getPersonData( Set<UserId> userIds, GroupId groupId, String appId, Set<String> fields,
                                               SecurityToken token ) throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Future<Void> deletePersonData( UserId userId, GroupId groupId, String appId, Set<String> fields,
                                        SecurityToken token ) throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Future<Void> updatePersonData( UserId userId, GroupId groupId, String appId, Set<String> fields,
                                        Map<String, String> values, SecurityToken token ) throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
