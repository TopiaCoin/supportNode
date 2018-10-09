package io.topiacoin.node.rest;

import org.junit.Test;

import static junit.framework.TestCase.*;

public class ContainerReplicationRequestTest {

    @Test
    public void testDefaultConstructor() throws Exception {

        ContainerReplicationRequest request = new ContainerReplicationRequest();

        assertNull(request.getContainerID());
        assertNull(request.getPeerNodeID());
    }

    @Test
    public void testAccessors() throws Exception {

        String containerID = "0xdeadbeef";
        String peerID = "fredflintstone";

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);

        assertEquals(containerID, request.getContainerID());
        assertEquals(peerID, request.getPeerNodeID());

    }

}
