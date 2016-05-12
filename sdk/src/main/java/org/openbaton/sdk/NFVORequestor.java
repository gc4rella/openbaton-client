package org.openbaton.sdk;



import org.openbaton.sdk.api.rest.*;
import org.openbaton.sdk.api.util.AbstractRestAgent;

/**
 * OpenBaton api requestor. Can be extended with security features to provide instances only only to granted requestors.
 * The Class is implemented in a static way to avoid any dependencies to spring and to create a corresponding small lib size.
 */
public final class NFVORequestor {


    private static RequestFactory factory;

    public NFVORequestor(String version){
        this.factory = RequestFactory.getInstance(null,null, "localhost", "8080", version);
    }

    /**
     * The public constructor taking as params
     * @param username
     * @param password
     * @param version
     */
    public NFVORequestor(String username, String password, String version) {
        this.factory = RequestFactory.getInstance(username,password, "localhost", "8080", version);
    }

    public NFVORequestor(String username, String password, String nfvoIp, String nfvoPort, String version) {
        this.factory = RequestFactory.getInstance(username,password, nfvoIp, nfvoPort, version);
    }

    /**
     * Gets the configuration requester
     *
     * @return configurationRequest: The (final) static configuration requester
     */
    public ConfigurationRestRequest getConfigurationAgent() {
        return factory.getConfigurationAgent();
    }

    /**
     * Gets the image requester
     *
     * @return image: The (final) static image requester
     */
    public ImageRestAgent getImageAgent() {
        return factory.getImageAgent();
    }

    /**
     * Gets the networkServiceDescriptor requester
     *
     * @return networkServiceDescriptorRequest: The (final) static networkServiceDescriptor requester
     */
    public NetworkServiceDescriptorRestAgent getNetworkServiceDescriptorAgent() {
        return factory.getNetworkServiceDescriptorAgent();
    }

    /**
     * Gets the networkServiceDescriptor requester
     *
     * @return networkServiceDescriptorRequest: The (final) static networkServiceDescriptor requester
     */
    public VirtualNetworkFunctionDescriptorRestAgent getVirtualNetworkFunctionDescriptorAgent() {
        return factory.getVirtualNetworkFunctionDescriptorAgent();
    }

    /**
     * Gets the networkServiceRecord requester
     *
     * @return networkServiceRecordRequest: The (final) static networkServiceRecord requester
     */

    public NetworkServiceRecordRestAgent getNetworkServiceRecordAgent() {
        return factory.getNetworkServiceRecordAgent();
    }

    /**
     * Gets the vimInstance requester
     *
     * @return vimInstanceRequest: The (final) static vimInstance requester
     */
    public VimInstanceRestAgent getVimInstanceAgent() {
        return factory.getVimInstanceAgent();
    }

    /**
     * Gets the virtualLink requester
     *
     * @return virtualLinkRequest: The (final) static virtualLink requester
     */
    public VirtualLinkRestAgent getVirtualLinkAgent() {
        return factory.getVirtualLinkAgent();
    }

    /**
     * Gets the VirtualNetworkFunctionDescriptor requester
     *
     * @return vnfdRequest; The (final) static VirtualNetworkFunctionDescriptor requester
     */
    public VirtualNetworkFunctionDescriptorRestAgent getVirtualNetworkFunctionDescriptorRestAgent() {
        return factory.getVirtualNetworkFunctionDescriptorAgent();
    }

    /**
     * Gets the VNFFG requester
     *
     * @return vNFFGRequest: The (final) static vNFFG requester
     */
    public VNFFGRestAgent getVNFFGAgent() {
        return factory.getVNFForwardingGraphAgent();
    }

    /**
     * Gets the Event requester
     *
     * @return eventRequest; The (final) static Event requester
     */
    public EventAgent getEventAgent() {
        return factory.getEventAgent();
    }

    /**
     * Gets the VNFPackage requester
     *
     * @return vnfPackageRequest; The (final) static VNFPackage requester
     */
    public VNFPackageAgent getVNFPackageAgent() {
        return factory.getVNFPackageAgent();
    }

    public AbstractRestAgent abstractRestAgent(Class clazz, String path){
        return factory.getAbstractAgent(clazz, path);
    }
}
