package io.topiacoin.node.storage;

import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.exceptions.CorruptDataItemException;
import io.topiacoin.node.model.DataItemInfo;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.provider.MemoryDataModelProvider;
import io.topiacoin.node.storage.provider.MemoryDataStorageProvider;
import io.topiacoin.node.utilities.HashUtilities;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static junit.framework.TestCase.*;

public class DataStorageManagerTest {

//    @Test
//    public void sanityTest() {
//        fail ( "I'm the sanest test ever!");
//    }

    @Test
    public void saveFetchRemoveByteArray() throws Exception {

        // Setup and configure the Data Storage Manager
        DataModel dataModel = getDataModel();

        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        dsp.initialize();

        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.setDataModel(dataModel);
        dsm.initialize();

        // Create the test data
        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();
        byte[] data;
        String dataHash;
        boolean hasData = false;

        // Create the data and hash it
        data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);
        dataHash = HashUtilities.generateHash("SHA-256", data);

        // Preload the Data Model
        dataModel.createContainer(containerID, 0, null);

        // Make sure the DSM doesn't contain the data item.
        hasData = dsm.hasData(containerID, dataID);
        assertFalse("DataStorageManage should not have the data item", hasData);

        // Dave the data item in the DSM
        dsm.saveData(containerID, dataID, dataHash, data);

        // Verify that the DSM now has the data item
        hasData = dsm.hasData(containerID, dataID);
        assertTrue("DataStorageManage should have the data item", hasData);
        assertTrue(dataModel.isDataItemInContainer(dataID, containerID));
        assertNotNull(dataModel.getDataItem(dataID));

        // Fetch the data item from the DSM and verify it is correct
        byte[] fetchedData = dsm.fetchData(containerID, dataID);
        assertNotNull("No Data was returned", fetchedData);
        assertTrue("Fetched Data did not match stored data", Arrays.equals(data, fetchedData));

        // Remove the data item from the DSM
        dsm.removeData(containerID, dataID);

        // Verify the DSM no longer has the data item
        hasData = dsm.hasData(containerID, dataID);
        assertFalse("DataStorageManage should not have the data item", hasData);
        assertFalse(dataModel.isDataItemInContainer(dataID, containerID));
        assertNull(dataModel.getDataItem(dataID));

        // Attempt to fetch the remove data item and verify it fails
        try {
            fetchedData = dsm.fetchData(containerID, dataID);
            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void saveFetchRemoveDataStream() throws Exception {

        // Setup and configure the Data Storage Manager
        DataModel dataModel = getDataModel();

        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        dsp.initialize();

        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.setDataModel(dataModel);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();
        byte[] data;
        String dataHash;
        InputStream dataInputStream;
        OutputStream dataOutputStream;
        boolean hasData = false;

        // Create the data and hash it
        data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);
        dataHash = HashUtilities.generateHash("SHA-256", data);

        dataInputStream = new ByteArrayInputStream(data);

        // Preload the Data Model
        dataModel.createContainer(containerID, 0, null);

        // Make sure the DSM doesn't contain the data item.
        hasData = dsm.hasData(containerID, dataID);
        assertFalse("DataStorageManage should not have the data item", hasData);

        // Dave the data item in the DSM
        dsm.saveData(containerID, dataID, dataHash, dataInputStream);

        // Verify that the DSM now has the data item
        hasData = dsm.hasData(containerID, dataID);
        assertTrue("DataStorageManage should have the data item", hasData);

        // Fetch the data item from the DSM and verify it is correct
        dataOutputStream = new ByteArrayOutputStream();
        dsm.fetchData(containerID, dataID, dataOutputStream);
        dataOutputStream.close();
        byte[] fetchedData = ((ByteArrayOutputStream) dataOutputStream).toByteArray();
        assertNotNull("No Data was returned", fetchedData);
        assertTrue("Fetched Data did not match stored data", Arrays.equals(data, fetchedData));

        // Remove the data item from the DSM
        dsm.removeData(containerID, dataID);

        // Verify the DSM no longer has the data item
        hasData = dsm.hasData(containerID, dataID);
        assertFalse("DataStorageManage should not have the data item", hasData);

        // Attempt to fetch the remove data item and verify it fails
        try {
            dataOutputStream = new ByteArrayOutputStream();
            dsm.fetchData(containerID, dataID, dataOutputStream);
            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testFetchNonExistentByteArray() throws Exception {
        // Setup and configure the Data Storage Manager
        DataModel dataModel = getDataModel();

        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        dsp.initialize();

        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.setDataModel(dataModel);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        // Preload the Data Model
        dataModel.createContainer(containerID, 0, null);

        try {
            byte[] fetchedData = dsm.fetchData(containerID, dataID);
            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testFetchNonExistentDataStream() throws Exception {
        // Setup and configure the Data Storage Manager
        DataModel dataModel = getDataModel();

        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        dsp.initialize();

        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.setDataModel(dataModel);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        // Preload the Data Model
        dataModel.createContainer(containerID, 0, null);

        try {
            ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
            dsm.fetchData(containerID, dataID, dataOutputStream);

            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testSaveDuplicateByteArray() throws Exception {
        // Setup and configure the Data Storage Manager
        DataModel dataModel = getDataModel();

        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        dsp.initialize();

        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.setDataModel(dataModel);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        byte[] data;
        String dataHash;

        // Create the data and hash it
        data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);
        dataHash = HashUtilities.generateHash("SHA-256", data);

        // Preload the Data Model
        dataModel.createContainer(containerID, 0, null);

        // Save initial copy of the data
        dsm.saveData(containerID, dataID, dataHash, data);

        // Attempt to save it again
        try {
            dsm.saveData(containerID, dataID, dataHash, data);

            fail("Expected DataItemAlreadyExistsException was not thrown");
        } catch (DataItemAlreadyExistsException e) {
            // NOOP
        }
    }

    @Test
    public void testSaveDuplicateDataStream() throws Exception {
        // Setup and configure the Data Storage Manager
        DataModel dataModel = getDataModel();

        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        dsp.initialize();

        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.setDataModel(dataModel);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        byte[] data;
        String dataHash;

        // Create the data and hash it
        data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);
        dataHash = HashUtilities.generateHash("SHA-256", data);

        // Preload the Data Model
        dataModel.createContainer(containerID, 0, null);

        // Save initial copy of the data
        dsm.saveData(containerID, dataID, dataHash, data);

        // Attempt to save it again
        try {
            ByteArrayInputStream dataInputStream = new ByteArrayInputStream(data);
            dsm.saveData(containerID, dataID, dataHash, data);

            fail("Expected DataItemAlreadyExistsException was not thrown");
        } catch (DataItemAlreadyExistsException e) {
            // NOOP
        }
    }

    @Test
    public void testSaveByteArrayWithCorruptHash() throws Exception {
        // Setup and configure the Data Storage Manager
        DataModel dataModel = getDataModel();

        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        dsp.initialize();

        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.setDataModel(dataModel);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        // Preload the Data Model
        dataModel.createContainer(containerID, 0, null);

        byte[] data;
        String dataHash;

        // Create the data and hash it
        data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);
        dataHash = HashUtilities.generateHash("SHA-256", "Wrong Data".getBytes());

        // Attempt to save the data item with a bad hash
        try {
            dsm.saveData(containerID, dataID, dataHash, data);

            fail("Expected CorruptDataItemException was not thrown");
        } catch (CorruptDataItemException e) {
            // NOOP
        }
    }

    @Test
    public void testSaveDataStreamWithCorruptHash() throws Exception {
        // Setup and configure the Data Storage Manager
        DataModel dataModel = getDataModel();

        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        dsp.initialize();

        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.setDataModel(dataModel);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        // Preload the Data Model
        dataModel.createContainer(containerID, 0, null);

        byte[] data;
        String dataHash;

        // Create the data and hash it
        data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);
        dataHash = HashUtilities.generateHash("SHA-256", "Wrong Data".getBytes());

        // Attempt to save the data item with a bad hash
        try {
            ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
            dsm.saveData(containerID, dataID, dataHash, dataStream);

            fail("Expected CorruptDataItemException was not thrown");
        } catch (CorruptDataItemException e) {
            // NOOP
        }
    }

    @Test
    public void testFetchByteArrayWithCorruptHash() throws Exception {
        // Setup and configure the Data Storage Manager
        DataModel dataModel = getDataModel();

        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        dsp.initialize();

        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.setDataModel(dataModel);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        // Preload the Data Model
        dataModel.createContainer(containerID, 0, null);

        byte[] data;
        String dataHash;

        // Create the data and hash it
        data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);
        dataHash = HashUtilities.generateHash("SHA-256", data);
        String badHash = HashUtilities.generateHash("SHA-256", "Wrong Data".getBytes());

        // Write the data into the Data Store
        ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
        dsm.saveData(containerID, dataID, dataHash, dataStream);

        // Update the Data Hash in the Data Model with a bad value
        DataItemInfo dataItemInfo = dataModel.getDataItem(dataID);
        dataItemInfo.setDataHash(badHash);
        dataModel.updateDataItem(dataItemInfo);

        // Attempt to save the data item with a bad hash
        try {
            byte[] fetchedData = dsm.fetchData(containerID, dataID) ;
            fail("Expected CorruptDataItemException was not thrown");
        } catch (CorruptDataItemException e) {
            // NOOP
        }
    }

    @Test
    public void testFetchDataStreamWithCorruptHash() throws Exception {
        // Setup and configure the Data Storage Manager
        DataModel dataModel = getDataModel();

        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        dsp.initialize();

        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.setDataModel(dataModel);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        // Preload the Data Model
        dataModel.createContainer(containerID, 0, null);

        byte[] data;
        String dataHash;

        // Create the data and hash it
        data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);
        dataHash = HashUtilities.generateHash("SHA-256", data);
        String badHash = HashUtilities.generateHash("SHA-256", "Wrong Data".getBytes());

        // Write the data into the Data Store
        ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
        dsm.saveData(containerID, dataID, dataHash, dataStream);

        // Update the Data Hash in the Data Model with a bad value
        DataItemInfo dataItemInfo = dataModel.getDataItem(dataID);
        dataItemInfo.setDataHash(badHash);
        dataModel.updateDataItem(dataItemInfo);

        // Attempt to save the data item with a bad hash
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            dsm.fetchData(containerID, dataID, outputStream) ;
            fail("Expected CorruptDataItemException was not thrown");
        } catch (CorruptDataItemException e) {
            // NOOP
        }
    }

    // -------- Utility Methods --------

    private DataModel getDataModel() {
        MemoryDataModelProvider dataModelProvider = new MemoryDataModelProvider();
        dataModelProvider.initialize();

        DataModel dataModel = new DataModel() ;
        dataModel.setProvider(dataModelProvider);
        dataModel.initialize();
        return dataModel;
    }

}