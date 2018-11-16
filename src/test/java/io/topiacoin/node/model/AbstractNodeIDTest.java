package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AbstractNodeIDTest {

    protected abstract NodeID createNodeID();
    protected abstract NodeID recreateNodeID(String nodeIDString);

    @Test
    public void testConstruction() throws Exception {
        NodeID nodeID1 = createNodeID();

        String nodeIDString = nodeID1.stringValue();

        NodeID nodeID2 = recreateNodeID(nodeIDString);

        assertEquals ( nodeID1, nodeID2);
    }
}
