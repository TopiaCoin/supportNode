package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BlockchainInfoTest {

	@Test
	public void testDefaultConstructor() throws Exception {
		BlockchainInfo blockchain = new BlockchainInfo();

		assertNull(blockchain.getId());
		assertNull(blockchain.getLocalPath());
	}

	@Test
	public void testConstructor() throws Exception {

		String id = "abc-123";
		String localPath = "/foo/bar";

		BlockchainInfo blockchain = new BlockchainInfo(id, localPath);

		assertEquals(id, blockchain.getId());
		assertEquals(localPath, blockchain.getLocalPath());
	}

	@Test
	public void testBasicAccessors() throws Exception {

		String id = "abc-123";
		String localPath = "/foo/bar";

		BlockchainInfo blockchain = new BlockchainInfo();

		assertNull(blockchain.getId());
		blockchain.setId(id);
		assertEquals(id, blockchain.getId());
		blockchain.setId(null);
		assertNull(blockchain.getId());

		assertNull(blockchain.getLocalPath());
		blockchain.setLocalPath(localPath);
		assertEquals(localPath, blockchain.getLocalPath());
		blockchain.setLocalPath(null);
		assertNull(blockchain.getLocalPath());
	}

	@Test
	public void testEqualsAndHashCode() throws Exception {

		String id = "abc-123";
		String localPath = "/foo/bar";

		BlockchainInfo blockchain1 = new BlockchainInfo(id, localPath);
		BlockchainInfo blockchain2 = new BlockchainInfo(id, localPath);

		assertEquals(blockchain1, blockchain1);
		assertEquals(blockchain2, blockchain2);
		assertEquals(blockchain1, blockchain2);
		assertEquals(blockchain2, blockchain1);

		assertEquals(blockchain1.hashCode(), blockchain2.hashCode());
	}

	@Test
	public void testEqualsAndHashCodeOfBareObjects() throws Exception {

		BlockchainInfo blockchain1 = new BlockchainInfo();
		BlockchainInfo blockchain2 = new BlockchainInfo();

		assertEquals(blockchain1, blockchain1);
		assertEquals(blockchain2, blockchain2);
		assertEquals(blockchain1, blockchain2);
		assertEquals(blockchain2, blockchain1);

		assertEquals(blockchain1.hashCode(), blockchain2.hashCode());
	}

}
