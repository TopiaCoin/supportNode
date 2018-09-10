package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataModel;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public abstract class ContainerInfoTest {

    public abstract DataModel initDataModel();

    public abstract void tearDownDataModel();

    @After
    public void destroy() {
        tearDownDataModel();
    }

    @Test
    public void testContainerInfoCRUD() throws Exception {
        ContainerInfo testContainer = new ContainerInfo();
        testContainer.setId("An ID");
        testContainer.setExpirationDate(11111L);

        DataModel dataModel = initDataModel();

        try {
            dataModel.getContainer("An ID");
            fail();
        } catch (NoSuchContainerException e) {
            //Good
        }

        ContainerInfo createdContainer = dataModel.createContainer(testContainer.getId(), testContainer.getExpirationDate());

        ContainerInfo fetchedContainer = dataModel.getContainer(testContainer.getId());

        assertEquals(createdContainer, testContainer);
        assertEquals(testContainer, fetchedContainer);
        assertEquals(fetchedContainer, createdContainer);

        assertEquals("An ID", testContainer.getId());
        assertEquals(11111L, testContainer.getExpirationDate());
        assertEquals("An ID", createdContainer.getId());
        assertEquals(11111L, createdContainer.getExpirationDate());
        assertEquals("An ID", fetchedContainer.getId());
        assertEquals(11111L, fetchedContainer.getExpirationDate());

        testContainer.setExpirationDate(12345L);

        dataModel.updateContainer(testContainer);

        fetchedContainer = dataModel.getContainer(testContainer.getId());
        assertEquals("An ID", fetchedContainer.getId());
        assertEquals(12345L, fetchedContainer.getExpirationDate());
    }

    @Test
    public void testModifyingContainerInfoObjectsDoesNotModifyModel() throws Exception {
        ContainerInfo testContainer = new ContainerInfo();
        testContainer.setId("An ID");
        testContainer.setExpirationDate(11111L);

        DataModel dataModel = initDataModel();

        try {
            dataModel.getContainer("An ID");
            fail();
        } catch (NoSuchContainerException e) {
            //Good
        }

        ContainerInfo createdContainer = dataModel.createContainer(testContainer.getId(), testContainer.getExpirationDate());

        ContainerInfo fetchedContainer = dataModel.getContainer(testContainer.getId());
        fetchedContainer.setExpirationDate(12345L);

        ContainerInfo fetchedContainer2 = dataModel.getContainer(testContainer.getId());
        assertNotEquals(fetchedContainer, fetchedContainer2);
    }

    @Test(expected = ContainerAlreadyExistsException.class)
    public void testCreateDuplicateContainer() throws Exception {
        ContainerInfo testContainer = new ContainerInfo();
        testContainer.setId("An ID");
        testContainer.setExpirationDate(11111L);

        DataModel dataModel = initDataModel();

        dataModel.createContainer(testContainer.getId(), testContainer.getExpirationDate());
        dataModel.createContainer(testContainer.getId(), testContainer.getExpirationDate());
    }

    @Test(expected = NoSuchContainerException.class)
    public void testUpdateNonExistentContainer() throws Exception {
        ContainerInfo testContainer = new ContainerInfo();
        testContainer.setId("An ID");
        testContainer.setExpirationDate(11111L);

        DataModel dataModel = initDataModel();

        dataModel.updateContainer(testContainer);
    }
}
