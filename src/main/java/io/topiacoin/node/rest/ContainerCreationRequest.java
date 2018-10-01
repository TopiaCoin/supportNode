package io.topiacoin.node.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

public class ContainerCreationRequest {
    @JsonProperty
    public String containerID;

    @Override
    public String toString() {
        return "ContainerCreationRequest{" +
                "containerID='" + containerID + '\'' +
                '}';
    }
}
