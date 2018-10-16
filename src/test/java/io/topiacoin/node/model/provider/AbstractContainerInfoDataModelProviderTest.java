package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataModel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public abstract class AbstractContainerInfoDataModelProviderTest {

    protected abstract DataModel getDataModel();

    @Test
    public void testContainerInfoCRUD() throws Exception {
        String containerID = "An ID";
        long expirationDate = 11111L;

        Challenge challenge = new Challenge(containerID, new ArrayList<>());

        ContainerInfo testContainer = new ContainerInfo();
        testContainer.setId(containerID);
        testContainer.setExpirationDate(expirationDate);
        testContainer.setChallenge(challenge);

        DataModel dataModel = getDataModel();

        ContainerInfo fetchedContainer = dataModel.getContainer(containerID);
        assertNull(fetchedContainer);

        ContainerInfo createdContainer = dataModel.createContainer(testContainer.getId(), testContainer.getExpirationDate(), testContainer.getChallenge());

        assertEquals(createdContainer, testContainer);
        assertEquals(containerID, createdContainer.getId());
        assertEquals(expirationDate, createdContainer.getExpirationDate());

        fetchedContainer = dataModel.getContainer(testContainer.getId());

        assertEquals(testContainer, fetchedContainer);
        assertEquals(createdContainer, fetchedContainer);
        assertEquals(containerID, fetchedContainer.getId());
        assertEquals(expirationDate, fetchedContainer.getExpirationDate());

        Challenge newChallenge = new Challenge(containerID, new ArrayList<>());
        testContainer.setExpirationDate(12345L);
        testContainer.setChallenge(newChallenge);

        dataModel.updateContainer(testContainer);

        fetchedContainer = dataModel.getContainer(testContainer.getId());
        assertEquals(containerID, fetchedContainer.getId());
        assertEquals(12345L, fetchedContainer.getExpirationDate());
        assertEquals(newChallenge, fetchedContainer.getChallenge());
    }

    @Test
    public void testModifyingContainerInfoObjectsDoesNotModifyModel() throws Exception {
        String containerID = "An ID";
        Challenge challenge = new Challenge(containerID, new ArrayList<>());

        ContainerInfo testContainer = new ContainerInfo();
        testContainer.setId(containerID);
        testContainer.setExpirationDate(11111L);
        testContainer.setChallenge(challenge);

        DataModel dataModel = getDataModel();

        ContainerInfo fetchedContainer = dataModel.getContainer(containerID);
        assertNull(fetchedContainer);

        ContainerInfo createdContainer = dataModel.createContainer(testContainer.getId(), testContainer.getExpirationDate(), testContainer.getChallenge());

        fetchedContainer = dataModel.getContainer(testContainer.getId());
        fetchedContainer.setExpirationDate(12345L);
        assertNotEquals(createdContainer, fetchedContainer);

        ContainerInfo fetchedContainer2 = dataModel.getContainer(testContainer.getId());
        assertNotEquals(fetchedContainer, fetchedContainer2);
    }

    @Test(expected = ContainerAlreadyExistsException.class)
    public void testCreateDuplicateContainer() throws Exception {
        String containerID = "An ID";
        Challenge challenge = new Challenge(containerID, new ArrayList<>());

        ContainerInfo testContainer = new ContainerInfo();
        testContainer.setId(containerID);
        testContainer.setExpirationDate(11111L);
        testContainer.setChallenge(challenge);

        DataModel dataModel = getDataModel();

        dataModel.createContainer(testContainer.getId(), testContainer.getExpirationDate(), testContainer.getChallenge());
        dataModel.createContainer(testContainer.getId(), testContainer.getExpirationDate(), testContainer.getChallenge());
    }

    @Test(expected = NoSuchContainerException.class)
    public void testUpdateNonExistentContainer() throws Exception {
        String containerID = "An ID";
        Challenge challenge = new Challenge(containerID, new ArrayList<>());

        ContainerInfo testContainer = new ContainerInfo();
        testContainer.setId("An ID");
        testContainer.setExpirationDate(11111L);
        testContainer.setChallenge(challenge);

        DataModel dataModel = getDataModel();

        dataModel.updateContainer(testContainer);
    }

    @Test
    public void testRemoveContainer() throws Exception {
        DataModel dataModel = getDataModel();

        String containerID = UUID.randomUUID().toString();

        dataModel.createContainer(containerID, 0, null);

        dataModel.removeContainer(containerID);

        ContainerInfo fetchedContainerInfo = dataModel.getContainer(containerID);
        assertNull(fetchedContainerInfo);
    }

    @Test
    public void testRemoveNonExistentContainer() throws Exception {
        DataModel dataModel = getDataModel();

        String containerID = UUID.randomUUID().toString();

        dataModel.removeContainer(containerID);
    }
}
