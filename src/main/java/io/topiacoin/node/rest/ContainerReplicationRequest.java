package io.topiacoin.node.rest;

public class ContainerReplicationRequest {

    private String containerID;
    private String peerNodeID;

    public ContainerReplicationRequest() {
    }

    public ContainerReplicationRequest(String containerID, String peerNodeID) {
        this.containerID = containerID;
        this.peerNodeID = peerNodeID;
    }

    public String getContainerID() {
        return containerID;
    }

    public String getPeerNodeID() {
        return peerNodeID;
    }

    @Override
    public String toString() {
        return "ContainerReplicationRequest{" +
                "containerID='" + containerID + '\'' +
                ", peerNodeID='" + peerNodeID + '\'' +
                '}';
    }
}
