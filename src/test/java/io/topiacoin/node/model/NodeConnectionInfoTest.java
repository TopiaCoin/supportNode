package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class NodeConnectionInfoTest {

    @Test
    public void testAccessors() throws Exception {

        String containerID = "1234";
        String nodeID = "foobarbaz";
        String rpcURL = "http://localhost:1234";
        String p2pURL = "http://localhost:2345";

        NodeConnectionInfo info = new NodeConnectionInfo(containerID, nodeID, rpcURL, p2pURL);

        assertEquals(containerID, info.getContainerID());
        assertEquals(nodeID, info.getNodeID());
        assertEquals(rpcURL, info.getRpcURL());
        assertEquals(p2pURL, info.getP2PURL());
    }

    @Test
    public void testDefaultConstructor() throws Exception {
        NodeConnectionInfo info = new NodeConnectionInfo();

        assertNull(info.getContainerID());
        assertNull(info.getNodeID());
        assertNull(info.getRpcURL());
        assertNull(info.getP2PURL());
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {

        String containerID = "1234";
        String containerID2 = "45678";
        String nodeID = "foobarbaz";
        String nodeID2 = "fizzbuzz";
        String rpcURL = "http://localhost:1234";
        String rpcURL2 = "http://localhost:9876";
        String p2pURL = "http://localhost:2345";
        String p2pURL2 = "http://localhost:8765";

        NodeConnectionInfo info1 = new NodeConnectionInfo(containerID, nodeID, rpcURL, p2pURL);
        NodeConnectionInfo info2 = new NodeConnectionInfo(containerID, nodeID, rpcURL, p2pURL);

        NodeConnectionInfo info3 = new NodeConnectionInfo(containerID2, nodeID, rpcURL, p2pURL);
        NodeConnectionInfo info4 = new NodeConnectionInfo(containerID, nodeID2, rpcURL, p2pURL);
        NodeConnectionInfo info5 = new NodeConnectionInfo(containerID, nodeID, rpcURL2, p2pURL);
        NodeConnectionInfo info6 = new NodeConnectionInfo(containerID, nodeID, rpcURL, p2pURL2);

        assertEquals(info1, info1);
        assertEquals(info1, info2);

        assertNotEquals(info1, info3);
        assertNotEquals(info1, info4);
        assertNotEquals(info1, info5);
        assertNotEquals(info1, info6);

        assertEquals(info1.hashCode(), info1.hashCode());
        assertEquals(info1.hashCode(), info2.hashCode());

        assertNotEquals(info1.hashCode(), info3.hashCode());
        assertNotEquals(info1.hashCode(), info4.hashCode());
        assertNotEquals(info1.hashCode(), info5.hashCode());
        assertNotEquals(info1.hashCode(), info6.hashCode());

    }
}
