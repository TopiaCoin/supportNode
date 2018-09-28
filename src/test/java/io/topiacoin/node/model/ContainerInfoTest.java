package io.topiacoin.node.model;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.*;

public class ContainerInfoTest {

	@Test
	public void testDefaultConstructor() throws Exception {
		ContainerInfo container = new ContainerInfo();

		assertNull(container.getId());
		assertEquals(0, container.getExpirationDate());
		assertNull(container.getChallenge());
	}

	@Test
	public void testConstructor() throws Exception {

		String id = "abc-123";
		long expirationDate = 1111L;
		Challenge challenge = new Challenge(id, new ArrayList<>());

		ContainerInfo container = new ContainerInfo(id, expirationDate, challenge);

		assertEquals(id, container.getId());
		assertEquals(expirationDate, container.getExpirationDate());
		assertEquals(challenge, container.getChallenge());
	}

	@Test
	public void testBasicAccessors() throws Exception {

		String id = "abc-123";
		long expirationDate = 1111L;
		Challenge challenge = new Challenge(id, new ArrayList<>());

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

		assertNull(container.getChallenge());
		container.setChallenge(challenge);
		assertNotNull(container.getChallenge());
		assertEquals(challenge, container.getChallenge());
		container.setChallenge(null);
		assertNull(container.getChallenge());
	}

	@Test
	public void testEqualsAndHashCode() throws Exception {

		String id = "abc-123";
		long expirationDate = 1111L;
		Challenge challenge = new Challenge(id, new ArrayList<>());

		ContainerInfo container1 = new ContainerInfo(id, expirationDate, challenge);
		ContainerInfo container2 = new ContainerInfo(id, expirationDate, challenge);

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
