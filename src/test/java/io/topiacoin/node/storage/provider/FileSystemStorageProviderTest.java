package io.topiacoin.node.storage.provider;

public class FileSystemStorageProviderTest extends AbstractDataStorageProviderTest {
    @Override
    protected DataStorageProvider getDataStorageProvider() {

        FileSystemStorageProvider storageProvider = new FileSystemStorageProvider();

        return storageProvider;

    }
}
