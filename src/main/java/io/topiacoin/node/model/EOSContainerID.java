package io.topiacoin.node.model;

import java.util.Objects;
import java.util.UUID;

public class EOSContainerID extends ContainerID {

    private long containerIDValue;

    /**
     * Constructs a new Randomly generated NodeID.
     */
    public EOSContainerID() {
        containerIDValue = UUID.randomUUID().getLeastSignificantBits();
    }

    /**
     * Constructs a new Node ID object from the specified String.
     *
     * @param containerID
     */
    public EOSContainerID(String containerID) {
        this.containerIDValue = Long.valueOf(containerID, 16);
    }

    public EOSContainerID(long containerIDValue) {
        this.containerIDValue = containerIDValue;
    }

    public String stringValue() {
        return Long.toString(this.containerIDValue, 16);
    }

    public long longValue() {
        return containerIDValue;
    }

    // -------- equals, hashCode, toString --------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EOSContainerID eosNodeID = (EOSContainerID) o;
        return Objects.equals(containerIDValue, eosNodeID.containerIDValue);
    }

    @Override
    public int hashCode() {

        return Objects.hash(containerIDValue);
    }

    @Override
    public String toString() {
        return "EOSContainerID{" +
                containerIDValue +
                '}';
    }
}
