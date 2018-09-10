package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
		MicroNetworkState state = new MicroNetworkState("State");

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
		MicroNetworkState state = new MicroNetworkState("state");

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
		String containerID = "def-456";
		String path = "whatever";
		String rpcURL = "rpcURL";
		String p2pURL = "p2pURL";
		MicroNetworkState state = new MicroNetworkState("State");

		MicroNetworkInfo info1 = new MicroNetworkInfo(id, containerID, path, state, rpcURL, p2pURL);
		MicroNetworkInfo info2 = new MicroNetworkInfo(id, containerID, path, state, rpcURL, p2pURL);

		assertEquals(info1, info1);
		assertEquals(info2, info2);
		assertEquals(info1, info2);
		assertEquals(info2, info1);

		assertEquals(info1.hashCode(), info2.hashCode());
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
