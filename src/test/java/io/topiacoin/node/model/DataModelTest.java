package io.topiacoin.node.model;

import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.exceptions.NoSuchMicroNetworkException;
import io.topiacoin.node.model.provider.DataModelProvider;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class DataModelTest {

    private DataModelProvider _dataModelProvider;
    private DataModel _dataModel;

    @Before
    public void setUp() throws Exception {
        _dataModelProvider = EasyMock.createMock(DataModelProvider.class);

        _dataModel = new DataModel();
        _dataModel.setProvider(_dataModelProvider);
        _dataModel.initialize();
    }

    @After
    public void tearDown() throws Exception {
        _dataModel.shutdown();
        _dataModel = null;

        _dataModelProvider = null;
    }

    @Test
    public void testCreateContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        long expiration = System.currentTimeMillis() + 600000;
        Challenge challenge = new Challenge();

        ContainerInfo containerInfo = new ContainerInfo(containerID, expiration, challenge);

        // Configure Mock Object Expectations
        EasyMock.expect(_dataModelProvider.createContainer(containerID, expiration, challenge)).andReturn(containerInfo);
        EasyMock.expect(_dataModelProvider.createContainer(containerID, expiration, challenge)).andThrow(new ContainerAlreadyExistsException());

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        ContainerInfo createdInfo = _dataModel.createContainer(containerID, expiration, challenge);
        try {
            _dataModel.createContainer(containerID, expiration, challenge);
            fail("Expected ContainerAlreadyExistsException was not thrown");
        } catch (ContainerAlreadyExistsException e) {
            // NOOP - Expected Exception
        }

        // Assert Results of the test
        assertEquals(containerID, createdInfo.getId());
        assertEquals(expiration, createdInfo.getExpirationDate());
        assertEquals(challenge, createdInfo.getChallenge());

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testUpdateContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        long expiration = System.currentTimeMillis() + 600000;
        Challenge challenge = new Challenge();

        ContainerInfo containerInfo = new ContainerInfo(containerID, expiration, challenge);

        // Configure Mock Object Expectations
        _dataModelProvider.updateContainer(containerInfo);
        EasyMock.expectLastCall();
        _dataModelProvider.updateContainer(containerInfo);
        EasyMock.expectLastCall().andThrow(new NoSuchContainerException());

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        _dataModel.updateContainer(containerInfo);
        try {
            _dataModel.updateContainer(containerInfo);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Assert Results of the test
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testGetContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        long expiration = System.currentTimeMillis() + 600000;
        Challenge challenge = new Challenge();

        ContainerInfo containerInfo = new ContainerInfo(containerID, expiration, challenge);

        // Configure Mock Object Expectations
        EasyMock.expect(_dataModelProvider.getContainer(containerID)).andReturn(containerInfo);
        EasyMock.expect(_dataModelProvider.getContainer(containerID)).andReturn(null);

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        ContainerInfo fetchedInfo1 = null;
        ContainerInfo fetchedInfo2 = null;
        fetchedInfo1 = _dataModel.getContainer(containerID);
        fetchedInfo2 = _dataModel.getContainer(containerID);

        // Assert Results of the test
        assertNotNull(fetchedInfo1);
        assertNull(fetchedInfo2);

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testRemoveContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        long expiration = System.currentTimeMillis() + 600000;
        Challenge challenge = new Challenge();

        ContainerInfo containerInfo = new ContainerInfo(containerID, expiration, challenge);

        // Configure Mock Object Expectations
        EasyMock.expect(_dataModelProvider.removeContainer(containerID)).andReturn(true);
        EasyMock.expect(_dataModelProvider.removeContainer(containerID)).andReturn(false);

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        boolean removed1 = _dataModel.removeContainer(containerID);
        boolean removed2 = _dataModel.removeContainer(containerID);

        // Assert Results of the test
        assertTrue(removed1);
        assertFalse(removed2);

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testAddDataToContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        long expiration = System.currentTimeMillis() + 600000;
        Challenge challenge = new Challenge();

        // Configure Mock Object Expectations
        _dataModelProvider.addDataItemToContainer(chunkID, containerID);
        EasyMock.expectLastCall();
        _dataModelProvider.addDataItemToContainer(chunkID, containerID);
        EasyMock.expectLastCall().andThrow(new NoSuchContainerException());
        _dataModelProvider.addDataItemToContainer(chunkID, containerID);
        EasyMock.expectLastCall().andThrow(new NoSuchDataItemException());
        _dataModelProvider.addDataItemToContainer(chunkID, containerID);
        EasyMock.expectLastCall().andThrow(new DataItemAlreadyExistsException());

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        _dataModel.addDataItemToContainer(chunkID, containerID);
        try {
            _dataModel.addDataItemToContainer(chunkID, containerID);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }
        try {
            _dataModel.addDataItemToContainer(chunkID, containerID);
            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }
        try {
            _dataModel.addDataItemToContainer(chunkID, containerID);
            fail("Expected DataItemAlreadyExistsException was not thrown");
        } catch (DataItemAlreadyExistsException e) {
            // NOOP - Expected Exception
        }

        // Assert Results of the test
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testRemoveDataItemFromContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        long expiration = System.currentTimeMillis() + 600000;
        Challenge challenge = new Challenge();

        // Configure Mock Object Expectations
        _dataModelProvider.removeDataItemFromContainer(chunkID, containerID);
        EasyMock.expectLastCall().andReturn(true);
        _dataModelProvider.removeDataItemFromContainer(chunkID, containerID);
        EasyMock.expectLastCall().andReturn(false);
        _dataModelProvider.removeDataItemFromContainer(chunkID, containerID);
        EasyMock.expectLastCall().andThrow(new NoSuchContainerException());

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        boolean result1 = false;
        boolean result2 = false;
        result1 = _dataModel.removeDataItemFromContainer(chunkID, containerID);
        result2 = _dataModel.removeDataItemFromContainer(chunkID, containerID);
        try {
            _dataModel.removeDataItemFromContainer(chunkID, containerID);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Assert Results of the test
        // -- None --
        assertTrue(result1);
        assertFalse(result2);

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testIsDataItemInContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        long expiration = System.currentTimeMillis() + 600000;
        Challenge challenge = new Challenge();

        // Configure Mock Object Expectations
        _dataModelProvider.isDataItemInContainer(chunkID, containerID);
        EasyMock.expectLastCall().andReturn(true);
        _dataModelProvider.isDataItemInContainer(chunkID, containerID);
        EasyMock.expectLastCall().andReturn(false);
        _dataModelProvider.isDataItemInContainer(chunkID, containerID);
        EasyMock.expectLastCall().andThrow(new NoSuchContainerException());

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        boolean result1 = false;
        boolean result2 = false;
        result1 = _dataModel.isDataItemInContainer(chunkID, containerID);
        result2 = _dataModel.isDataItemInContainer(chunkID, containerID);
        try {
            _dataModel.isDataItemInContainer(chunkID, containerID);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Assert Results of the test
        // -- None --
        assertTrue(result1);
        assertFalse(result2);

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testCreateDataItem() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        long size = 1233456L;
        String dataHash = "SHA-256:deadbeef";

        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, size, dataHash);

        // Configure Mock Object Expectations
        EasyMock.expect(_dataModelProvider.createDataItem(chunkID, size, dataHash)).andReturn(dataItemInfo);
        _dataModelProvider.createDataItem(chunkID, size, dataHash);
        EasyMock.expectLastCall().andThrow(new DataItemAlreadyExistsException());

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        DataItemInfo info;
        info = _dataModel.createDataItem(chunkID, size, dataHash);
        try {
            _dataModel.createDataItem(chunkID, size, dataHash);
            fail("Expected DataItemAlreadyExistsException was not thrown");
        } catch (DataItemAlreadyExistsException e) {
            // NOOP - Expected Exception
        }

        // Assert Results of the test
        // -- None --
        assertNotNull(info);
        assertEquals(chunkID, info.getId());
        assertEquals(size, info.getSize());
        assertEquals(dataHash, info.getDataHash());

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testUpdateDataItem() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        long size = 1233456L;
        String dataHash = "SHA-256:deadbeef";

        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, size, dataHash);

        // Configure Mock Object Expectations
        _dataModelProvider.updateDataItem(dataItemInfo);
        EasyMock.expectLastCall();
        _dataModelProvider.updateDataItem(dataItemInfo);
        EasyMock.expectLastCall().andThrow(new NoSuchDataItemException());

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        _dataModel.updateDataItem(dataItemInfo);
        try {
            _dataModel.updateDataItem(dataItemInfo);
            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }

        // Assert Results of the test
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }


    @Test
    public void testGetDataItem() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        long size = 1233456L;
        String dataHash = "SHA-256:deadbeef";

        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, size, dataHash);

        // Configure Mock Object Expectations
        EasyMock.expect(_dataModelProvider.getDataItem(chunkID)).andReturn(dataItemInfo);
        EasyMock.expect(_dataModelProvider.getDataItem(chunkID)).andReturn(null);

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        DataItemInfo info1 = _dataModel.getDataItem(chunkID);
        DataItemInfo info2 = _dataModel.getDataItem(chunkID);

        // Assert Results of the test
        // -- None --
        assertNotNull(info1);
        assertEquals(chunkID, info1.getId());
        assertEquals(size, info1.getSize());
        assertEquals(dataHash, info1.getDataHash());
        assertNull(info2);

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testGetDataItems() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        long size = 1233456L;
        String dataHash = "SHA-256:deadbeef";

        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, size, dataHash);
        List<DataItemInfo> infoList = new ArrayList<>();
        infoList.add(dataItemInfo);

        // Configure Mock Object Expectations
        EasyMock.expect(_dataModelProvider.getDataItems(containerID)).andReturn(infoList);
        _dataModelProvider.getDataItems(containerID);
        EasyMock.expectLastCall().andThrow(new NoSuchContainerException());

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        List<DataItemInfo> infos;
        infos = _dataModel.getDataItems(containerID);
        try {
            _dataModel.getDataItems(containerID);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Assert Results of the test
        // -- None --
        assertNotNull(infos);
        assertEquals(infoList.size(), infos.size());
        DataItemInfo info = infos.get(0);
        assertEquals(chunkID, info.getId());
        assertEquals(size, info.getSize());
        assertEquals(dataHash, info.getDataHash());

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testRemoveDataItem() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        long size = 1233456L;
        String dataHash = "SHA-256:deadbeef";

        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, size, dataHash);

        // Configure Mock Object Expectations
        EasyMock.expect(_dataModelProvider.removeDataItem(chunkID)).andReturn(true);
        EasyMock.expect(_dataModelProvider.removeDataItem(chunkID)).andReturn(false);

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        boolean removed1 = _dataModel.removeDataItem(chunkID);
        boolean removed2 = _dataModel.removeDataItem(chunkID);

        // Assert Results of the test
        assertTrue(removed1);
        assertFalse(removed2);

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testRemoveDataItems() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        long size = 1233456L;
        String dataHash = "SHA-256:deadbeef";

        // Configure Mock Object Expectations
        EasyMock.expect(_dataModelProvider.removeDataItems(containerID)).andReturn(true);
        EasyMock.expect(_dataModelProvider.removeDataItems(containerID)).andReturn(false);
        EasyMock.expect(_dataModelProvider.removeDataItems(containerID)).andThrow(new NoSuchContainerException());

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        boolean removed1 = _dataModel.removeDataItems(containerID);
        boolean removed2 = _dataModel.removeDataItems(containerID);
        try {
            _dataModel.removeDataItems(containerID);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Assert Results of the test
        assertTrue(removed1);
        assertFalse(removed2);

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }


    @Test
    public void testCreateMicroNetwork() throws Exception {

        // Create Test Data
        String netID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();
        String path = "/dev/null";
        MicroNetworkState state = MicroNetworkState.STARTING;
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";

        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(netID, containerID, path, state, rpcURL, p2pURL);

        // Configure Mock Object Expectations
        EasyMock.expect(_dataModelProvider.createMicroNetwork(netID, containerID, path, state, rpcURL, p2pURL)).andReturn(microNetworkInfo);
        _dataModelProvider.createMicroNetwork(netID, containerID, path, state, rpcURL, p2pURL);
        EasyMock.expectLastCall().andThrow(new MicroNetworkAlreadyExistsException());

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        MicroNetworkInfo info;
        info = _dataModel.createMicroNetwork(netID, containerID, path, state, rpcURL, p2pURL);
        try {
            _dataModel.createMicroNetwork(netID, containerID, path, state, rpcURL, p2pURL);
            fail("Expected MicroNetworkAlreadyExistsException was not thrown");
        } catch (MicroNetworkAlreadyExistsException e) {
            // NOOP - Expected Exception
        }

        // Assert Results of the test
        // -- None --
        assertNotNull(info);
        assertEquals(netID, info.getId());
        assertEquals(containerID, info.getContainerID());
        assertEquals(path, info.getPath());
        assertEquals(state, info.getState());
        assertEquals(rpcURL, info.getRpcURL());
        assertEquals(p2pURL, info.getP2pURL());

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testUpdateMicroNetwork() throws Exception {

        // Create Test Data
        String netID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();
        String path = "/dev/null";
        MicroNetworkState state = MicroNetworkState.STARTING;
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";

        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(netID, containerID, path, state, rpcURL, p2pURL);

        // Configure Mock Object Expectations
        _dataModelProvider.updateMicroNetwork(microNetworkInfo);
        EasyMock.expectLastCall();
        _dataModelProvider.updateMicroNetwork(microNetworkInfo);
        EasyMock.expectLastCall().andThrow(new NoSuchMicroNetworkException());

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        _dataModel.updateMicroNetwork(microNetworkInfo);
        try {
            _dataModel.updateMicroNetwork(microNetworkInfo);
            fail("Expected MicroNetworkAlreadyExistsException was not thrown");
        } catch (NoSuchMicroNetworkException e) {
            // NOOP - Expected Exception
        }

        // Assert Results of the test
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testGetMicroNetwork() throws Exception {

        // Create Test Data
        String netID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();
        String path = "/dev/null";
        MicroNetworkState state = MicroNetworkState.STARTING;
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";

        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(netID, containerID, path, state, rpcURL, p2pURL);

        // Configure Mock Object Expectations
        EasyMock.expect(_dataModelProvider.getMicroNetwork(netID)).andReturn(microNetworkInfo);
        EasyMock.expect(_dataModelProvider.getMicroNetwork(netID)).andReturn(null);

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        MicroNetworkInfo info1 = _dataModel.getMicroNetwork(netID);
        MicroNetworkInfo info2 = _dataModel.getMicroNetwork(netID);

        // Assert Results of the test
        assertNotNull(info1);
        assertEquals(netID, info1.getId());
        assertEquals(containerID, info1.getContainerID());
        assertEquals(path, info1.getPath());
        assertEquals(state, info1.getState());
        assertEquals(rpcURL, info1.getRpcURL());
        assertEquals(p2pURL, info1.getP2pURL());

        assertNull(info2);

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }

    @Test
    public void testRemoveMicroNetwork() throws Exception {

        // Create Test Data
        String netID = UUID.randomUUID().toString();

        // Configure Mock Object Expectations
        EasyMock.expect(_dataModelProvider.removeMicroNetwork(netID)).andReturn(true);
        EasyMock.expect(_dataModelProvider.removeMicroNetwork(netID)).andReturn(false);

        // Replay the Mock Objects
        EasyMock.replay(_dataModelProvider);

        // Execute the method being tested
        boolean removed1 = _dataModel.removeMicroNetwork(netID);
        boolean removed2 = _dataModel.removeMicroNetwork(netID);

        // Assert Results of the test
        assertTrue(removed1);
        assertFalse(removed2);

        // Verify the Mock Objects
        EasyMock.verify(_dataModelProvider);
    }
}
