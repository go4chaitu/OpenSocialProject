// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.opensocial.spi;

import org.apache.shindig.social.opensocial.model.Person;

import java.util.concurrent.Future;

public interface RelationshipService
{
  /**
   * Create a relationship between two users
   *
   * @param personId
   * @param friendId
   */
  public Future<Person> createRelationship( String personId, String friendId );
}
