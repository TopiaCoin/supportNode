package io.topiacoin.node.model;

import java.util.Objects;

public class NodeConnectionInfo {

    private String nodeID;
    private String nodeURL;

    public NodeConnectionInfo() {
    }

    public NodeConnectionInfo(String nodeID, String nodeURL) {
        this.nodeID = nodeID;
        this.nodeURL = nodeURL;
    }

    public NodeConnectionInfo(NodeConnectionInfo info) {
        this.nodeID = info.nodeID;
        this.nodeURL = info.nodeURL;
    }

    public String getNodeID() {
        return nodeID;
    }

    public String getNodeURL() {
        return nodeURL;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public void setNodeURL(String nodeURL) {
        this.nodeURL = nodeURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeConnectionInfo that = (NodeConnectionInfo) o;
        return Objects.equals(nodeID, that.nodeID) &&
                Objects.equals(nodeURL, that.nodeURL);
    }

    @Override
    public int hashCode() {

        return Objects.hash(nodeID, nodeURL);
    }

    @Override
    public String toString() {
        return "NodeConnectionInfo{" +
                "nodeID='" + nodeID + '\'' +
                ", nodeURL='" + nodeURL + '\'' +
                '}';
    }
}
