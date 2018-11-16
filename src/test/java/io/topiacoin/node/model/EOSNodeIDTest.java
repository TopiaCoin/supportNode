package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class EOSNodeIDTest extends AbstractNodeIDTest {
    @Override
    protected NodeID createNodeID() {
        return new EOSNodeID();
    }

    @Override
    protected NodeID recreateNodeID(String nodeString) {
        return new EOSNodeID(nodeString);
    }

    @Test
    public void testRecreateFromLong() throws Exception{

        EOSNodeID nodeID1 = new EOSNodeID();

        long nodeIDValue = nodeID1.longValue();

        EOSNodeID nodeID2 = new EOSNodeID(nodeIDValue);

        assertEquals ( nodeID1, nodeID2);

    }
}
