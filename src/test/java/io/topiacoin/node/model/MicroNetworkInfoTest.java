package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class MicroNetworkInfoTest {

	@Test
	public void testDefaultConstructor() throws Exception {
		MicroNetworkInfo info = new MicroNetworkInfo();

		assertNull(info.getId());
		assertNull(info.getContainerID());
		assertNull(info.getPath());
		assertNull(info.getRpcURL());
		assertNull(info.getP2pURL());
		assertEquals(null, info.getState());
	}

	@Test
	public void testConstructor() throws Exception {

		String id = "abc-123";
		String containerID = "def-456";
		String path = "whatever";
		String rpcURL = "rpcURL";
		String p2pURL = "p2pURL";
		MicroNetworkState state = MicroNetworkState.STARTING;

		MicroNetworkInfo info = new MicroNetworkInfo(id, containerID, path, state, rpcURL, p2pURL);

		assertEquals(id, info.getId());
		assertEquals(containerID, info.getContainerID());
		assertEquals(path, info.getPath());
		assertEquals(rpcURL, info.getRpcURL());
		assertEquals(p2pURL, info.getP2pURL());
		assertEquals(state, info.getState());
	}

	@Test
	public void testBasicAccessors() throws Exception {

		String id = "abc-123";
		String containerID = "def-456";
		String path = "whatever";
		String rpcURL = "rpcURL";
		String p2pURL = "p2pURL";
		MicroNetworkState state = MicroNetworkState.STARTING;

		MicroNetworkInfo info = new MicroNetworkInfo();

		assertNull(info.getId());
		info.setId(id);
		assertEquals(id, info.getId());
		info.setId(null);
		assertNull(info.getId());

		assertNull(info.getContainerID());
		info.setContainerID(containerID);
		assertEquals(containerID, info.getContainerID());
		info.setContainerID(null);
		assertNull(info.getContainerID());

		assertNull(info.getPath());
		info.setPath(path);
		assertEquals(path, info.getPath());
		info.setPath(null);
		assertNull(info.getPath());

		assertNull(info.getRpcURL());
		info.setRpcURL(rpcURL);
		assertEquals(rpcURL, info.getRpcURL());
		info.setRpcURL(null);
		assertNull(info.getRpcURL());

		assertNull(info.getP2pURL());
		info.setP2pURL(p2pURL);
		assertEquals(p2pURL, info.getP2pURL());
		info.setP2pURL(null);
		assertNull(info.getP2pURL());

		assertNull(info.getState());
		info.setState(state);
		assertEquals(state, info.getState());
		info.setState(null);
		assertNull(info.getState());
	}

	@Test
	public void testEqualsAndHashCode() throws Exception {

		String id = "abc-123";
		String id2 = "efg-456";
		String containerID = "def-456";
		String containerID2 = "zyx-987";
		String path = "whatever";
		String path2 = "whoever";
		String rpcURL = "rpcURL";
		String rpcURL2 = "rpcURL2";
		String p2pURL = "p2pURL";
		String p2pURL2 = "p2pURL2";
		MicroNetworkState state = MicroNetworkState.STARTING;
		MicroNetworkState state2 = MicroNetworkState.RUNNING;

		MicroNetworkInfo info1 = new MicroNetworkInfo(id, containerID, path, state, rpcURL, p2pURL);
		MicroNetworkInfo info2 = new MicroNetworkInfo(id, containerID, path, state, rpcURL, p2pURL);

		MicroNetworkInfo info3 = new MicroNetworkInfo(id2, containerID, path, state, rpcURL, p2pURL);
		MicroNetworkInfo info4 = new MicroNetworkInfo(id, containerID2, path, state, rpcURL, p2pURL);
		MicroNetworkInfo info5 = new MicroNetworkInfo(id, containerID, path2, state, rpcURL, p2pURL);
		MicroNetworkInfo info6 = new MicroNetworkInfo(id, containerID, path, state2, rpcURL, p2pURL);
		MicroNetworkInfo info7 = new MicroNetworkInfo(id, containerID, path, state, rpcURL2, p2pURL);
		MicroNetworkInfo info8 = new MicroNetworkInfo(id, containerID, path, state, rpcURL, p2pURL2);

		assertEquals(info1, info1);
		assertEquals(info2, info2);
		assertEquals(info1, info2);
		assertEquals(info2, info1);

		assertNotEquals(info1, info3);
		assertNotEquals(info1, info4);
		assertNotEquals(info1, info5);
		assertNotEquals(info1, info6);
		assertNotEquals(info1, info7);
		assertNotEquals(info1, info8);

		assertEquals(info1.hashCode(), info2.hashCode());

		assertNotEquals(info1.hashCode(), info3.hashCode());
		assertNotEquals(info1.hashCode(), info4.hashCode());
		assertNotEquals(info1.hashCode(), info5.hashCode());
		assertNotEquals(info1.hashCode(), info6.hashCode());
		assertNotEquals(info1.hashCode(), info7.hashCode());
		assertNotEquals(info1.hashCode(), info8.hashCode());
	}

	@Test
	public void testEqualsAndHashCodeOfBareObjects() throws Exception {

		MicroNetworkInfo info1 = new MicroNetworkInfo();
		MicroNetworkInfo info2 = new MicroNetworkInfo();

		assertEquals(info1, info1);
		assertEquals(info2, info2);
		assertEquals(info1, info2);
		assertEquals(info2, info1);

		assertEquals(info1.hashCode(), info2.hashCode());
	}

}
