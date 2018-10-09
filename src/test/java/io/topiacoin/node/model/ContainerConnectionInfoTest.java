package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ContainerConnectionInfoTest {

    @Test
    public void testAccessors()  throws Exception {

        String containerID = "foo";
        String rpcURL = "http://localhost:1234";
        String p2pURL = "http://localhost:2345";

        ContainerConnectionInfo info = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        assertEquals ( containerID, info.getContainerID());
        assertEquals(rpcURL, info.getConnectionURL());
        assertEquals(p2pURL, info.getP2PURL());
    }

    @Test
    public void testDefaultConstructor() throws Exception {

        ContainerConnectionInfo info = new ContainerConnectionInfo();

        assertNull(info.getContainerID());
        assertNull(info.getConnectionURL());
        assertNull(info.getP2PURL());
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {

        String containerID = "foo";
        String containerID2 = "bar";
        String rpcURL = "http://localhost:1234";
        String rpcURL2 = "http://localhost:9876";
        String p2pURL = "http://localhost:2345";
        String p2pURL2 = "http://localhost:8675";

        ContainerConnectionInfo info1 = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);
        ContainerConnectionInfo info2 = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        ContainerConnectionInfo info3 = new ContainerConnectionInfo(containerID2, rpcURL, p2pURL);
        ContainerConnectionInfo info4 = new ContainerConnectionInfo(containerID, rpcURL2, p2pURL);
        ContainerConnectionInfo info5 = new ContainerConnectionInfo(containerID, rpcURL, p2pURL2);

        assertEquals(info1, info1);
        assertEquals(info1, info2);

        assertNotEquals(info1, info3);
        assertNotEquals(info1, info4);
        assertNotEquals(info1, info5);

        assertEquals(info1.hashCode(), info1.hashCode());
        assertEquals(info1.hashCode(), info2.hashCode());

        assertNotEquals(info1.hashCode(), info3.hashCode());
        assertNotEquals(info1.hashCode(), info4.hashCode());
        assertNotEquals(info1.hashCode(), info5.hashCode());
    }
}
