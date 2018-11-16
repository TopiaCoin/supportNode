package io.topiacoin.node.model;

import java.util.Objects;
import java.util.UUID;

public class EOSNodeID extends NodeID {

    private long nodeIDValue;

    /**
     * Constructs a new Randomly generated NodeID.
     */
    public EOSNodeID() {
        nodeIDValue = UUID.randomUUID().getLeastSignificantBits();
    }

    /**
     * Constructs a new Node ID object from the specified String.
     *
     * @param nodeID
     */
    public EOSNodeID(String nodeID) {
        this.nodeIDValue = Long.valueOf(nodeID, 16);
    }

    public EOSNodeID(long nodeIDValue) {
        this.nodeIDValue = nodeIDValue;
    }

    public String stringValue() {
        return Long.toString(this.nodeIDValue, 16);
    }

    public long longValue() {
        return nodeIDValue;
    }

    // -------- equals, hashCode, toString --------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EOSNodeID eosNodeID = (EOSNodeID) o;
        return Objects.equals(nodeIDValue, eosNodeID.nodeIDValue);
    }

    @Override
    public int hashCode() {

        return Objects.hash(nodeIDValue);
    }

    @Override
    public String toString() {
        return "EOSNodeID{" +
                nodeIDValue +
                '}';
    }
}
