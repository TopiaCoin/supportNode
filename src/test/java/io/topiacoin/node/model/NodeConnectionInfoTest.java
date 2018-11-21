package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class NodeConnectionInfoTest {

    @Test
    public void testAccessors() throws Exception {

        String nodeID = "foobarbaz";
        String rpcURL = "http://localhost:1234";

        NodeConnectionInfo info = new NodeConnectionInfo(nodeID, rpcURL);

        assertEquals(nodeID, info.getNodeID());
        assertEquals(rpcURL, info.getNodeURL());
    }

    @Test
    public void testDefaultConstructor() throws Exception {
        NodeConnectionInfo info = new NodeConnectionInfo();

        assertNull(info.getNodeID());
        assertNull(info.getNodeURL());
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {

        String nodeID = "foobarbaz";
        String nodeID2 = "fizzbuzz";
        String nodeURL = "http://localhost:1234";
        String nodeURL2 = "http://localhost:9876";

        NodeConnectionInfo info1 = new NodeConnectionInfo(nodeID, nodeURL);
        NodeConnectionInfo info2 = new NodeConnectionInfo(nodeID, nodeURL);

        NodeConnectionInfo info4 = new NodeConnectionInfo(nodeID2, nodeURL);
        NodeConnectionInfo info5 = new NodeConnectionInfo(nodeID, nodeURL2);
        NodeConnectionInfo info6 = new NodeConnectionInfo(nodeID2, nodeURL2);

        assertEquals(info1, info1);
        assertEquals(info1, info2);

        assertNotEquals(info1, info4);
        assertNotEquals(info1, info5);
        assertNotEquals(info1, info6);

        assertEquals(info1.hashCode(), info1.hashCode());
        assertEquals(info1.hashCode(), info2.hashCode());

        assertNotEquals(info1.hashCode(), info4.hashCode());
        assertNotEquals(info1.hashCode(), info5.hashCode());
        assertNotEquals(info1.hashCode(), info6.hashCode());

    }
}
