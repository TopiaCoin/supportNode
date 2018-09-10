package io.topiacoin.node.storage.provider;

public class MemoryDataStorageProviderTest extends AbstractDataStorageProviderTest {
    @Override
    protected DataStorageProvider getDataStorageProvider() {
        return new MemoryDataStorageProvider();
    }
}
