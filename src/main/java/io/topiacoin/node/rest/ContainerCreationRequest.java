package io.topiacoin.node.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

public class ContainerCreationRequest {
    @JsonProperty
    private String containerID;

    public ContainerCreationRequest() {
    }

    public ContainerCreationRequest(String containerID) {
        this.containerID = containerID;
    }

    public String getContainerID() {
        return containerID;
    }

    @Override
    public String toString() {
        return "ContainerCreationRequest{" +
                "containerID='" + containerID + '\'' +
                '}';
    }
}
