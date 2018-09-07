package io.topiacoin.node.storage.provider;

import io.topiacoin.node.exceptions.NoSuchDataItemException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static junit.framework.TestCase.*;

public abstract class AbstractDataStorageProviderTest {

    protected abstract DataStorageProvider getDataStorageProvider() ;

    @Test
    public void testSaveFetchRemoveDataStream() throws Exception {

        DataStorageProvider dsp = getDataStorageProvider();

        String dataID = UUID.randomUUID().toString();
        byte[] data = new byte[16384] ;
        Random random = new Random();
        random.nextBytes(data);
        boolean hasData = false ;
        InputStream dataInputStream = new ByteArrayInputStream(data);

        hasData = dsp.hasData(dataID) ;
        assertFalse("The Data Storage Provider should not have the data item", hasData);

        dsp.saveData(dataID, dataInputStream);

        hasData = dsp.hasData(dataID) ;
        assertTrue("The Data Storage Provider should have the data item", hasData);

        ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
        dsp.fetchData(dataID, dataOutputStream);
        dataOutputStream.close();
        byte[] fetchedData = dataOutputStream.toByteArray();
        assertNotNull ( fetchedData ) ;
        assertTrue ( "Fetched Data does not match stored data", Arrays.equals(data, fetchedData));

        boolean didRemove = dsp.removeData(dataID);
        assertTrue ( "Did not remove the data", didRemove);

        hasData = dsp.hasData(dataID) ;
        assertFalse("The Data Storage Provider should not have the data item", hasData);

        try {
            dataOutputStream = new ByteArrayOutputStream();
            dsp.fetchData(dataID, dataOutputStream);
            fail ( "Expected NoSuchDataItemException not thrown" ) ;
        } catch( NoSuchDataItemException e ) {
            // NOOP - Expected Exception
        } finally {
            dataOutputStream.close();
        }
    }

    @Test
    public void testSaveDataFromClosedInputStream() throws Exception {
        DataStorageProvider dsp = getDataStorageProvider();

        String dataID = UUID.randomUUID().toString();
        byte[] data = new byte[16384];
        FileInputStream dataInputStream = new FileInputStream("./pom.xml") ;
        dataInputStream.close();

        try {
            dsp.saveData(dataID, dataInputStream);
            fail ( "Expected IOException not thrown when attempting to save from closed InputStream");
        } catch (IOException e ) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testFetchDataItem() throws Exception {

        DataStorageProvider dsp = getDataStorageProvider();

        String dataID = UUID.randomUUID().toString();
        byte[] data = new byte[16384] ;
        Random random = new Random();
        random.nextBytes(data);
        boolean hasData = false ;
        InputStream dataInputStream = new ByteArrayInputStream(data);

        try {
            dsp.saveData(dataID, dataInputStream);

            ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
            dsp.fetchData(dataID, dataOutputStream);
            dataOutputStream.close();
            byte[] fetchedData = dataOutputStream.toByteArray();
            assertNotNull(fetchedData);
            assertTrue("Fetched Data does not match stored data", Arrays.equals(data, fetchedData));
        } finally {
            dsp.removeData(dataID);
        }
    }

    @Test
    public void testFetchSubsetOfDataItem() throws Exception {

        DataStorageProvider dsp = getDataStorageProvider();

        String dataID = UUID.randomUUID().toString();
        byte[] data = new byte[16384] ;
        Random random = new Random();
        random.nextBytes(data);
        boolean hasData = false ;
        InputStream dataInputStream = new ByteArrayInputStream(data);

        int offset = 1000 ;
        int length = 3000 ;

        try {
            dsp.saveData(dataID, dataInputStream);

            ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
            dsp.fetchData(dataID, offset, length, dataOutputStream);
            dataOutputStream.close();
            byte[] fetchedData = dataOutputStream.toByteArray();
            assertNotNull(fetchedData);
            assertTrue("Fetched Data does not match stored data", Arrays.equals(Arrays.copyOfRange(data, offset, offset + length), fetchedData));
        } finally {
            dsp.removeData(dataID);
        }
    }

    @Test
    public void testFetchNonExistentDataItem() throws Exception {

        DataStorageProvider dsp = getDataStorageProvider();

        String dataID = UUID.randomUUID().toString();

        try {
            ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
            dsp.fetchData(dataID, dataOutputStream);
            dataOutputStream.close();
            fail ( "Expected NoSuchDataItemException was not thrown when fetching a non-existent data item");
        } catch ( NoSuchDataItemException e ) {
            // NOOP - Expected Exception
        }
    }


    @Test
    public void testFetchSubsetOfNonExistentDataItem() throws Exception {

        DataStorageProvider dsp = getDataStorageProvider();

        String dataID = UUID.randomUUID().toString();

        int offset = 1000 ;
        int length = 3000 ;

        try {
            ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
            dsp.fetchData(dataID, offset, length, dataOutputStream);
            dataOutputStream.close();
            fail ( "Expected NoSuchDataItemException was not thrown when fetching a subset of a non-existent data item");
        } catch ( NoSuchDataItemException e ) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testFetchDataToClosedOutputStream() throws Exception {
        DataStorageProvider dsp = getDataStorageProvider();

        String dataID = UUID.randomUUID().toString();
        byte[] data = new byte[16384] ;
        Random random = new Random();
        random.nextBytes(data);
        boolean hasData = false ;
        InputStream dataInputStream = new ByteArrayInputStream(data);

        dsp.saveData(dataID, dataInputStream);

        try {
            FileOutputStream dataOutputStream = new FileOutputStream("./temp");
            dataOutputStream.close();
            dsp.fetchData(dataID, dataOutputStream);
            fail("Expected IOException not thrown when fetching to closed OutputStream");
        } catch ( IOException e ) {
            // NOOP - Expected Exception
        } finally {
            new File("./temp").delete();
        }
    }

    @Test
    public void testFetchSubsetOfDataToClosedOutputStream() throws Exception {
        DataStorageProvider dsp = getDataStorageProvider();

        String dataID = UUID.randomUUID().toString();
        byte[] data = new byte[16384] ;
        Random random = new Random();
        random.nextBytes(data);
        boolean hasData = false ;
        InputStream dataInputStream = new ByteArrayInputStream(data);

        int offset = 1000;
        int length = 3000;

        dsp.saveData(dataID, dataInputStream);

        try {
            FileOutputStream dataOutputStream = new FileOutputStream("./temp");
            dataOutputStream.close();
            dsp.fetchData(dataID, offset, length, dataOutputStream);
            fail("Expected IOException not thrown when fetching to closed OutputStream");
        } catch ( IOException e ) {
            // NOOP - Expected Exception
        } finally {
            new File("./temp").delete();
        }
    }


    @Test
    public void testRemoveDataItem() throws Exception {

        DataStorageProvider dsp = getDataStorageProvider();

        String dataID = UUID.randomUUID().toString();
        byte[] data = new byte[16384] ;
        Random random = new Random();
        random.nextBytes(data);
        boolean hasData = false ;
        InputStream dataInputStream = new ByteArrayInputStream(data);

        dsp.saveData(dataID, dataInputStream);

        boolean didRemove = dsp.removeData(dataID) ;
        assertTrue("Should have successfully removed the data item", didRemove);
    }

    @Test
    public void testRemoveNonExistentDataItem() throws Exception {

        DataStorageProvider dsp = getDataStorageProvider();

        String dataID = UUID.randomUUID().toString();

        boolean didRemove = dsp.removeData(dataID) ;
        assertFalse("Should not have successfully removed a non-existent data item", didRemove);
    }

    @Test
    public void testHasDataItem() throws Exception {
        DataStorageProvider dsp = getDataStorageProvider();

        String dataID = UUID.randomUUID().toString();
        byte[] data = new byte[16384] ;
        Random random = new Random();
        random.nextBytes(data);
        boolean hasData = false ;
        InputStream dataInputStream = new ByteArrayInputStream(data);

        dsp.saveData(dataID, dataInputStream);

        hasData = dsp.hasData(dataID) ;
        assertTrue ( "DataStorageProvider should have the data", hasData);
    }

    @Test
    public void testHasNonExistentDataItem() throws Exception {
        DataStorageProvider dsp = getDataStorageProvider();

        String dataID = UUID.randomUUID().toString();

        boolean hasData = dsp.hasData(dataID) ;
        assertFalse ( "DataStorageProvider should have the data", hasData);
    }
}
