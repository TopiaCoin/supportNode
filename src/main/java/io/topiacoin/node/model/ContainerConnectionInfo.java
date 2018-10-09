package io.topiacoin.node.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainerConnectionInfo that = (ContainerConnectionInfo) o;
        return Objects.equals(containerID, that.containerID) &&
                Objects.equals(connectionURL, that.connectionURL) &&
                Objects.equals(p2pURL, that.p2pURL);
    }

    @Override
    public int hashCode() {

        return Objects.hash(containerID, connectionURL, p2pURL);
    }

    @Override
    public String toString() {
        return "ContainerConnectionInfo{" +
                "containerID='" + containerID + '\'' +
                ", connectionURL='" + connectionURL + '\'' +
                ", p2pURL='" + p2pURL + '\'' +
                '}';
    }
}
