package io.topiacoin.node;

import org.junit.Assert;
import org.junit.Test;

public class StubTest {

	@Test
	public void testStub() {
		Stub stub = new Stub();
		Assert.assertFalse("NO STUBS ALLOWED", stub.isThisAStub());
	}
}
