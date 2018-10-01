package io.topiacoin.node.storage;

import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.exceptions.CorruptDataItemException;
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
        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.initialize();

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

        // Make sure the DSM doesn't contain the data item.
        hasData = dsm.hasData(dataID, containerID);
        assertFalse("DataStorageManage should not have the data item", hasData);

        // Dave the data item in the DSM
        dsm.saveData(dataID, containerID, dataHash, data);

        // Verify that the DSM now has the data item
        hasData = dsm.hasData(dataID, containerID);
        assertTrue("DataStorageManage should have the data item", hasData);

        // Fetch the data item from the DSM and verify it is correct
        byte[] fetchedData = dsm.fetchData(dataID, containerID, dataHash);
        assertNotNull("No Data was returned", fetchedData);
        assertTrue("Fetched Data did not match stored data", Arrays.equals(data, fetchedData));

        // Remove the data item from the DSM
        dsm.removeData(dataID, containerID);

        // Verify the DSM no longer has the data item
        hasData = dsm.hasData(dataID, containerID);
        assertFalse("DataStorageManage should not have the data item", hasData);

        // Attempt to fetch the remove data item and verify it fails
        try {
            fetchedData = dsm.fetchData(dataID, containerID, null);
            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void saveFetchRemoveDataStream() throws Exception {

        // Setup and configure the Data Storage Manager
        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
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

        // Make sure the DSM doesn't contain the data item.
        hasData = dsm.hasData(dataID, containerID);
        assertFalse("DataStorageManage should not have the data item", hasData);

        // Dave the data item in the DSM
        dsm.saveData(dataID, containerID, dataHash, dataInputStream);

        // Verify that the DSM now has the data item
        hasData = dsm.hasData(dataID, containerID);
        assertTrue("DataStorageManage should have the data item", hasData);

        // Fetch the data item from the DSM and verify it is correct
        dataOutputStream = new ByteArrayOutputStream();
        dsm.fetchData(dataID, containerID, dataHash, dataOutputStream);
        dataOutputStream.close();
        byte[] fetchedData = ((ByteArrayOutputStream) dataOutputStream).toByteArray();
        assertNotNull("No Data was returned", fetchedData);
        assertTrue("Fetched Data did not match stored data", Arrays.equals(data, fetchedData));

        // Remove the data item from the DSM
        dsm.removeData(dataID, containerID);

        // Verify the DSM no longer has the data item
        hasData = dsm.hasData(dataID, containerID);
        assertFalse("DataStorageManage should not have the data item", hasData);

        // Attempt to fetch the remove data item and verify it fails
        try {
            dataOutputStream = new ByteArrayOutputStream();
            dsm.fetchData(dataID, containerID, null, dataOutputStream);
            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testFetchNonExistentByteArray() throws Exception {
        // Setup and configure the Data Storage Manager
        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        try {
            byte[] fetchedData = dsm.fetchData(dataID, containerID, null);
            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testFetchNonExistentDataStream() throws Exception {
        // Setup and configure the Data Storage Manager
        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        try {
            ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
            dsm.fetchData(dataID, containerID, null, dataOutputStream);

            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testSaveDuplicateByteArray() throws Exception {
        // Setup and configure the Data Storage Manager
        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
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

        // Save initial copy of the data
        dsm.saveData(dataID, containerID, dataHash, data);

        // Attempt to save it again
        try {
            dsm.saveData(dataID, containerID, dataHash, data);

            fail("Expected DataItemAlreadyExistsException was not thrown");
        } catch (DataItemAlreadyExistsException e) {
            // NOOP
        }
    }

    @Test
    public void testSaveDuplicateDataStream() throws Exception {
        // Setup and configure the Data Storage Manager
        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
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

        // Save initial copy of the data
        dsm.saveData(dataID, containerID, dataHash, data);

        // Attempt to save it again
        try {
            ByteArrayInputStream dataInputStream = new ByteArrayInputStream(data);
            dsm.saveData(dataID, containerID, dataHash, data);

            fail("Expected DataItemAlreadyExistsException was not thrown");
        } catch (DataItemAlreadyExistsException e) {
            // NOOP
        }
    }

    @Test
    public void testSaveByteArrayWithCorruptHash() throws Exception {
        // Setup and configure the Data Storage Manager
        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        byte[] data;
        String dataHash;

        // Create the data and hash it
        data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);
        dataHash = HashUtilities.generateHash("SHA-256", "Wrong Data".getBytes());

        // Attempt to save the data item with a bad hash
        try {
            dsm.saveData(dataID, containerID, dataHash, data);

            fail("Expected CorruptDataItemException was not thrown");
        } catch (CorruptDataItemException e) {
            // NOOP
        }
    }

    @Test
    public void testSaveDataStreamWithCorruptHash() throws Exception {
        // Setup and configure the Data Storage Manager
        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
        dsm.initialize();

        String dataID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

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
            dsm.saveData(dataID, containerID, dataHash, dataStream);

            fail("Expected CorruptDataItemException was not thrown");
        } catch (CorruptDataItemException e) {
            // NOOP
        }
    }

    @Test
    public void testFetchByteArrayWithCorruptHash() throws Exception {
        // Setup and configure the Data Storage Manager
        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
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
        String badHash = HashUtilities.generateHash("SHA-256", "Wrong Data".getBytes());

        // Write the data into the Data Store
        ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
        dsm.saveData(dataID, containerID, dataHash, dataStream);

        // Attempt to save the data item with a bad hash
        try {
            byte[] fetchedData = dsm.fetchData(dataID, containerID, badHash) ;
            fail("Expected CorruptDataItemException was not thrown");
        } catch (CorruptDataItemException e) {
            // NOOP
        }
    }

    @Test
    public void testFetchDataStreamWithCorruptHash() throws Exception {
        // Setup and configure the Data Storage Manager
        MemoryDataStorageProvider dsp = new MemoryDataStorageProvider();
        DataStorageManager dsm = new DataStorageManager();
        dsm.setDataStorageProvider(dsp);
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
        String badHash = HashUtilities.generateHash("SHA-256", "Wrong Data".getBytes());

        // Write the data into the Data Store
        ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
        dsm.saveData(dataID, containerID, dataHash, dataStream);

        // Attempt to save the data item with a bad hash
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            dsm.fetchData(dataID, containerID, badHash, outputStream) ;
            fail("Expected CorruptDataItemException was not thrown");
        } catch (CorruptDataItemException e) {
            // NOOP
        }
    }
}