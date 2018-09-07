package io.topiacoin.node.storage;

import io.topiacoin.node.model.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.model.exceptions.NoSuchDataItemException;
import io.topiacoin.node.storage.exceptions.CorruptDataItemException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataStorageManager {

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
     * @param containerID The ID of the container in which this data item belongs.
     * @param dataHash    The cryptographic hash of this data item.
     * @param dataStream  The InputStream containing the raw bytes of the data item.
     *
     * @throws DataItemAlreadyExistsException If a data item with the specified dataID already exists in the specified
     *                                        container.
     * @throws CorruptDataItemException       If the provided data item doesn't match its cryptographic hash.
     * @throws IOException                    If their is an exception saving the data item.
     */
    void saveData(String dataID, String containerID, String dataHash, InputStream dataStream)
            throws IOException, DataItemAlreadyExistsException, CorruptDataItemException {

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
     * @param containerID The ID of the container in which this item belongs.
     * @param dataHash    The cryptographic hash of this data item.
     * @param data        The array containing the raw bytes of this data item.
     *
     * @throws DataItemAlreadyExistsException If a data item with the specified dataID already exists in the specified
     *                                        container.
     * @throws CorruptDataItemException       If the provided data item doesn't match its cryptographic hash.
     * @throws IOException                    If there is an exception saving the data item.
     */
    void saveData(String dataID, String containerID, String dataHash, byte[] data)
            throws IOException, DataItemAlreadyExistsException, CorruptDataItemException {

    }

    /**
     * Retrieves the data with the specified dataID and containerID.  Before returning the data, its integrity will be
     * verified against the recorded hash to insure that it hasn't been corrupted.  If the data is found to be
     * corrupted, it will be purged from internal storage and an exception returned to the caller.
     *
     * @param dataID      The ID of the data item that is being fetched.
     * @param containerID The ID of the container in which the data item resides.
     *
     * @return A byte array containing the raw bytes of the data item.
     *
     * @throws NoSuchDataItemException  If the specified data item does not exist in the specified container.
     * @throws CorruptDataItemException If the specified data item doesn't match its cryptographic hash.
     * @throws IOException              If there is an exception reading the data item.
     */
    byte[] fetchData(String dataID, String containerID)
            throws IOException, NoSuchDataItemException, CorruptDataItemException {
        return null;
    }

    /**
     * Retrieves the data with the specified dataID and containerID.  Before returning the data in the outputStream, its
     * integrity will be verified against the recorded hash to insure that it hasn't been corrupted.  If the data is
     * found to be corrupted, it will be purged from internal storage and an exception returned to the caller.
     *
     * @param dataID       The ID of the data item that is being fetched.
     * @param containerID  The ID of the container in which the data item resides.
     * @param outputStream The OutputStream to which the data item should be written.
     *
     * @throws NoSuchDataItemException  If the specified data item does not exist in the specified container.
     * @throws CorruptDataItemException If the specified data item doesn't match its cryptographic hash.
     * @throws IOException              If there is an exception reading the data item.
     */
    void fetchData(String dataID, String containerID, OutputStream outputStream)
            throws IOException, NoSuchDataItemException, CorruptDataItemException {

    }

    /**
     * Retrieves a specific sub-portion of the data item.  No verification is performed before returning the data.
     *
     * @param dataID      The ID of the data item that is being fetched.
     * @param containerID The ID of the container in which the data item resides.
     * @param offset      The offset within the data item from which to read.
     * @param length      The number of bytes within the data item to read.
     *
     * @return A byte array containing the raw bytes of the region of the data item.
     *
     * @throws IOException             If there is an exception reading the data item.
     * @throws NoSuchDataItemException If the specified data item does not exist in the specified container.
     */
    byte[] fetchDataSubset(String dataID, String containerID, int offset, int length)
            throws IOException, NoSuchDataItemException {
        return null;
    }

    /**
     * Removes the data item with the specified dataID and containerID.  Returns true if a matching data item was found
     * and removed.  Returns false if no matching data item was found.
     *
     * @param dataID      The ID of the data item that is being removed.
     * @param containerID The ID of the container in which the data item resides.
     *
     * @return True if the specified data item was removed from storage.  False if the specified data item was not
     * removed from storage.
     *
     * @throws IOException If there is an exception removing the data item.
     */
    boolean removeData(String dataID, String containerID)
            throws IOException {
        return false;
    }

    /**
     * Removes all of the data items with the specified containerID.  This will purge multiple data items from the
     * subsystem.
     *
     * @param containerID The ID of the container whose data items are being removed.
     *
     * @throws IOException If there is an exception removing the data items.
     */
    void removeData(String containerID)
            throws IOException {

    }

    /**
     * Returns whether the data item with the specified dataID and containerID is present in the Data Storage subsystem.
     * If the data is stored, its integrity will be verified against the recorded hash to insure it hasn't been
     * corrupted.  If the data is corrupted, the method will return false, and the data will be purged from internal
     * storage.
     *
     * @param dataID      The ID of the data item whose existence is being queried.
     * @param containerID The ID of the container in which the specified data item's existence is to be checked.
     *
     * @return True, if the data item exists in the specified container.  False, if it does not exist in the specified
     * container.
     *
     * @throws IOException If there is an exception checking the existence of the specified data item.
     */
    boolean hasData(String dataID, String containerID)
            throws IOException {
        return false;
    }


}