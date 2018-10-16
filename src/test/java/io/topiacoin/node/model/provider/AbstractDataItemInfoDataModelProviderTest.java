package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataItemInfo;
import io.topiacoin.node.model.DataModel;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public abstract class AbstractDataItemInfoDataModelProviderTest {

    public abstract DataModel getDataModel();

    @Test
    public void testDataItemInfoCRUD() throws Exception {
        DataItemInfo testDataItem = new DataItemInfo();
        testDataItem.setId("An ID");
        testDataItem.setSize(1234);
        testDataItem.setDataHash("aHash");

        DataModel dataModel = getDataModel();

        DataItemInfo fetchedDataItem = dataModel.getDataItem("An ID");
        assertNull(fetchedDataItem);

        DataItemInfo createdDataItem = dataModel.createDataItem(testDataItem.getId(), testDataItem.getSize(), testDataItem.getDataHash());

        fetchedDataItem = dataModel.getDataItem(testDataItem.getId());

        assertEquals(createdDataItem, testDataItem);
        assertEquals(testDataItem, fetchedDataItem);
        assertEquals(fetchedDataItem, createdDataItem);

        assertEquals("An ID", testDataItem.getId());
        assertEquals(1234, testDataItem.getSize());
        assertEquals("aHash", testDataItem.getDataHash());

        assertEquals("An ID", createdDataItem.getId());
        assertEquals(1234, createdDataItem.getSize());
        assertEquals("aHash", createdDataItem.getDataHash());

        assertEquals("An ID", fetchedDataItem.getId());
        assertEquals(1234, fetchedDataItem.getSize());
        assertEquals("aHash", fetchedDataItem.getDataHash());

        testDataItem.setSize(5678);
        testDataItem.setDataHash("shazam");

        dataModel.updateDataItem(testDataItem);

        fetchedDataItem = dataModel.getDataItem(testDataItem.getId());
        assertEquals("An ID", fetchedDataItem.getId());
        assertEquals(5678, fetchedDataItem.getSize());
        assertEquals("shazam", fetchedDataItem.getDataHash());
    }

    @Test
    public void testModifyingDataItemInfoObjectsDoesNotModifyModel() throws Exception {
        DataItemInfo testDataItem = new DataItemInfo();
        testDataItem.setId("An ID");
        testDataItem.setSize(1234);
        testDataItem.setDataHash("aHash");

        DataModel dataModel = getDataModel();

        DataItemInfo fetchedDataItem = dataModel.getDataItem("An ID");
        assertNull(fetchedDataItem);

        DataItemInfo createdDataItem = dataModel.createDataItem(testDataItem.getId(), testDataItem.getSize(), testDataItem.getDataHash());

        fetchedDataItem = dataModel.getDataItem(testDataItem.getId());
        fetchedDataItem.setSize(5678);
        fetchedDataItem.setDataHash("shazam");

        DataItemInfo fetchedDataItem2 = dataModel.getDataItem(testDataItem.getId());
        assertNotEquals(fetchedDataItem, fetchedDataItem2);
    }

    @Test(expected = DataItemAlreadyExistsException.class)
    public void testCreateDuplicateDataItem() throws Exception {
        DataItemInfo testDataItem = new DataItemInfo();
        testDataItem.setId("An ID");
        testDataItem.setSize(1234);
        testDataItem.setDataHash("aHash");

        DataModel dataModel = getDataModel();

        dataModel.createDataItem(testDataItem.getId(), testDataItem.getSize(), testDataItem.getDataHash());
        dataModel.createDataItem(testDataItem.getId(), testDataItem.getSize(), testDataItem.getDataHash());
    }

    @Test(expected = NoSuchDataItemException.class)
    public void testUpdateNonExistentDataItem() throws Exception {
        DataItemInfo testDataItem = new DataItemInfo();
        testDataItem.setId("An ID");
        testDataItem.setSize(1234);
        testDataItem.setDataHash("aHash");

        DataModel dataModel = getDataModel();

        dataModel.updateDataItem(testDataItem);
    }

    @Test
    public void testAddRemoveDataItemInContainer() throws Exception {

        String containerID = "baz";
        String id = "foo";
        long size = 123456;
        String dataHash = "SHA-256:beefbeef";

        DataModel dataModel = getDataModel();

        ContainerInfo containerInfo = dataModel.createContainer(containerID, 0, null);
        DataItemInfo dataItemInfo = dataModel.createDataItem(id, size, dataHash);

        assertFalse(dataModel.isDataItemInContainer(id, containerID));

        dataModel.addDataItemToContainer(id, containerID);

        assertTrue(dataModel.isDataItemInContainer(id, containerID));

        assertTrue(dataModel.isDataItemInAnyContainer(id)) ;

        dataModel.removeDataItemFromContainer(id, containerID);

        assertFalse(dataModel.isDataItemInContainer(id, containerID));

        assertFalse(dataModel.isDataItemInAnyContainer(id)) ;
    }

    @Test
    public void testIsDataItemInAnyContainer() throws Exception {
        String containerID1 = "foo";
        String containerID2 = "bar";
        String id = "foo";
        long size = 123456;
        String dataHash = "SHA-256:beefbeef";

        DataModel dataModel = getDataModel();

        ContainerInfo containerInfo1 = dataModel.createContainer(containerID1, 0, null);
        ContainerInfo containerInfo2 = dataModel.createContainer(containerID2, 0, null);
        DataItemInfo dataItemInfo = dataModel.createDataItem(id, size, dataHash);

        assertFalse(dataModel.isDataItemInContainer(id, containerID1));
        assertFalse(dataModel.isDataItemInContainer(id, containerID2));
        assertFalse(dataModel.isDataItemInAnyContainer(id)) ;

        dataModel.addDataItemToContainer(id, containerID1);

        assertTrue(dataModel.isDataItemInContainer(id, containerID1));
        assertFalse(dataModel.isDataItemInContainer(id, containerID2));
        assertTrue(dataModel.isDataItemInAnyContainer(id)) ;

        dataModel.addDataItemToContainer(id, containerID2);

        assertTrue(dataModel.isDataItemInContainer(id, containerID1));
        assertTrue(dataModel.isDataItemInContainer(id, containerID2));
        assertTrue(dataModel.isDataItemInAnyContainer(id)) ;

        dataModel.removeDataItemFromContainer(id, containerID1);

        assertFalse(dataModel.isDataItemInContainer(id, containerID1));
        assertTrue(dataModel.isDataItemInContainer(id, containerID2));
        assertTrue(dataModel.isDataItemInAnyContainer(id)) ;

        dataModel.removeDataItemFromContainer(id, containerID2);

        assertFalse(dataModel.isDataItemInContainer(id, containerID1));
        assertFalse(dataModel.isDataItemInContainer(id, containerID2));
        assertFalse(dataModel.isDataItemInAnyContainer(id)) ;

    }

    @Test
    public void testAddDuplicateDataItemToContainer() throws Exception {

        String containerID = "baz";
        String id = "foo";
        long size = 123456;
        String dataHash = "SHA-256:beefbeef";

        DataModel dataModel = getDataModel();

        dataModel.createDataItem(id, size, dataHash);
        dataModel.createContainer(containerID, 0, null);

        dataModel.addDataItemToContainer(id, containerID);

        try {
            dataModel.addDataItemToContainer(id, containerID);
            fail("Expected DataItemAlreadyExistsException not thrown");
        } catch (DataItemAlreadyExistsException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testAddDataItemToNonExistentContainer() throws Exception {

        String containerID = "baz";
        String id = "foo";
        long size = 123456;
        String dataHash = "SHA-256:beefbeef";

        DataModel dataModel = getDataModel();

        DataItemInfo dataItemInfo = dataModel.createDataItem(id, size, dataHash);

        try {
            dataModel.addDataItemToContainer(id, containerID);
            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testAddNonExistentDataItemToContainer() throws Exception {

        String containerID = "baz";
        String id = "foo";
        long size = 123456;
        String dataHash = "SHA-256:beefbeef";

        DataModel dataModel = getDataModel();

        ContainerInfo containerInfo = dataModel.createContainer(containerID, 0, null);

        try {
            dataModel.addDataItemToContainer(id, containerID);
            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testRemoveDataItemFromNonExistentContainer() throws Exception {

        String chunkID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();

        dataModel.createDataItem(chunkID, 100, "foo");

        try {
            dataModel.removeDataItemFromContainer(chunkID, containerID);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Execpted Exception
        }
    }

    @Test
    public void testRemoveNonExistentDataItemFromContainer() throws Exception {

        String chunkID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();

        dataModel.createContainer(containerID, 0, null);

        boolean removed = dataModel.removeDataItemFromContainer(chunkID, containerID);
        assertFalse(removed);
    }

    @Test
    public void testContainerHasNonExistentDataItem() throws Exception {

        String chunkID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();

        dataModel.createContainer(containerID, 0, null);

        boolean hasData = dataModel.isDataItemInContainer(chunkID, containerID);

        assertFalse(hasData);
    }

    @Test
    public void testNonExistentContainerHasDataItem() throws Exception {

        String chunkID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();

        dataModel.createDataItem(chunkID, 100, "foo");

        try {
            dataModel.isDataItemInContainer(chunkID, containerID);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Execpted Exception
        }
    }


    @Test
    public void testRemoveItem() throws Exception {
        DataModel dataModel = getDataModel();

        String chunkID = UUID.randomUUID().toString();

        dataModel.createDataItem(chunkID, 100, "hash");

        boolean removed = dataModel.removeDataItem(chunkID);
        assertTrue(removed);

        DataItemInfo info = dataModel.getDataItem(chunkID);
        assertNull(info);
    }

    @Test
    public void testRemoveNonExistentDataItem() throws Exception {
        DataModel dataModel = getDataModel();

        String chunkID = UUID.randomUUID().toString();

        boolean removed = dataModel.removeDataItem(chunkID);
        assertFalse(removed);
    }

    @Test
    public void testRemoveItems() throws Exception {
        DataModel dataModel = getDataModel();

        String containerID = UUID.randomUUID().toString();

        String chunkID = UUID.randomUUID().toString();
        String chunkID2 = UUID.randomUUID().toString();
        String chunkID3 = UUID.randomUUID().toString();
        String chunkID4 = UUID.randomUUID().toString();

        dataModel.createContainer(containerID, 0, null);

        dataModel.createDataItem(chunkID, 100, "hash");
        dataModel.createDataItem(chunkID2, 100, "hash");
        dataModel.createDataItem(chunkID3, 100, "hash");
        dataModel.createDataItem(chunkID4, 100, "hash");

        dataModel.addDataItemToContainer(chunkID, containerID);
        dataModel.addDataItemToContainer(chunkID2, containerID);
        dataModel.addDataItemToContainer(chunkID3, containerID);
        dataModel.addDataItemToContainer(chunkID4, containerID);

        boolean removed = dataModel.removeDataItems(containerID);
        assertTrue(removed);

        List<DataItemInfo> infoList = dataModel.getDataItems(containerID);

        assertNotNull(infoList);
        assertEquals(0, infoList.size());
    }

    @Test
    public void testRemoveItemsWhenNoItemsAvailable() throws Exception {
        DataModel dataModel = getDataModel();

        String containerID = UUID.randomUUID().toString();

        dataModel.createContainer(containerID, 0, null);

        boolean removed = dataModel.removeDataItems(containerID);
        assertFalse(removed);

        List<DataItemInfo> infoList = dataModel.getDataItems(containerID);

        assertNotNull(infoList);
        assertEquals(0, infoList.size());
    }

    @Test
    public void testRemoveItemsFromNonExistentContainer() throws Exception {
        DataModel dataModel = getDataModel();

        String containerID = UUID.randomUUID().toString();

        try {
            dataModel.removeDataItems(containerID);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testGetItems() throws Exception {
        DataModel dataModel = getDataModel();

        String containerID = UUID.randomUUID().toString();

        String chunkID = UUID.randomUUID().toString();
        String chunkID2 = UUID.randomUUID().toString();
        String chunkID3 = UUID.randomUUID().toString();
        String chunkID4 = UUID.randomUUID().toString();

        dataModel.createContainer(containerID, 0, null);

        dataModel.createDataItem(chunkID, 100, "hash");
        dataModel.createDataItem(chunkID2, 100, "hash");
        dataModel.createDataItem(chunkID3, 100, "hash");
        dataModel.createDataItem(chunkID4, 100, "hash");

        dataModel.addDataItemToContainer(chunkID, containerID);
        dataModel.addDataItemToContainer(chunkID2, containerID);
        dataModel.addDataItemToContainer(chunkID3, containerID);
        dataModel.addDataItemToContainer(chunkID4, containerID);

        List<DataItemInfo> infoList = dataModel.getDataItems(containerID);

        assertNotNull(infoList);
        assertEquals(4, infoList.size());
    }

    @Test
    public void testGetItemsFromNonExistentContainer() throws Exception {
        DataModel dataModel = getDataModel();

        String containerID = UUID.randomUUID().toString();

        try {
            dataModel.getDataItems(containerID);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

    }
}
