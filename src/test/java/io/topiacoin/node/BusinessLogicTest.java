package io.topiacoin.node;

import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.CorruptDataItemException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.micronetwork.MicroNetworkManager;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataItemInfo;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.MicroNetworkInfo;
import io.topiacoin.node.model.MicroNetworkState;
import io.topiacoin.node.proof.ProofSolver;
import io.topiacoin.node.smsc.SMSCManager;
import io.topiacoin.node.storage.DataStorageManager;
import io.topiacoin.node.utilities.HashUtilities;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.*;

public class BusinessLogicTest {

    private DataModel _dataModel;
    private DataStorageManager _dataStorageMember;
    private MicroNetworkManager _microNetworkManager;
    private ProofSolver _proofSolver;
    private SMSCManager _smscManager;

    @Before
    public void setUp() {
        // Create the Mock Objects that will be wired into the Test Object
        _dataModel = EasyMock.createMock(DataModel.class);
        _dataStorageMember = EasyMock.createMock(DataStorageManager.class);
        _microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);
        _proofSolver = EasyMock.createMock(ProofSolver.class);
        _smscManager = EasyMock.createMock(SMSCManager.class);
    }

    @After
    public void tearDown() {
        _dataModel = null;
        _dataStorageMember = null;
        _microNetworkManager = null;
        _proofSolver = null;
        _smscManager = null;
    }

    @Test
    public void testSanity() {
        fail("This test isn't sane!");
    }

    // -------- getContainer() --------

    @Test
    public void testGetContainerExistsInDataModel() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_dataModel.getContainer(containerID)).andReturn(containerInfo);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            ContainerInfo fetchedContainer = bl.getContainer(containerID);

            // Verify the expected Results of the Test
            assertNotNull(fetchedContainer);
            assertEquals(containerID, fetchedContainer.getId());

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testGetContainerDoesNotExistInDataModel() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testGetContainerDoesNotExistAtAll() throws Exception {
        fail("Test Not Yet Implemented");
    }

    // -------- createContainer() --------

    @Test
    public void testCreateContainerWhenContainerExistsInDataModel() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo("foo", containerID, "/dev/null", new MicroNetworkState("Sane"), "http://localhost:1234/", "http://localhost:8765/");

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_dataModel.getContainer(containerID)).andReturn(containerInfo);
        EasyMock.expect(_microNetworkManager.getBlockchainInfo(containerID)).andReturn(null);
        _microNetworkManager.createBlockchain(containerID);
        EasyMock.expect(_microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);
        EasyMock.expectLastCall();

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            ContainerInfo fetchedContainer = bl.createContainer(containerID);

            // Verify the expected Results of the Test
            assertNotNull(fetchedContainer);
            assertEquals(containerID, fetchedContainer.getId());

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testCreateContainerWhenContainerDoesNotExistInDataModel() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo("foo", containerID, "/dev/null", new MicroNetworkState("Sane"), "http://localhost:1234/", "http://localhost:8765/");

        Future<ContainerInfo> containerInfoFuture = getContainerInfoFuture(containerInfo);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_dataModel.getContainer(containerID)).andReturn(null);
        EasyMock.expect(_smscManager.getContainerInfo(containerID)).andReturn(containerInfoFuture);
        EasyMock.expect(_dataModel.createContainer(containerID, 0, null)).andReturn(containerInfo);
        EasyMock.expect(_microNetworkManager.getBlockchainInfo(containerID)).andReturn(null);
        _microNetworkManager.createBlockchain(containerID);
        EasyMock.expect(_microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);
        EasyMock.expectLastCall();

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            ContainerInfo fetchedContainer = bl.createContainer(containerID);

            // Verify the expected Results of the Test
            assertNotNull(fetchedContainer);
            assertEquals(containerID, fetchedContainer.getId());

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testCreateContainerWhenContainerDoesNotExistAtAll() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo("foo", containerID, "/dev/null", new MicroNetworkState("Sane"), "http://localhost:1234/", "http://localhost:8765/");

        Future<ContainerInfo> containerInfoFuture = getContainerInfoFuture(null);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_dataModel.getContainer(containerID)).andReturn(null);
        EasyMock.expect(_smscManager.getContainerInfo(containerID)).andReturn(containerInfoFuture);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.createContainer(containerID);
                fail("Expected NoSuchContainerException was not thrown");
            } catch (NoSuchContainerException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testCreateContainerWhenContainerIsAlreadyRunning() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo("foo", containerID, "/dev/null", new MicroNetworkState("Sane"), "http://localhost:1234/", "http://localhost:8765/");

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_dataModel.getContainer(containerID)).andReturn(containerInfo);
        EasyMock.expect(_microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                ContainerInfo fetchedContainer = bl.createContainer(containerID);
                fail("Expected ContainerAlreadyExistsException was not thrown");
            } catch (ContainerAlreadyExistsException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    // -------- replicateChunk() --------

    @Test
    public void testReplicateContainerWhenContainerExistsInDataModel() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testReplicateContainerWhenContainerDoesNotExistInDataModel() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testReplicateContainerWhenContainerDoesNotExistAtAll() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testReplicateContainerWhenContainerIsAlreadyRunning() throws Exception {
        fail("Test Not Yet Implemented");
    }

    // -------- addChunk() --------

    @Test
    public void testAddChunk() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testAddChunkToNonExistentContainer() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testAddChunkAlreadyExistsInContainer() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testAddChunkExistsInAnotherContainer() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testAddChunkBadHash() throws Exception {
        fail("Test Not Yet Implemented");
    }

    // -------- hasChunk() --------

    @Test
    public void testHasChunk() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testHasChunkInAnotherContainer() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testHasChunkNonExistentChunk() throws Exception {
        fail("Test Not Yet Implemented");
    }

    // -------- getChunk() --------

    @Test
    public void testGetChunk() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo("foo", containerID, "/dev/null", new MicroNetworkState("Sane"), "http://localhost:1234/", "http://localhost:8765/");
        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, containerID, data.length, dataHash);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_dataModel.getDataItem(chunkID)).andReturn(dataItemInfo);
        EasyMock.expect(_dataStorageMember.hasData(chunkID, containerID)).andReturn(true);
        _dataStorageMember.fetchData(chunkID, containerID, dataHash, outputStream);
        EasyMock.expectLastCall().andAnswer(() -> {
            outputStream.write(data);
            return null;
        });

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            bl.getChunk(containerID, chunkID, outputStream);

            // Verify the expected Results of the Test
            byte[] retrievedData = outputStream.toByteArray();
            assertTrue(Arrays.equals(data, retrievedData));

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testGetChunkInAnotherContainer() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String otherContainerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, otherContainerID, data.length, dataHash);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_dataModel.getDataItem(chunkID)).andReturn(dataItemInfo);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.getChunk(containerID, chunkID, outputStream);
                fail("Expected NoSuchDataItemException not thrown");
            } catch (NoSuchDataItemException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testGetChunkNonExistentChunk() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_dataModel.getDataItem(chunkID)).andReturn(null);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.getChunk(containerID, chunkID, outputStream);
                fail("Expected NoSuchDataItemException not thrown");
            } catch (NoSuchDataItemException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testGetChunkHashIsWrong() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        byte[] data = new byte[1024];
        String dataHash = HashUtilities.generateHash("SHA-256", data); // This hash is wrong
        new Random().nextBytes(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, containerID, data.length, dataHash);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_dataModel.getDataItem(chunkID)).andReturn(dataItemInfo);
        EasyMock.expect(_dataStorageMember.hasData(chunkID, containerID)).andReturn(true);
        _dataStorageMember.fetchData(chunkID, containerID, dataHash, outputStream);
        EasyMock.expectLastCall().andThrow(new CorruptDataItemException("Data Item Is Corrupt"));

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.getChunk(containerID, chunkID, outputStream);
                fail("Expected CorruptDataItemException not thrown");
            } catch (CorruptDataItemException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    // -------- submitChallenge() --------

    @Test
    public void testSubmitChallenge() throws Exception {
        fail("Test Not Yet Implemented");
    }

    @Test
    public void testSubmitChallengeWithInvalidParameters() throws Exception {
        fail("Test Not Yet Implemented");
    }

    // -------- Private Methods --------

    private BusinessLogic getConfiguredBusinessLogic() {
        BusinessLogic bl = new BusinessLogic();
        bl.setDataModel(_dataModel);
        bl.setDataStorageManager(_dataStorageMember);
        bl.setMicroNetworkManager(_microNetworkManager);
        bl.setProofSolver(_proofSolver);
        bl.setSmscManager(_smscManager);

        bl.initialize();

        return bl;
    }

    private Future<ContainerInfo> getContainerInfoFuture(final ContainerInfo containerInfo) {
        return new Future<ContainerInfo>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public ContainerInfo get() throws InterruptedException, ExecutionException {
                return containerInfo;
            }

            @Override
            public ContainerInfo get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return containerInfo;
            }
        };
    }


}
