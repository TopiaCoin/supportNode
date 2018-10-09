package io.topiacoin.node.storage.provider;

import org.junit.After;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSystemStorageProviderTest extends AbstractDataStorageProviderTest {

    public static final String STORAGE_BASE_PATH = "./target/storageTest";
    private FileSystemStorageProvider _storageProvider;

    @After
    public void tearDown() throws Exception {

        _storageProvider.shutdown();

        File file = new File(STORAGE_BASE_PATH);

        deletePathAndContents(file);
    }

    private void deletePathAndContents(File file ) {
        for ( File curFile : file.listFiles()) {
            if ( curFile.isFile() ) {
                curFile.delete();
            } else if ( curFile.isDirectory() ) {
                deletePathAndContents(curFile);
            }
        }

        file.delete();
    }

    @Override
    protected DataStorageProvider getDataStorageProvider() {

        _storageProvider = new FileSystemStorageProvider();

        _storageProvider.setStorageBasePath(STORAGE_BASE_PATH);

        _storageProvider.initialize();

        return _storageProvider;
    }
}
