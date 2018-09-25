package io.topiacoin.node.storage.provider;

import io.topiacoin.node.exceptions.InitializationException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileSystemStorageProvider implements DataStorageProvider {

    private Log _log = LogFactory.getLog(this.getClass());

    private String storageBasePath;
    private File storageBaseFile;


    public FileSystemStorageProvider() {

    }

    @PostConstruct
    public void initialize() {
        _log.info ( "Initializing File System Storage Provider");


        storageBaseFile = new File(storageBasePath) ;

        boolean exists = storageBaseFile.exists() || storageBaseFile.mkdirs();
        boolean readable = storageBaseFile.canRead();
        boolean writable = storageBaseFile.canWrite();

        _log.info ( "Storage Base Path: " + storageBasePath);
        _log.info ( "        Exists   : " + exists);
        _log.info ( "        Readable : " + readable);
        _log.info ( "        Writable : " + writable);


        // Attempt to create the storage base path if it doesn't exist.
        if ( !exists) {
            _log.warn ( "Unable to access or create the Storage Base Path" );
            throw new InitializationException( "Failed to create the Storage Base Path Directory" ) ;
        }


        // Verify that we have read and write access to the storage base path.
        if ( !readable ) {
            _log.warn ( "Unable to read from the Storage Base Path" );
            throw new InitializationException("Unable to read from the Storage Base Path") ;
        }
        if ( !writable ) {
            _log.warn ( "Unable to write to the Storage Base Path" );
            throw new InitializationException("Unable to write to the Storage Base Path") ;
        }

        _log.info ( "Initialized File System Storage Provider");
    }

    @PreDestroy
    public void shutdown() {
        _log.info ( "Shutting Down File System Storage Provider");
        _log.info ( "Shut Down File System Storage Provider");
    }

    /**
     * Saves the given data item to persistent storage.
     *
     * @param dataID     The ID of the data item that is being saved.
     * @param dataStream The InputStream containing the raw bytes of the data item.
     *
     * @throws IOException If there is an exception trying to save the data.
     */
    @Override
    public void saveData(String dataID, InputStream dataStream) throws IOException {

        String path = generatePathForDataID(dataID);
        File dataPath = new File(storageBaseFile, path);
        dataPath.getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(dataPath)) {
            IOUtils.copy(dataStream, fos);
        }
    }

    /**
     * Retrieves the data item from persistent storage and writes to to the given outputStream.  If the data item is not
     * in storage, an exception is thrown.
     *
     * @param dataID       The ID of the data item being fetched
     * @param outputStream The OutputStream to which the fetched item will be written.
     *
     * @throws IOException If there is an exception trying to fetch the data.
     * @throws NoSuchDataItemException If the specified Data Item does not exist.
     */
    @Override
    public void fetchData(String dataID, OutputStream outputStream) throws IOException, NoSuchDataItemException {
        String path = generatePathForDataID(dataID) ;
        File dataPath = new File(storageBaseFile, path);

        if (! dataPath.exists() ) {
            throw new NoSuchDataItemException("The requested Data Item does not exist");
        }

        try (FileInputStream fis = new FileInputStream(dataPath)) {
            IOUtils.copy(fis, outputStream);
        }
    }

    /**
     * Retrieves the requested portion of the data item from persistent storage and writes to to the given outputStream.
     * If the data item is not in storage, an exception is thrown.
     *
     * @param dataID       The ID of the data item being fetched.
     * @param offset       The offset within the data item to start reading data.
     * @param length       The number of bytes of data to return, starting at the offset.  If length is larger than the
     *                     size of the data item, an IOException is thrown.
     * @param outputStream The OutputStream to which the fetched data will be written.
     *
     * @throws IOException If there is an exception trying to fetch the data.
     * @throws NoSuchDataItemException If the specified Data Item does not exist.
     */
    @Override
    public void fetchData(String dataID, int offset, int length, OutputStream outputStream) throws IOException, NoSuchDataItemException {
        String path = generatePathForDataID(dataID) ;
        File dataPath = new File(storageBaseFile, path);

        if (! dataPath.exists() ) {
            throw new NoSuchDataItemException("The requested Data Item does not exist");
        }

        try (FileInputStream fis = new FileInputStream(dataPath)) {
            IOUtils.copyLarge(fis, outputStream, offset, length);
        }
    }

    /**
     * Removes the data item from persistent storage.  Returns true if a matching data item was found and removed.
     * Returns false if no matching data item was found.
     *
     * @param dataID The ID of the data item that is to be removed from storage.
     *
     * @return True if the specified data item was removed from storage.  False if it was not found.
     *
     * @throws IOException If there is an exception trying to remove the data.
     */
    @Override
    public boolean removeData(String dataID) throws IOException {
        String path = generatePathForDataID(dataID) ;
        File dataPath = new File(storageBaseFile, path);

        boolean deleted = dataPath.delete();

        // Remove empty parent dirs
        dataPath = dataPath.getParentFile();
        do {
            if ( ! dataPath.delete() ) {
                // We couldn't delete this dir.  This means there are other files/directories in here.
                // Thus, we also can't delete anything else up the tree either.
                break;
            }
            dataPath = dataPath.getParentFile();
        } while ( !dataPath.equals(storageBaseFile)) ;

        return deleted;
    }

    /**
     * Returns true if a data item with the specified dataID has been persistently stored.
     *
     * @param dataID The ID of the data item whose existence is being checked.
     *
     * @return True if the specified data item exists in the storage.  False if it does not.
     *
     * @throws IOException If there is an exception trying to check for the data.
     */
    @Override
    public boolean hasData(String dataID) throws IOException {
        String path = generatePathForDataID(dataID) ;
        File dataPath = new File(storageBaseFile, path);

        boolean exists = dataPath.exists();

        return exists;
    }

    // -------- Private Methods --------

    private String generatePathForDataID(String dataID) {
        StringBuilder sb = new StringBuilder();

        sb.append ( dataID.substring(0,2)) ;
        sb.append ( File.separator) ;
        sb.append (dataID.substring(2, 4));
        sb.append (File.separator);
        sb.append ( dataID.substring(4, 6));
        sb.append ( File.separator);
        sb.append ( dataID.substring(6, 8)) ;
        sb.append(File.separator);
        sb.append ( dataID);

        return sb.toString();
    }



    // -------- Accessor Methods --------


    public void setStorageBasePath(String storageBasePath) {
        this.storageBasePath = storageBasePath;
    }
}
