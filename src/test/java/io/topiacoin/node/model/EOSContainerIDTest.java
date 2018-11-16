package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class EOSContainerIDTest extends AbstractContainerIDTest {
    @Override
    protected ContainerID createNodeID() {
        return new EOSContainerID();
    }

    @Override
    protected ContainerID recreateNodeID(String nodeString) {
        return new EOSContainerID(nodeString);
    }

    @Test
    public void testRecreateFromLong() throws Exception{

        EOSContainerID nodeID1 = new EOSContainerID();

        long nodeIDValue = nodeID1.longValue();

        EOSContainerID nodeID2 = new EOSContainerID(nodeIDValue);

        assertEquals ( nodeID1, nodeID2);

    }
}
