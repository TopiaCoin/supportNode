package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataItemInfo;
import io.topiacoin.node.model.DataModel;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AbstractDataModelDataItemInfoTest {

    public abstract DataModel getDataModel();

    @Test
    public void testDataItemInfoCRUD() throws Exception {
        DataItemInfo testDataItem = new DataItemInfo();
        testDataItem.setId("An ID");
        testDataItem.setSize(1234);
        testDataItem.setDataHash("aHash");

        DataModel dataModel = getDataModel();

        try {
            dataModel.getDataItem("An ID");
            fail();
        } catch (NoSuchDataItemException e) {
            //Good
        }

        DataItemInfo createdDataItem = dataModel.createDataItem(testDataItem.getId(), testDataItem.getSize(), testDataItem.getDataHash());

        DataItemInfo fetchedDataItem = dataModel.getDataItem(testDataItem.getId());

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

        try {
            dataModel.getDataItem("An ID");
            fail();
        } catch (NoSuchDataItemException e) {
            //Good
        }

        DataItemInfo createdDataItem = dataModel.createDataItem(testDataItem.getId(), testDataItem.getSize(), testDataItem.getDataHash());

        DataItemInfo fetchedDataItem = dataModel.getDataItem(testDataItem.getId());
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
        long size =123456 ;
        String dataHash = "SHA-256:beefbeef";

        DataModel dataModel = getDataModel();

        ContainerInfo containerInfo = dataModel.createContainer(containerID, 0, null);
        DataItemInfo dataItemInfo = dataModel.createDataItem(id, size, dataHash);;

        assertFalse ( dataModel.isDataItemInContainer(id, containerID));

        dataModel.addDataItemToContainer(id, containerID);

        assertTrue ( dataModel.isDataItemInContainer(id, containerID));

        dataModel.removeDataItemFromContainer(id, containerID);

        assertFalse ( dataModel.isDataItemInContainer(id, containerID));
    }

    @Test
    public void testAddDuplicateDataItemToContainer() throws Exception {

        String containerID = "baz";
        String id = "foo";
        long size =123456 ;
        String dataHash = "SHA-256:beefbeef";

        DataModel dataModel = getDataModel();

        dataModel.addDataItemToContainer(id, containerID);

        try {
            dataModel.addDataItemToContainer(id, containerID);
            fail ( "Expected DataItemAlreadyExistsException not thrown" ) ;
        } catch ( DataItemAlreadyExistsException e ) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testAddDataItemToNonExistentContainer() throws Exception {

        String containerID = "baz";
        String id = "foo";
        long size =123456 ;
        String dataHash = "SHA-256:beefbeef";

        DataModel dataModel = getDataModel();

        DataItemInfo dataItemInfo = dataModel.createDataItem(id, size, dataHash);

        try {
            dataModel.addDataItemToContainer(id, containerID);
            fail ( "Expected NoSuchDataItemException was not thrown" ) ;
        } catch ( NoSuchContainerException e ) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testAddNonExistentDataItemToContainer() throws Exception {

        String containerID = "baz";
        String id = "foo";
        long size =123456 ;
        String dataHash = "SHA-256:beefbeef";

        DataModel dataModel = getDataModel();

        ContainerInfo containerInfo = dataModel.createContainer(containerID, 0, null);

        try {
            dataModel.addDataItemToContainer(id, containerID);
            fail ( "Expected NoSuchDataItemException was not thrown" ) ;
        } catch ( NoSuchDataItemException e ) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testRemoveDataItemFromNonExistentContainer() throws Exception {
        fail ( "Test Not Yet Implemented" );
    }

    @Test
    public void testRemoveNonExistentDataItemFromContainer() throws Exception {
        fail ( "Test Not Yet Implemented" );
    }

    @Test
    public void testContainerHasNonExistentDataItem() throws Exception {
        fail ( "Test Not Yet Implemented" );
    }

    @Test
    public void testNonExistentContainerHasDataItem() throws Exception {
        fail ( "Test Not Yet Implemented" );
    }


}
