/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shindig.social.opensocial.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.shindig.protocol.HandlerPreconditions;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Group;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.GroupService;
import org.apache.shindig.social.opensocial.spi.UserId;

import com.google.inject.Inject;


/**
 * RPC/REST handler for groups requests
 * @since 2.0.0
 */
@Service(name = "groups", path = "/{userId}")
public class GroupHandler {

  private final GroupService service;

  @Inject
  public GroupHandler(GroupService service) {
    this.service = service;
  }

  @Operation(httpMethods = "GET")
  public Future<?> get(SocialRequestItem request) throws ProtocolException {
    Set<UserId> userIds = request.getUsers();
    CollectionOptions options = new CollectionOptions(request);

    // Preconditions
    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular(userIds, "Only one userId must be specified");

    return service.getGroups(userIds.iterator().next(), options, request.getFields(), request.getToken());
  }

  @Operation(httpMethods = "GET")
  public Future<?> getGroup(SocialRequestItem request) throws ProtocolException {
    Set<UserId> userIds = request.getUsers();
    CollectionOptions options = new CollectionOptions(request);
    String groupId = request.getParameter("groupId");
    // Preconditions
    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular(userIds, "Only one userId must be specified");
    HandlerPreconditions.requireNotEmpty(Lists.<String>newArrayList(groupId), "No groupId specified");

    return service.getGroup(userIds.iterator().next(), groupId, options, request.getFields(), request.getToken());
  }

  @Operation(httpMethods = "GET")
  public Future<?> getFollowings(SocialRequestItem request) throws ProtocolException {
    Set<UserId> userIds = request.getUsers();
    CollectionOptions options = new CollectionOptions(request);

    // Preconditions
    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular(userIds, "Only one userId must be specified");

    return service.getFollowings(userIds.iterator().next(), options, request.getFields(), request.getToken());
  }

  @Operation(httpMethods = "PUT", bodyParam = "group")
  public Future<?> update(SocialRequestItem request) throws ProtocolException {
    Set<UserId> userIds = request.getUsers();

    // Enforce preconditions - exactly one user is specified
    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular(userIds, "Multiple userIds not supported");

    UserId userId = userIds.iterator().next();

    // Update Group and return it
    return service.updateGroup(Iterables.getOnlyElement(userIds),
        request.getTypedParameter("group", Group.class),
        request.getToken());
  }

    /**
   * Allowed end-points /groups/{userId}/@self
   *
   * examples: /groups/john.doe/@self - postBody is a group object
   */
  @Operation(httpMethods="POST", bodyParam = "group")
  public Future<?> create(SocialRequestItem request) throws ProtocolException {

    Set<UserId> userIds = request.getUsers();
    List<String> groupIds = request.getListParameter("groupId");

    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular( userIds, "Multiple userIds not supported" );

    return service.createGroup( Iterables.getOnlyElement( userIds ), request.getFields(),
      request.getTypedParameter( "group", Group.class ),
      request.getToken() );
  }

   @Operation(httpMethods="DELETE")
  public Future<?> delete(SocialRequestItem request) throws ProtocolException {

    Set<UserId> userIds = request.getUsers();
    String groupId = request.getParameter("groupId");
    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular( userIds, "Multiple userIds not supported" );
    HandlerPreconditions.requireNotEmpty( Lists.<String>newArrayList(groupId), "No groupId specified");
    HandlerPreconditions.requireSingular( Lists.<String>newArrayList(groupId), "Multiple groupIds not supported" );
    return service.deleteGroup( Iterables.getOnlyElement( userIds ), new GroupId(groupId), request.getToken() );
  }
}
