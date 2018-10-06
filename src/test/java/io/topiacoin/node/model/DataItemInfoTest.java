package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataItemInfoTest {

	@Test
	public void testDefaultConstructor() throws Exception {
		DataItemInfo container = new DataItemInfo();

		assertNull(container.getId());
		assertNull(container.getDataHash());
		assertEquals(0, container.getSize());
	}

	@Test
	public void testConstructor() throws Exception {

		String id = "abc-123";
		String containerID = "def-456";
		String dataHash = "potatoes";
		long size = 1111L;

		DataItemInfo container = new DataItemInfo(id, size, dataHash);

		assertEquals(id, container.getId());
		assertEquals(dataHash, container.getDataHash());
		assertEquals(size, container.getSize());
	}

	@Test
	public void testBasicAccessors() throws Exception {

		String id = "abc-123";
		String containerID = "def-456";
		String dataHash = "potatoes";
		long size = 1111L;

		DataItemInfo container = new DataItemInfo();

		assertNull(container.getId());
		container.setId(id);
		assertEquals(id, container.getId());
		container.setId(null);
		assertNull(container.getId());

		assertNull(container.getDataHash());
		container.setDataHash(dataHash);
		assertEquals(dataHash, container.getDataHash());
		container.setDataHash(null);
		assertNull(container.getDataHash());

		assertEquals(0, container.getSize());
		container.setSize(size);
		assertEquals(size, container.getSize());
		container.setSize(0);
		assertEquals(0, container.getSize());
	}

	@Test
	public void testEqualsAndHashCode() throws Exception {

		String id = "abc-123";
		String containerID = "def-456";
		String dataHash = "potatoes";
		long size = 1111L;

		DataItemInfo container1 = new DataItemInfo(id, size, dataHash);
		DataItemInfo container2 = new DataItemInfo(id, size, dataHash);

		assertEquals(container1, container1);
		assertEquals(container2, container2);
		assertEquals(container1, container2);
		assertEquals(container2, container1);

		assertEquals(container1.hashCode(), container2.hashCode());
	}

	@Test
	public void testEqualsAndHashCodeOfBareObjects() throws Exception {

		DataItemInfo container1 = new DataItemInfo();
		DataItemInfo container2 = new DataItemInfo();

		assertEquals(container1, container1);
		assertEquals(container2, container2);
		assertEquals(container1, container2);
		assertEquals(container2, container1);

		assertEquals(container1.hashCode(), container2.hashCode());
	}

}
