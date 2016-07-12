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

package org.openbaton.sdk.api.util;

import org.openbaton.sdk.api.annotations.Help;
import org.openbaton.sdk.api.exception.SDKException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lto on 03/07/15.
 */
public class AbstractRestAgent<T extends Serializable> extends RestRequest {

  private final Class<T> clazz;
  private Logger log = LoggerFactory.getLogger(this.getClass());

  public AbstractRestAgent(
      String username,
      String password,
      String projectId,
      boolean sslEnabled,
      String nfvoIp,
      String nfvoPort,
      String path,
      String version,
      Class<T> tClass) {
    super(username, password, projectId, sslEnabled, nfvoIp, nfvoPort, path, version);
    clazz = tClass;
  }

  public Class<T> getClazz() {
    return clazz;
  }

  /**
   * Adds a new VNF software Image to the object repository
   *
   * @param object : obj to add
   * @return string: The object filled with values from the api
   */
  @Help(help = "Create the object of type {#}")
  public T create(final T object) throws SDKException {
    return (T) requestPost(object);
  }

  /**
   * Removes the VNF software Image from the Image repository
   *
   * @param id : The Obj's id to be deleted
   */
  @Help(help = "Delete the object of type {#} passing the id")
  public void delete(final String id) throws SDKException {
    requestDelete(id);
  }

  /**
   * Returns the list of the VNF software images available
   *
   * @return : The list of VNF software images available
   */
  @Help(help = "Find all the objects of type {#}")
  public List<T> findAll() throws SDKException, ClassNotFoundException {
    return Arrays.asList((T[]) requestGet(null, clazz));
  }

  /**
   * Returns the VNF software image selected by id
   *
   * @param id : The id of the VNF software image
   * @return image: The VNF software image selected
   */
  @Help(help = "Find the object of type {#} through the id")
  public T findById(final String id) throws SDKException, ClassNotFoundException {
    return (T) requestGet(id, clazz);
  }

  /**
   * Updates the VNF software object
   *
   * @param object : Image to add
   * @param id : the id of VNF software object
   * @return object: the VNF software object updated
   */
  @Help(help = "Update the object of type {#} passing the new object and the id of the old object")
  public T update(final T object, final String id) throws SDKException {
    return (T) requestPut(id, object);
  }
}
