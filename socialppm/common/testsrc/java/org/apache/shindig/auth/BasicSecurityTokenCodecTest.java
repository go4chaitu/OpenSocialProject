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
package org.apache.shindig.auth;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.shindig.config.BasicContainerConfig;
import org.apache.shindig.config.ContainerConfig;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class BasicSecurityTokenCodecTest {

  private BasicSecurityTokenCodec codec;
  private ContainerConfig config;

  @Before
  public void setUp() throws Exception {
    config = new BasicContainerConfig();
    codec = new BasicSecurityTokenCodec(config);
  }

  @Test
  public void testGetTokenTimeToLive() throws Exception {
    Builder<String, Object> builder = ImmutableMap.builder();
    Map<String, Object> container = builder
            .put(ContainerConfig.CONTAINER_KEY, ImmutableList.of("default", "tokenTest"))
            .put(SecurityTokenCodec.SECURITY_TOKEN_TTL_CONFIG, Integer.valueOf(300)).build();

    config.newTransaction().addContainer(container).commit();
    assertEquals("Token TTL matches what is set in the container config", 300,
            codec.getTokenTimeToLive("tokenTest"));
    assertEquals("Token TTL matches the default TTL", AbstractSecurityToken.DEFAULT_MAX_TOKEN_TTL,
            codec.getTokenTimeToLive());
  }
}
