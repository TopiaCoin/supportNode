package io.topiacoin.node.model;

public class ContainerConnectionInfo {

    private String containerID;
    private String connectionURL;
    private String p2pURL;

    public ContainerConnectionInfo() {
    }

    public ContainerConnectionInfo(String containerID, String connectionURl, String p2pURL) {
        this.containerID = containerID;
        this.connectionURL = connectionURl;
        this.p2pURL = p2pURL;
    }

    public String getContainerID() {
        return containerID;
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public String getP2PURL() {
        return p2pURL;
    }
}
