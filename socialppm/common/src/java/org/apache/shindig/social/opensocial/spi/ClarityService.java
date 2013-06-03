// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.opensocial.spi;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Person;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * The ClarityService interface defines the service provider interface to retrieve clarity specific data from
 * the underlying Database.
 */
public interface ClarityService
{

  /**
   * Returns a list of all users in the db.
   * @return a response item with the list of activities.
   */
  Future<RestfulCollection<Person>> getAllUsers()
      throws ProtocolException;

  Future<Map<String,String>> getInstanceData( String objectCode, String instanceId )
      throws ProtocolException;
}
