// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupService;
import org.apache.shindig.social.opensocial.spi.UserId;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class GroupDBHandler implements GroupService {
  @Override
  public Future<RestfulCollection<Group>> getGroups( UserId userId, CollectionOptions options, Set<String> fields,
                                                     SecurityToken token ) throws ProtocolException
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}

