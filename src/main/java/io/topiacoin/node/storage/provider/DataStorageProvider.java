package io.topiacoin.node.storage.provider;

import io.topiacoin.node.exceptions.NoSuchDataItemException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DataStorageProvider {

    /**
     * Saves the given data item to persistent storage.
     *
     * @param dataID     The ID of the data item that is being saved.
     * @param dataStream The InputStream containing the raw bytes of the data item.
     *
     * @throws IOException If there is an exception trying to save the data.
     */
    void saveData(String dataID, InputStream dataStream) throws IOException;

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
    void fetchData(String dataID, OutputStream outputStream) throws IOException, NoSuchDataItemException;

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
    void fetchData(String dataID, int offset, int length, OutputStream outputStream) throws IOException, NoSuchDataItemException;

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
    boolean removeData(String dataID) throws IOException;

    /**
     * Returns true if a data item with the specified dataID has been persistently stored.
     *
     * @param dataID The ID of the data item whose existence is being checked.
     *
     * @return True if the specified data item exists in the storage.  False if it does not.
     *
     * @throws IOException If there is an exception trying to check for the data.
     */
    boolean hasData(String dataID) throws IOException;


}
