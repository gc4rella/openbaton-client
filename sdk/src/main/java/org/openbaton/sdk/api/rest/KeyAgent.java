/*
 * Copyright (c) 2015 Fraunhofer FOKUS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openbaton.sdk.api.rest;

import org.openbaton.catalogue.security.Key;
import org.openbaton.sdk.api.util.AbstractRestAgent;

/**
 * OpenBaton key-related api requester.
 */
public class KeyAgent extends AbstractRestAgent<Key> {

  public KeyAgent(
      String username,
      String password,
      String projectId,
      boolean sslEnabled,
      String nfvoIp,
      String nfvoPort,
      String path,
      String version) {
    super(username, password, projectId, sslEnabled, nfvoIp, nfvoPort, path, version, Key.class);
  }
}