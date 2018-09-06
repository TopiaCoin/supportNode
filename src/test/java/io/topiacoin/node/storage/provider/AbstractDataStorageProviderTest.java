package io.topiacoin.node.storage.provider;

import org.junit.Test;

import static junit.framework.TestCase.*;

public abstract class AbstractDataStorageProviderTest {

    protected abstract DataStorageProvider getDataStorageProvider() ;

    @Test
    public void sanityTest() {
        fail ( "I'm the sanest test ever!");
    }

}
