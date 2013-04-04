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
package org.apache.shindig.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Temporary package for use until we the jsr people release to maven central.
 * http://code.google.com/p/jsr-305/issues/detail?id=13
 *
 * This allows us to remove the jsr305 jar from findbugs which is LGPL and
 * moved from 'provided' to 'runtime' scope due to use by Guice.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Nullable {
}
