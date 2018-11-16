package io.topiacoin.node.model;

import org.apache.commons.lang.NotImplementedException;

public abstract class ContainerID {

    /**
     * Constructs a new Randomly generated ContainerID.
     */
    public ContainerID() {
        // NOOP - Expected subclasses to override
    }

    /**
     * Constructs a new Container ID object from the specified String.
     *
     * @param containerID
     */
    public ContainerID(String containerID) {
        throw new NotImplementedException("Subclasses must override this method with a proper implementation");
    }

    public abstract String stringValue() ;

}
