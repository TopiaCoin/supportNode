package io.topiacoin.node.model;

import org.apache.commons.lang.NotImplementedException;

public abstract class NodeID {

    /**
     * Constructs a new Randomly generated NodeID.
     */
    public NodeID() {
        // NOOP - Expected subclasses to override
    }

    /**
     * Constructs a new Node ID object from the specified String.
     *
     * @param nodeID
     */
    public NodeID(String nodeID) {
        throw new NotImplementedException("Subclasses must override this method with a proper implementation");
    }

    public abstract String stringValue() ;
}
