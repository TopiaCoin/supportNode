package io.topiacoin.node.micronetwork.providers;

import io.topiacoin.node.micronetwork.providers.BlockchainProvider;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AbstractBlockchainProviderTest {

    protected abstract BlockchainProvider getBlockchainProvider();

    @Test
    public void testSanity() {
        fail("Test Cases Not yet Implemented");
    }

    @Test
    public void testCreateStartStopDestroyBlockchain() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testSyncStopDestroyBlockchain() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testCreateAlreadyExistingBlockchain() throws Exception {
        fail ("Test Not Yet Implemented");
    }
}
