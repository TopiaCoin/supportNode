package io.topiacoin.node.micronetwork;

import io.topiacoin.node.model.ContainerConnectionInfo;
import io.topiacoin.node.model.ContainerInfo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Future;

public class ContainerManager {

    @PostConstruct
    public void initialize() throws Exception {

    }

    @PreDestroy
    public void shutdown() throws Exception {

    }

    public ContainerInfo getContainer(String containerID) {
        return null;
    }

    public boolean hasContainer(String containerID) {
        return false;
    }

    public ContainerConnectionInfo getContainerConnectionInfo(String containerID) {
        return null;
    }

    // TODO - Create the ContainerState Enum
    public /*ContainerState*/ String getContainerState(String containerID) {
        return null;
    }

    public Future<ContainerConnectionInfo> createContainer(String containerID) {
        return null;
    }

    public Future<ContainerConnectionInfo> replicateContainer(String containerID, String peerID) {
        return null;
    }

    public Future<Void> removeContainer(String containerID) {
        return null;
    }

}
