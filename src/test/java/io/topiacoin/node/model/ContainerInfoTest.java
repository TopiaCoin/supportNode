package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ContainerInfoTest {

	@Test
	public void testDefaultConstructor() throws Exception {
		ContainerInfo container = new ContainerInfo();

		assertNull(container.getId());
		assertEquals(0, container.getExpirationDate());
	}

	@Test
	public void testConstructor() throws Exception {

		String id = "abc-123";
		long expirationDate = 1111L;

		ContainerInfo container = new ContainerInfo(id, expirationDate);

		assertEquals(id, container.getId());
		assertEquals(expirationDate, container.getExpirationDate());
	}

	@Test
	public void testBasicAccessors() throws Exception {

		String id = "abc-123";
		long expirationDate = 1111L;

		ContainerInfo container = new ContainerInfo();

		assertNull(container.getId());
		container.setId(id);
		assertEquals(id, container.getId());
		container.setId(null);
		assertNull(container.getId());

		assertEquals(0, container.getExpirationDate());
		container.setExpirationDate(expirationDate);
		assertEquals(expirationDate, container.getExpirationDate());
		container.setExpirationDate(0);
		assertEquals(0, container.getExpirationDate());
	}

	@Test
	public void testEqualsAndHashCode() throws Exception {

		String id = "abc-123";
		long expirationDate = 1111L;

		ContainerInfo container1 = new ContainerInfo(id, expirationDate);
		ContainerInfo container2 = new ContainerInfo(id, expirationDate);

		assertEquals(container1, container1);
		assertEquals(container2, container2);
		assertEquals(container1, container2);
		assertEquals(container2, container1);

		assertEquals(container1.hashCode(), container2.hashCode());
	}

	@Test
	public void testEqualsAndHashCodeOfBareObjects() throws Exception {

		ContainerInfo container1 = new ContainerInfo();
		ContainerInfo container2 = new ContainerInfo();

		assertEquals(container1, container1);
		assertEquals(container2, container2);
		assertEquals(container1, container2);
		assertEquals(container2, container1);

		assertEquals(container1.hashCode(), container2.hashCode());
	}

}
