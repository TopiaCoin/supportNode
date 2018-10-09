package io.topiacoin.node.rest;

import org.junit.Test;

import static junit.framework.TestCase.*;

public class ContainerCreationRequestTest {

    @Test
    public void testDefaultConstructor() throws Exception {

        ContainerCreationRequest request = new ContainerCreationRequest();

        assertNull(request.getContainerID());
    }

    @Test
    public void testAccessors() throws Exception {

        String containerID = "0xdeadbeef";

        ContainerCreationRequest request = new ContainerCreationRequest(containerID);

        assertEquals(containerID, request.getContainerID());

    }
}
