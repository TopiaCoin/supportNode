package io.topiacoin.node.storage.provider;

import io.topiacoin.node.exceptions.NoSuchDataItemException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataStorageProvider implements DataStorageProvider {

    private Map<String, byte[]> _dataMap ;

    public MemoryDataStorageProvider() {
        _dataMap = new HashMap<>();
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[16384] ;
        int bytesRead = 0 ;
        while ( (bytesRead = dataStream.read(buffer)) > 0 ) {
            baos.write(buffer, 0, bytesRead);
        }
        baos.close();
        byte[] data = baos.toByteArray();

        _dataMap.put (dataID, data) ;
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
    public void fetchData(String dataID, OutputStream outputStream) throws NoSuchDataItemException, IOException {
        byte[] data = _dataMap.get(dataID) ;

        if ( data == null ) {
            throw new NoSuchDataItemException("Unable to fetch data item '" + dataID + "'");
        }

        outputStream.write(data);
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
        byte[] data = _dataMap.get(dataID) ;

        if ( data == null ) {
            throw new NoSuchDataItemException("Unable to fetch data item '" + dataID + "'");
        }

        outputStream.write(data, offset, length);
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
        byte[] bytes = _dataMap.remove(dataID);
        return bytes != null;
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
        return _dataMap.containsKey(dataID);
    }
}
