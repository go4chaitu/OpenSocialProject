// Copyright (c) 2013, CA Inc.  All rights reserved.
package org.apache.shindig.social.opensocial.service;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.apache.shindig.config.ContainerConfig;
import org.apache.shindig.protocol.HandlerPreconditions;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RequestItem;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.ClarityService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Rest/RPC handler for all clarity data related requests
 */
@Service(name = "claritydata", path="/{userId}+")
public class ClarityDataHandler
{

  private final ClarityService service;
  private final ContainerConfig config;

  @Inject
  public ClarityDataHandler( ClarityService service, ContainerConfig config ) {
    this.service = service;
    this.config = config;
  }

  @Operation(httpMethods = "GET")
  public Future<?> get(SocialRequestItem request) throws ProtocolException {
    return service.getAllUsers();
  }
  @Operation(httpMethods = "GET")
  public Future<?> getInstanceData(SocialRequestItem request) throws ProtocolException {
    String objectCode = request.getParameter( "objectCode" );
    String instanceId = request.getParameter( "instanceId" );

    HandlerPreconditions.requireNotEmpty( Lists.<Object>newArrayList( objectCode ), "No object code specified" );
    HandlerPreconditions.requireNotEmpty( Lists.<Object>newArrayList(instanceId), "No instance Id supported");

    return service.getInstanceData( objectCode, instanceId );
  }
}
