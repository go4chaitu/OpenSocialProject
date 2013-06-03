// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import com.google.inject.Singleton;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.DataCollection;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

@Singleton
public class AppDataDBHandler implements AppDataService
{
  @Override
  public Future<DataCollection> getPersonData( Set<UserId> userIds, GroupId groupId, String appId, Set<String> fields,
                                               SecurityToken token ) throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<Void> deletePersonData( UserId userId, GroupId groupId, String appId, Set<String> fields,
                                        SecurityToken token ) throws ProtocolException
  {
    return null;  
  }

  @Override
  public Future<Void> updatePersonData( UserId userId, GroupId groupId, String appId, Set<String> fields,
                                        Map<String, String> values, SecurityToken token ) throws ProtocolException
  {
    return null;  
  }
}
