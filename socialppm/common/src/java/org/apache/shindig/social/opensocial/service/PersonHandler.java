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


import com.google.common.collect.Lists;
import org.apache.shindig.config.ContainerConfig;
import org.apache.shindig.protocol.HandlerPreconditions;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RequestItem;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.GroupService;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.RelationshipService;
import org.apache.shindig.social.opensocial.spi.UserId;

import com.google.common.base.Objects;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;

import com.google.inject.Inject;

/**
 * RPC/REST handler for all /people requests
 */
@Service(name = "people", path = "/{userId}+/{groupId}/{personId}+")
public class PersonHandler {
  private final PersonService personService;
  private final ContainerConfig config;
  private RelationshipService relationshipService;
  private GroupService groupService;

  @Inject
  public PersonHandler(PersonService personService,
      RelationshipService relationshipService, GroupService groupService_, ContainerConfig config) {
    this( personService, config );
    this.relationshipService = relationshipService;
    this.groupService = groupService_;
  }
  // Return a future for the first item of a collection
  private static <T> Future<T> firstItem(Future<RestfulCollection<T>> collection) {
    Function<RestfulCollection<T>, T> firstItem = new Function<RestfulCollection<T>, T>() {
      public T apply(RestfulCollection<T> c) {
        if (c != null && c.getTotalResults() > 0) {
          return c.getList().get(0);
        }
        return null;
      };
    };
    return Futures.lazyTransform(collection, firstItem);
 }

  public PersonHandler(PersonService personService, ContainerConfig config) {
    this.personService = personService;
    this.config = config;
  }

  /**
   * Allowed end-points /people/{userId}+/{groupId} /people/{userId}/{groupId}/{optionalPersonId}+
   *
   * examples: /people/john.doe/@all /people/john.doe/@friends /people/john.doe/@self
   */
  @Operation(httpMethods = "GET")
  public Future<?> get(SocialRequestItem request) throws ProtocolException {
    GroupId groupId = request.getGroup();
    Set<String> optionalPersonId = ImmutableSet.copyOf(request.getListParameter("personId"));
    Set<String> fields = request.getFields(Person.Field.DEFAULT_FIELDS);
    Set<UserId> userIds = request.getUsers();

    // Preconditions
    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    if (userIds.size() > 1 && !optionalPersonId.isEmpty()) {
      throw new IllegalArgumentException("Cannot fetch personIds for multiple userIds");
    }

    CollectionOptions options = new CollectionOptions(request);

    if (userIds.size() == 1) {
      if (optionalPersonId.isEmpty()) {
        if (groupId.getType() == GroupId.Type.self) {
            // If a filter is set then we have to call getPeople(), otherwise use the simpler getPerson()
          if (options.getFilter() != null) {
            Future<RestfulCollection<Person>> people = personService.getPeople(
                userIds, groupId, options, fields, request.getToken());
            return firstItem(people);
          } else {
            return personService.getPerson(userIds.iterator().next(), fields, request.getToken());
          }
        } else {
          return personService.getPeople(userIds, groupId, options, fields, request.getToken());
        }
      } else if (optionalPersonId.size() == 1) {
        // TODO: Add some crazy concept to handle the userId?
        Set<UserId> optionalUserIds = ImmutableSet.of(
            new UserId(UserId.Type.userId, optionalPersonId.iterator().next()));

        Future<RestfulCollection<Person>> people = personService.getPeople(
            optionalUserIds, new GroupId(GroupId.Type.self, null),
            options, fields, request.getToken());
        return firstItem(people);
      } else {
        ImmutableSet.Builder<UserId> personIds = ImmutableSet.builder();
        for (String pid : optionalPersonId) {
          personIds.add(new UserId(UserId.Type.userId, pid));
        }
        // Every other case is a collection response of optional person ids
        return personService.getPeople(personIds.build(), new GroupId(GroupId.Type.self, null),
            options, fields, request.getToken());
      }
    }

    // Every other case is a collection response.
    return personService.getPeople(userIds, groupId, options, fields, request.getToken());
  }

  /**
   * Allowed end-points /people/{userId}/{groupId}
   *
   * examples: /people/john.doe/@all /people/john.doe/@friends /people/john.doe/@self
   */
  @Operation(httpMethods = "PUT", bodyParam = "person")
  public Future<?> update(SocialRequestItem request) throws ProtocolException {
    Set<UserId> userIds = request.getUsers();

    // Enforce preconditions - exactly one user is specified
    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular(userIds, "Multiple userIds not supported");

    UserId userId = userIds.iterator().next();

    // Update person and return it
    return personService.updatePerson(Iterables.getOnlyElement(userIds),
        request.getTypedParameter("person", Person.class),
        request.getToken());
  }

  @Operation(httpMethods = "GET", path="/@supportedFields")
  public List<Object> supportedFields(RequestItem request) {
    // TODO: Would be nice if name in config matched name of service.
    String container = Objects.firstNonNull(request.getToken().getContainer(), "default");
    return config.getList(container,
        "${Cur['gadgets.features'].opensocial.supportedFields.person}");
  }

  /**
   * Create a new friendship between 2 users POST - {id: 'friendId'}
   * /people/{userId}/@friends
   *
   * @param request
   * @return
   * @throws ProtocolException
   */
  @Operation(httpMethods = "POST", path = "/{userId}+/@friends")
  public Future<?> createFriends(SocialRequestItem request)
      throws ProtocolException {
    Set<UserId> userIds = request.getUsers();
    Person person = request.getTypedParameter("body", Person.class);
    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular(userIds,
        "Multiple userIds not supported");

    if (person == null || person.getId() == null || person.getId().length() < 1) {
      throw new IllegalArgumentException(
          "Cannot create relationship without a person to befriend");
    }
    UserId userId = userIds.iterator().next();
    return this.relationshipService.createRelationship(userId.getUserId(request
        .getToken()), person.getId());
  }

  @Operation(httpMethods = "POST", path = "/{userId}+/{groupId}")
  public Future<?> create(SocialRequestItem request)
      throws ProtocolException {
    Set<UserId> userIds = request.getUsers();
    GroupId groupId = request.getGroup();
    Person person = request.getTypedParameter("person", Person.class);
    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular( userIds, "Multiple userIds not supported" );
    HandlerPreconditions.requireNotEmpty( Lists.<Object>newArrayList( groupId ), "No groupId specified" );
    HandlerPreconditions.requireSingular( Lists.<Object>newArrayList(groupId), "Multiple groupIds not supported");

    if (person == null || person.getId() == null || person.getId().length() < 1) {
      throw new IllegalArgumentException(
          "Cannot create relationship without a person to befriend");
    }
    UserId userId = userIds.iterator().next();
    return this.groupService.addUserToGroup( groupId, person.getId());
  }

  @Operation(httpMethods="DELETE")
  public Future<?> delete(SocialRequestItem request)
      throws ProtocolException {

    Set<UserId> userIds = request.getUsers();
    Set<String> userIdsToDelete = ImmutableSet.copyOf(request.getListParameter("userIds"));
    GroupId groupId = request.getGroup();
    HandlerPreconditions.requireNotEmpty(userIds, "No userId specified");
    HandlerPreconditions.requireSingular(userIds, "Multiple userIds not supported");
    HandlerPreconditions.requireNotEmpty(userIdsToDelete, "No userId to be removed from the group specified");
    HandlerPreconditions.requireNotEmpty( Lists.<Object>newArrayList( groupId ), "No groupId specified" );
    HandlerPreconditions.requireSingular( Lists.<Object>newArrayList(groupId), "Multiple groupIds not supported");

    // Throws exceptions if userIds contains more than one element or zero elements
    return this.groupService.removeGroupUser( groupId, userIdsToDelete );
  }
}
