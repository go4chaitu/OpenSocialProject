// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.couchadapter.spi;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Singleton;
import org.apache.shindig.social.couchadapter.utils.CouchDBUtils;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.RelationshipService;

import java.util.concurrent.Future;

@Singleton
public class RelationshipDBHandler implements RelationshipService
{

  @Override
  public Future<Person> createRelationship( String personId, String friendId )
  {
    CouchDBUtils.getDBInstance().addFriend( personId, friendId );
    return Futures.immediateFuture( null );
  }
}
