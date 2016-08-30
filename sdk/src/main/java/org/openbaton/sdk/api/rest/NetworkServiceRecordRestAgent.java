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

import com.google.gson.JsonObject;
import org.openbaton.catalogue.mano.descriptor.VNFComponent;
import org.openbaton.catalogue.mano.record.*;
import org.openbaton.catalogue.nfvo.Configuration;
import org.openbaton.catalogue.nfvo.messages.Interfaces.NFVMessage;
import org.openbaton.sdk.api.annotations.Help;
import org.openbaton.sdk.api.exception.SDKException;
import org.openbaton.sdk.api.util.AbstractRestAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * OpenBaton image-related commands api requester.
 */
public class NetworkServiceRecordRestAgent extends AbstractRestAgent<NetworkServiceRecord> {

  /**
   * Create a NetworkServiceRecord requester with a given url path
   *
   * @param path the url path used for the api requests
   */
  public NetworkServiceRecordRestAgent(
      String username,
      String password,
      String projectId,
      boolean sslEnabled,
      String nfvoIp,
      String nfvoPort,
      String path,
      String version) {
    super(
        username,
        password,
        projectId,
        sslEnabled,
        nfvoIp,
        nfvoPort,
        path,
        version,
        NetworkServiceRecord.class);
  }

  @Help(help = "Create NetworkServiceRecord from NetworkServiceDescriptor id")
  public NetworkServiceRecord create(
      final String id,
      HashMap<String, ArrayList<String>> vduVimInstances,
      ArrayList<String> keys,
      HashMap<String, Configuration> configurations)
      throws SDKException {
    HashMap<String, Serializable> jsonBody = new HashMap<>();
    jsonBody.put("keys", keys);
    jsonBody.put("vduVimInstances", vduVimInstances);
    jsonBody.put("configurations", configurations);
    return (NetworkServiceRecord) this.requestPost("/" + id, jsonBody, NetworkServiceRecord.class);
  }

  /**
   *
   */
  @Help(help = "Get all the VirtualNetworkFunctionRecords of NetworkServiceRecord with specific id")
  public List<VirtualNetworkFunctionRecord> getVirtualNetworkFunctionRecords(final String id)
      throws SDKException {
    String url = id + "/vnfrecords";
    return Arrays.asList(
        (VirtualNetworkFunctionRecord[]) requestGetAll(url, VirtualNetworkFunctionRecord.class));
  }

  /**
   *
   */
  @Help(help = "Get the VirtualNetworkFunctionRecord of NetworkServiceRecord with specific id")
  public VirtualNetworkFunctionRecord getVirtualNetworkFunctionRecord(
      final String id, final String id_vnf) throws SDKException {
    String url = id + "/vnfrecords" + "/" + id_vnf;
    return (VirtualNetworkFunctionRecord) requestGet(url, VirtualNetworkFunctionRecord.class);
  }

  /**
   *
   */
  @Help(help = "Delete the VirtualNetworkFunctionRecord of NetworkServiceRecord with specific id")
  public void deleteVirtualNetworkFunctionRecord(final String id, final String id_vnf)
      throws SDKException {
    String url = id + "/vnfrecords" + "/" + id_vnf;
    requestDelete(url);
  }

  @Help(help = "create VNFCInstance in standby")
  public void createVNFCInstanceInStandby(
      final String idNSR, final String idVNF, final String idVdu, final VNFComponent component)
      throws SDKException {
    String url = idNSR + "/vnfrecords/" + idVNF + "/vdunits/" + idVdu + "/vnfcinstances/standby";
    requestPost(url, component);
  }

  @Help(help = "switch to standby")
  public void switchToStandby(
      final String idNSR,
      final String idVNF,
      final String idVdu,
      final String idVnfc,
      final VNFCInstance failedVnfcInstance)
      throws SDKException {
    String url =
        idNSR
            + "/vnfrecords/"
            + idVNF
            + "/vdunits/"
            + idVdu
            + "/vnfcinstances/"
            + idVnfc
            + "/switchtostandby";
    requestPost(url, failedVnfcInstance);
  }

  // Actually only HEAL action is supported
  @Help(help = "Execute a specific action specified in the nfvMessage")
  public void postAction(
      final String idNSR,
      final String idVNF,
      final String idVdu,
      final String idVnfc,
      final NFVMessage nfvMessage)
      throws SDKException {
    String url =
        idNSR
            + "/vnfrecords/"
            + idVNF
            + "/vdunits/"
            + idVdu
            + "/vnfcinstances/"
            + idVnfc
            + "/actions";
    requestPost(url, nfvMessage);
  }

  /**
   * TODO (check the orchestrator)
   */
  @Help(help = "create VirtualNetworkFunctionRecord")
  public VirtualNetworkFunctionRecord createVNFR(
      final String idNSR, final VirtualNetworkFunctionRecord virtualNetworkFunctionRecord)
      throws SDKException {
    String url = idNSR + "/vnfrecords";
    return (VirtualNetworkFunctionRecord) requestPost(url, virtualNetworkFunctionRecord);
  }

  @Help(help = "create VNFCInstance. Aka SCALE OUT")
  public void createVNFCInstance(
      final String idNSR, final String idVNF, final VNFComponent component) throws SDKException {
    String url = idNSR + "/vnfrecords/" + idVNF + "/vdunits/vnfcinstances";
    requestPost(url, component);
  }

  @Help(help = "create VNFCInstance. Aka SCALE OUT")
  public void createVNFCInstance(
      final String idNSR, final String idVNF, final String idVDU, final VNFComponent component)
      throws SDKException {
    String url = idNSR + "/vnfrecords/" + idVNF + "/vdunits/" + idVDU + "/vnfcinstances";
    requestPost(url, component);
  }

  @Help(help = "remove VNFCInstance. Aka SCALE IN")
  public void deleteVNFCInstance(final String idNSR, final String idVNF) throws SDKException {
    String url = idNSR + "/vnfrecords/" + idVNF + "/vdunits/vnfcinstances";
    requestDelete(url);
  }

  @Help(help = "remove VNFCInstance. Aka SCALE IN")
  public void deleteVNFCInstance(final String idNSR, final String idVNF, final String idVDU)
      throws SDKException {
    String url = idNSR + "/vnfrecords/" + idVNF + "/vdunits/" + idVDU + "/vnfcinstances";
    requestDelete(url);
  }

  @Help(help = "remove VNFCInstance. Aka SCALE IN")
  public void deleteVNFCInstance(
      final String idNSR, final String idVNF, final String idVDU, final String idVNFCInstance)
      throws SDKException {
    String url =
        idNSR + "/vnfrecords/" + idVNF + "/vdunits/" + idVDU + "/vnfcinstances/" + idVNFCInstance;
    requestDelete(url);
  }

  /**
   * TODO (check the orchestrator)
   */
  @Help(help = "update VirtualNetworkFunctionRecord")
  public String updateVNFR(
      final String idNSR,
      final String id_vnfr,
      final VirtualNetworkFunctionRecord virtualNetworkFunctionRecord)
      throws SDKException {
    String url = idNSR + "/vnfrecords" + "/" + id_vnfr;
    return requestPut(url, virtualNetworkFunctionRecord).toString();
  }

  /**
   *
   */
  @Help(
    help =
        "Get all the VirtualNetworkFunctionRecord dependencies of NetworkServiceRecord with specific id"
  )
  public List<VNFRecordDependency> getVNFDependencies(final String idNSR) throws SDKException {
    String url = idNSR + "/vnfdependencies";
    return Arrays.asList((VNFRecordDependency[]) requestGetAll(url, VNFRecordDependency.class));
  }

  /**
   *
   */
  @Help(
    help =
        "Get the VirtualNetworkFunctionRecord Dependency of a NetworkServiceRecord with specific id"
  )
  public VNFRecordDependency getVNFDependency(final String idNSR, final String id_vnfrdep)
      throws SDKException {
    String url = idNSR + "/vnfdependencies" + "/" + id_vnfrdep;
    return (VNFRecordDependency) requestGet(url, VNFRecordDependency.class);
  }

  /**
   *
   */
  @Help(
    help =
        "Delete the VirtualNetworkFunctionRecord Dependency of a NetworkServiceRecord with specific id"
  )
  public void deleteVNFDependency(final String idNSR, final String id_vnfd) throws SDKException {
    String url = idNSR + "/vnfdependencies" + "/" + id_vnfd;
    requestDelete(url);
  }

  /**
   * TODO (check the orchestrator)
   */
  @Help(
    help =
        "Create the VirtualNetworkFunctionRecord Dependency of a NetworkServiceRecord with specific id"
  )
  public VNFRecordDependency postVNFDependency(
      final String idNSR, final VNFRecordDependency vnfDependency) throws SDKException {
    String url = idNSR + "/vnfdependencies" + "/";
    return (VNFRecordDependency) requestPost(url, vnfDependency);
  }

  /**
   *
   */
  @Help(
    help =
        "Update the VirtualNetworkFunctionRecord Dependency of a NetworkServiceRecord with specific id"
  )
  public VNFRecordDependency updateVNFDependency(
      final String idNSR, final String id_vnfrDep, final VNFRecordDependency vnfDependency)
      throws SDKException {
    String url = idNSR + "/vnfdependencies" + "/" + id_vnfrDep;
    return (VNFRecordDependency) requestPut(url, vnfDependency);
  }

  /**
   * Returns the set of PhysicalNetworkFunctionRecord into a NSD with id
   *
   * @param idNSR : The id of NSD
   * @return : The Set of PhysicalNetworkFunctionRecord into NSD
   */
  @Help(
    help = "Get all the PhysicalNetworkFunctionRecords of a specific NetworkServiceRecord with id"
  )
  public List<PhysicalNetworkFunctionRecord> getPhysicalNetworkFunctionRecords(final String idNSR)
      throws SDKException {
    String url = idNSR + "/pnfrecords";
    return Arrays.asList(
        (PhysicalNetworkFunctionRecord[]) requestGetAll(url, PhysicalNetworkFunctionRecord.class));
  }

  /**
   * Returns the PhysicalNetworkFunctionRecord
   *
   * @param idNSR : The NSD id
   * @param idPnf The PhysicalNetworkFunctionRecord id
   * @return PhysicalNetworkFunctionRecord: The PhysicalNetworkFunctionRecord selected
   */
  @Help(help = "Get the PhysicalNetworkFunctionRecord of a NetworkServiceRecord with specific id")
  public PhysicalNetworkFunctionRecord getPhysicalNetworkFunctionRecord(
      final String idNSR, final String idPnf) throws SDKException {
    String url = idNSR + "/pnfrecords" + "/" + idPnf;
    return (PhysicalNetworkFunctionRecord)
        requestGetWithStatusAccepted(url, PhysicalNetworkFunctionRecord.class);
  }

  /**
   * Deletes the PhysicalNetworkFunctionRecord with the idPnf
   *
   * @param idNSR The NSD id
   * @param idPnf The PhysicalNetworkFunctionRecord id
   */
  @Help(
    help = "Delete the PhysicalNetworkFunctionRecord of a NetworkServiceRecord with specific id"
  )
  public void deletePhysicalNetworkFunctionRecord(final String idNSR, final String idPnf)
      throws SDKException {
    String url = idNSR + "/pnfrecords" + "/" + idPnf;
    requestDelete(url);
  }

  /**
   * Stores the PhysicalNetworkFunctionRecord
   *
   * @param physicalNetworkFunctionRecord : The PhysicalNetworkFunctionRecord to be stored
   * @param idNSR : The NSD id
   * @return PhysicalNetworkFunctionRecord: The PhysicalNetworkFunctionRecord stored
   */
  @Help(
    help = "Create the PhysicalNetworkFunctionRecord of a NetworkServiceRecord with specific id"
  )
  public PhysicalNetworkFunctionRecord postPhysicalNetworkFunctionRecord(
      final String idNSR, final PhysicalNetworkFunctionRecord physicalNetworkFunctionRecord)
      throws SDKException {
    String url = idNSR + "/pnfrecords" + "/";
    return (PhysicalNetworkFunctionRecord) requestPost(url, physicalNetworkFunctionRecord);
  }

  /**
   * Edits the PhysicalNetworkFunctionRecord
   *
   * @param physicalNetworkFunctionRecord : The PhysicalNetworkFunctionRecord to be edited
   * @param idNSR : The NSD id
   * @return PhysicalNetworkFunctionRecord: The PhysicalNetworkFunctionRecord edited
   */
  @Help(
    help = "Update the PhysicalNetworkFunctionRecord of a NetworkServiceRecord with specific id"
  )
  public PhysicalNetworkFunctionRecord updatePNFD(
      final String idNSR,
      final String idPnf,
      final PhysicalNetworkFunctionRecord physicalNetworkFunctionRecord)
      throws SDKException {
    String url = idNSR + "/pnfrecords" + "/" + idPnf;
    return (PhysicalNetworkFunctionRecord) requestPut(url, physicalNetworkFunctionRecord);
  }
}
