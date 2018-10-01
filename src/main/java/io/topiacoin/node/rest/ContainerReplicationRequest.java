package io.topiacoin.node.rest;

public class ContainerReplicationRequest {

    public String containerID;
    public String peerNodeID;

    @Override
    public String toString() {
        return "ContainerReplicationRequest{" +
                "containerID='" + containerID + '\'' +
                ", peerNodeID='" + peerNodeID + '\'' +
                '}';
    }
}
