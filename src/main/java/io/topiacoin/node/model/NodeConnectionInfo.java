package io.topiacoin.node.model;

import java.util.Objects;

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

    public NodeConnectionInfo(NodeConnectionInfo info) {
        this.containerID = info.containerID;
        this.nodeID = info.nodeID;
        this.rpcURL = info.rpcURL;
        this.p2pURL = info.p2pURL;
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

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public void setRpcURL(String rpcURL) {
        this.rpcURL = rpcURL;
    }

    public void setP2pURL(String p2pURL) {
        this.p2pURL = p2pURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeConnectionInfo that = (NodeConnectionInfo) o;
        return Objects.equals(containerID, that.containerID) &&
                Objects.equals(nodeID, that.nodeID) &&
                Objects.equals(rpcURL, that.rpcURL) &&
                Objects.equals(p2pURL, that.p2pURL);
    }

    @Override
    public int hashCode() {

        return Objects.hash(containerID, nodeID, rpcURL, p2pURL);
    }

    @Override
    public String toString() {
        return "NodeConnectionInfo{" +
                "containerID='" + containerID + '\'' +
                ", nodeID='" + nodeID + '\'' +
                ", rpcURL='" + rpcURL + '\'' +
                ", p2pURL='" + p2pURL + '\'' +
                '}';
    }
}
