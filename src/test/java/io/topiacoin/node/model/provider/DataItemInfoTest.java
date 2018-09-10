package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.model.DataItemInfo;
import io.topiacoin.node.model.DataModel;
import org.junit.After;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public abstract class DataItemInfoTest {

    public abstract DataModel initDataModel();

    public abstract void tearDownDataModel();

    @After
    public void destroy() {
        tearDownDataModel();
    }

    @Test
    public void testDataItemInfoCRUD() throws Exception {
        DataItemInfo testDataItem = new DataItemInfo();
        testDataItem.setId("An ID");
        testDataItem.setContainerID("Another ID");
        testDataItem.setSize(1234);
        testDataItem.setDataHash("aHash");

        DataModel dataModel = initDataModel();

        try {
            dataModel.getDataItem("An ID");
            fail();
        } catch (NoSuchDataItemException e) {
            //Good
        }

        DataItemInfo createdDataItem = dataModel.createDataItem(testDataItem.getId(), testDataItem.getContainerID(), testDataItem.getSize(), testDataItem.getDataHash());

        DataItemInfo fetchedDataItem = dataModel.getDataItem(testDataItem.getId());

        assertEquals(createdDataItem, testDataItem);
        assertEquals(testDataItem, fetchedDataItem);
        assertEquals(fetchedDataItem, createdDataItem);

        assertEquals("An ID", testDataItem.getId());
        assertEquals("Another ID", testDataItem.getContainerID());
        assertEquals(1234, testDataItem.getSize());
        assertEquals("aHash", testDataItem.getDataHash());

        assertEquals("An ID", createdDataItem.getId());
        assertEquals("Another ID", createdDataItem.getContainerID());
        assertEquals(1234, createdDataItem.getSize());
        assertEquals("aHash", createdDataItem.getDataHash());

        assertEquals("An ID", fetchedDataItem.getId());
        assertEquals("Another ID", fetchedDataItem.getContainerID());
        assertEquals(1234, fetchedDataItem.getSize());
        assertEquals("aHash", fetchedDataItem.getDataHash());

        testDataItem.setContainerID("new ID");
        testDataItem.setSize(5678);
        testDataItem.setDataHash("shazam");

        dataModel.updateDataItem(testDataItem);

        fetchedDataItem = dataModel.getDataItem(testDataItem.getId());
        assertEquals("An ID", fetchedDataItem.getId());
        assertEquals("new ID", fetchedDataItem.getContainerID());
        assertEquals(5678, fetchedDataItem.getSize());
        assertEquals("shazam", fetchedDataItem.getDataHash());
    }

    @Test
    public void testModifyingDataItemInfoObjectsDoesNotModifyModel() throws Exception {
        DataItemInfo testDataItem = new DataItemInfo();
        testDataItem.setId("An ID");
        testDataItem.setContainerID("Another ID");
        testDataItem.setSize(1234);
        testDataItem.setDataHash("aHash");

        DataModel dataModel = initDataModel();

        try {
            dataModel.getDataItem("An ID");
            fail();
        } catch (NoSuchDataItemException e) {
            //Good
        }

        DataItemInfo createdDataItem = dataModel.createDataItem(testDataItem.getId(), testDataItem.getContainerID(), testDataItem.getSize(), testDataItem.getDataHash());

        DataItemInfo fetchedDataItem = dataModel.getDataItem(testDataItem.getId());
        fetchedDataItem.setContainerID("new ID");
        fetchedDataItem.setSize(5678);
        fetchedDataItem.setDataHash("shazam");

        DataItemInfo fetchedDataItem2 = dataModel.getDataItem(testDataItem.getId());
        assertNotEquals(fetchedDataItem, fetchedDataItem2);
    }

    @Test(expected = DataItemAlreadyExistsException.class)
    public void testCreateDuplicateContainer() throws Exception {
        DataItemInfo testDataItem = new DataItemInfo();
        testDataItem.setId("An ID");
        testDataItem.setContainerID("Another ID");
        testDataItem.setSize(1234);
        testDataItem.setDataHash("aHash");

        DataModel dataModel = initDataModel();

        dataModel.createDataItem(testDataItem.getId(), testDataItem.getContainerID(), testDataItem.getSize(), testDataItem.getDataHash());
        dataModel.createDataItem(testDataItem.getId(), testDataItem.getContainerID(), testDataItem.getSize(), testDataItem.getDataHash());
    }

    @Test(expected = NoSuchDataItemException.class)
    public void testUpdateNonExistentContainer() throws Exception {
        DataItemInfo testDataItem = new DataItemInfo();
        testDataItem.setId("An ID");
        testDataItem.setContainerID("Another ID");
        testDataItem.setSize(1234);
        testDataItem.setDataHash("aHash");

        DataModel dataModel = initDataModel();

        dataModel.updateDataItem(testDataItem);
    }
}
