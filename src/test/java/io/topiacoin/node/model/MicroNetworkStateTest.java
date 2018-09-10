package io.topiacoin.node.model;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MicroNetworkStateTest {

	@Test
	public void testIt() throws Exception {
		MicroNetworkState state = new MicroNetworkState("blah");
		Assert.assertEquals("blah", state.getState());
		state.setState("blah2");
		Assert.assertEquals("blah2", state.getState());
	}
}
