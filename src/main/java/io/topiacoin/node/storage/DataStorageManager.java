package io.topiacoin.node.storage;

import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.InitializationException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.exceptions.CorruptDataItemException;
import io.topiacoin.node.storage.provider.DataStorageProvider;
import io.topiacoin.node.utilities.HashUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

@Component
public class DataStorageManager {

    private Log _log = LogFactory.getLog(this.getClass());

    @Autowired
    private DataStorageProvider _dataStorageProvider ;

    @PostConstruct
    public void initialize() {
        _log.info("Initializing Data Storage Manager" ) ;

        if ( _dataStorageProvider == null ) {
            throw new InitializationException("Failed to initialize the Data Storage Manager.  Data Storage Provider was not configured.");
        }
        _log.info("Initialized Data Storage Manager" ) ;
    }

    @PreDestroy
    public void shutdown() {
        _log.info("Shutting Down Data Storage Manager" ) ;
        _log.info("Shut Down Data Storage Manager" ) ;
    }

    /**
     * Saves the data in the given dataStream.  The data is recorded using the specified dataID and verified against the
     * dataHash.
     * <p>
     * This operation will fail if a data item already exists with the specified dataID and containerID, or if the data
     * in the dataStream does not match the dataHash.  If the data passes these checks, the manager will add entries to
     * the data model to record the existence of the data item, its hash, and its containerID, then pass the data item
     * to Data Storage for persistent storage.
     *
     * @param dataID      The ID of the data item that is being saved.
     * @param dataHash    The cryptographic hash of this data item.
     * @param dataStream  The InputStream containing the raw bytes of the data item.
     *
     * @return The size of the stored data item.
     *
     * @throws DataItemAlreadyExistsException If a data item with the specified dataID already exists in the specified
     *                                        container.
     * @throws CorruptDataItemException       If the provided data item doesn't match its cryptographic hash.
     * @throws IOException                    If their is an exception saving the data item.
     */
    public long saveData(String dataID, String dataHash, InputStream dataStream)
            throws IOException, DataItemAlreadyExistsException, CorruptDataItemException {
        if ( hasData(dataID)) {
            throw new DataItemAlreadyExistsException("The specified data item already exists. (ID: " + dataID + ")");
        }

        long size = -1 ;
        try {
            if ( ! HashUtilities.verifyHash(dataHash, dataStream) ) {
                throw new CorruptDataItemException("The specified data item does not match the specified hash" );
            }
            size = _dataStorageProvider.saveData(dataID, dataStream);
        } catch ( NoSuchAlgorithmException e ) {
            throw new CorruptDataItemException( "Unable to verify the data hash.", e) ;
        }

        return size;
    }

    /**
     * Saves the given data.  The data is recorded using the specified dataID and verified against the dataHash.
     * <p>
     * This operation will fail if a data item already exists with the specified dataID and containerID, or if the data
     * in the dataStream does not match the dataHash.  If the data passes these checks, the manager will add entries to
     * the data model to record the existence of the data item, its hash, and its containerID, then pass the data item
     * to Data Storage for persistent storage.
     *
     * @param dataID      The ID of the data item that is being saved.
     * @param dataHash    The cryptographic hash of this data item.
     * @param data        The array containing the raw bytes of this data item.
     *
     * @throws DataItemAlreadyExistsException If a data item with the specified dataID already exists in the specified
     *                                        container.
     * @throws CorruptDataItemException       If the provided data item doesn't match its cryptographic hash.
     * @throws IOException                    If there is an exception saving the data item.
     */
    public void saveData(String dataID, String dataHash, byte[] data)
            throws IOException, DataItemAlreadyExistsException, CorruptDataItemException {
        saveData(dataID, dataHash, new ByteArrayInputStream(data));
    }

    /**
     * Retrieves the data with the specified dataID and containerID.  Before returning the data, its integrity will be
     * verified against the recorded hash to insure that it hasn't been corrupted.  If the data is found to be
     * corrupted, it will be purged from internal storage and an exception returned to the caller.
     *
     * @param dataID      The ID of the data item that is being fetched.
     *
     * @param dataHash
     * @return A byte array containing the raw bytes of the data item.
     *
     * @throws NoSuchDataItemException  If the specified data item does not exist in the specified container.
     * @throws CorruptDataItemException If the specified data item doesn't match its cryptographic hash.
     * @throws IOException              If there is an exception reading the data item.
     */
    public byte[] fetchData(String dataID, String dataHash)
            throws IOException, NoSuchDataItemException, CorruptDataItemException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        _dataStorageProvider.fetchData(dataID, outputStream);
        outputStream.close();
        byte[] data = outputStream.toByteArray();

        try {
            if (!HashUtilities.verifyHash(dataHash, data)) {
                throw new CorruptDataItemException("The requested data item is corrupt.");
            }
        } catch ( NoSuchAlgorithmException e ) {
                throw new CorruptDataItemException("Unable to verify the data integrity.", e);
        }

        return data;
    }

    /**
     * Retrieves the data with the specified dataID and containerID.  Before returning the data in the outputStream, its
     * integrity will be verified against the recorded hash to insure that it hasn't been corrupted.  If the data is
     * found to be corrupted, it will be purged from internal storage and an exception returned to the caller.
     *
     * @param dataID       The ID of the data item that is being fetched.
     * @param dataHash
     * @param outputStream The OutputStream to which the data item should be written.
     *
     * @throws NoSuchDataItemException  If the specified data item does not exist in the specified container.
     * @throws CorruptDataItemException If the specified data item doesn't match its cryptographic hash.
     * @throws IOException              If there is an exception reading the data item.
     */
    public void fetchData(String dataID, String dataHash, OutputStream outputStream)
            throws IOException, NoSuchDataItemException, CorruptDataItemException {

        byte[] data = fetchData(dataID, dataHash) ;

        try {
            if (!HashUtilities.verifyHash(dataHash, data)) {
                throw new CorruptDataItemException("The requested data item is corrupt.");
            }
        } catch ( NoSuchAlgorithmException e ) {
            throw new CorruptDataItemException("Unable to verify the data integrity.", e);
        }

        outputStream.write(data);
    }

    /**
     * Retrieves a specific sub-portion of the data item.  No verification is performed before returning the data.
     *
     * @param dataID      The ID of the data item that is being fetched.
     * @param offset      The offset within the data item from which to read.
     * @param length      The number of bytes within the data item to read.
     *
     * @return A byte array containing the raw bytes of the region of the data item.
     *
     * @throws IOException             If there is an exception reading the data item.
     * @throws NoSuchDataItemException If the specified data item does not exist in the specified container.
     */
    public byte[] fetchDataSubset(String dataID, int offset, int length)
            throws IOException, NoSuchDataItemException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        _dataStorageProvider.fetchData(dataID, offset, length, outputStream);
        outputStream.close();
        byte[] data = outputStream.toByteArray();

        return data;
    }

    /**
     * Removes the data item with the specified dataID and containerID.  Returns true if a matching data item was found
     * and removed.  Returns false if no matching data item was found.
     *
     * @param dataID      The ID of the data item that is being removed.
     * @return True if the specified data item was removed from storage.  False if the specified data item was not
     * removed from storage.
     *
     * @throws IOException If there is an exception removing the data item.
     */
    public boolean removeData(String dataID)
            throws IOException {
        return _dataStorageProvider.removeData(dataID);
    }

    /**
     * Returns whether the data item with the specified dataID and containerID is present in the Data Storage subsystem.
     * If the data is stored, its integrity will be verified against the recorded hash to insure it hasn't been
     * corrupted.  If the data is corrupted, the method will return false, and the data will be purged from internal
     * storage.
     *
     * @param dataID      The ID of the data item whose existence is being queried.
     * @return True, if the data item exists in the specified container.  False, if it does not exist in the specified
     * container.
     *
     * @throws IOException If there is an exception checking the existence of the specified data item.
     */
    public boolean hasData(String dataID)
            throws IOException {
        return _dataStorageProvider.hasData(dataID);
    }

    public void setDataStorageProvider(DataStorageProvider dataStorageProvider) {
        _dataStorageProvider = dataStorageProvider;
    }
}
