package io.topiacoin.node.model;

public class NodeConnectionInfo {

    private String containerID;
    private String nodeID;
    private String rpcURL;
    private String p2pURL;

    public NodeConnectionInfo() {
    }

    public NodeConnectionInfo(String containerID, String nodeID, String rpcURL, String p2pURL) {
        this.containerID = containerID;
        this.nodeID = nodeID;
        this.rpcURL = rpcURL;
        this.p2pURL = p2pURL;
    }

    public String getContainerID() {
        return containerID;
    }

    public String getNodeID() {
        return nodeID;
    }

    public String getRpcURL() {
        return rpcURL;
    }

    public String getP2PURL() {
        return p2pURL;
    }
}
