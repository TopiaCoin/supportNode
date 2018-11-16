package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AbstractContainerIDTest {

    protected abstract ContainerID createNodeID();
    protected abstract ContainerID recreateNodeID(String nodeIDString);

    @Test
    public void testConstruction() throws Exception {
        ContainerID nodeID1 = createNodeID();

        String nodeIDString = nodeID1.stringValue();

        ContainerID nodeID2 = recreateNodeID(nodeIDString);

        assertEquals ( nodeID1, nodeID2);
    }
}
