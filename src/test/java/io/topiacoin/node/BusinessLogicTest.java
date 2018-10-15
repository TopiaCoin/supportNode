package io.topiacoin.node;

import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.CorruptDataItemException;
import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.FailedToCreateContainer;
import io.topiacoin.node.exceptions.InvalidChallengeException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.micronetwork.ContainerManager;
import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ChallengeChunkInfo;
import io.topiacoin.node.model.ChallengeSolution;
import io.topiacoin.node.model.ContainerConnectionInfo;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataItemInfo;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.*;

public class BusinessLogicTest {

    //    private DataModel _dataModel;
    private DataStorageManager _dataStorageManager;
    private ContainerManager _containerManager;
    private ProofSolver _proofSolver;
    private SMSCManager _smscManager;

    @Before
    public void setUp() {
        // Create the Mock Objects that will be wired into the Test Object
//        _dataModel = EasyMock.createMock(DataModel.class);
        _dataStorageManager = EasyMock.createMock(DataStorageManager.class);
        _containerManager = EasyMock.createMock(ContainerManager.class);
        _proofSolver = EasyMock.createMock(ProofSolver.class);
        _smscManager = EasyMock.createMock(SMSCManager.class);
    }

    @After
    public void tearDown() {
//        _dataModel = null;
        _dataStorageManager = null;
        _containerManager = null;
        _proofSolver = null;
        _smscManager = null;
    }


    // -------- getContainer() --------

    @Test
    public void testGetContainerExistsInDataModel() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        String rpcURL = "http://localhost:1234";
        String p2pURL = "http:localhost:5678/";
        ContainerConnectionInfo containerConnectionInfo = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.getContainerConnectionInfo(containerID)).andReturn(containerConnectionInfo);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            ContainerConnectionInfo fetchedContainerConnectionInfo = bl.getContainer(containerID);

            // Verify the expected Results of the Test
            assertNotNull(fetchedContainerConnectionInfo);
            assertEquals(containerID, fetchedContainerConnectionInfo.getContainerID());
            assertEquals(rpcURL, fetchedContainerConnectionInfo.getConnectionURL());
            assertEquals(p2pURL, fetchedContainerConnectionInfo.getP2PURL());

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testGetContainerDoesNotExistInDataModel() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();

        // Configure the Mock Objects with Expected Behavior
        // -- None --
        EasyMock.expect(_containerManager.getContainerConnectionInfo(containerID)).andReturn(null);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                ContainerConnectionInfo fetchedContainerConnectionInfo = bl.getContainer(containerID);
                fail("Expected NoSuchContainerException was not thrown");
            } catch (NoSuchContainerException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testGetContainerIsNotHostedOnNode() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.getContainerConnectionInfo(containerID)).andReturn(null);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                ContainerConnectionInfo fetchedContainerConnectionInfo = bl.getContainer(containerID);
                fail("Expected NoSuchContainerException was not thrown");
            } catch (NoSuchContainerException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    // -------- createContainer() --------

    @Test
    public void testCreateContainerWhenContainerExistsInDataModel() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String containerState = "STOPPED";

        String rpcURL = "http://localhost:1234";
        String p2pURL = "http:localhost:5678/";
        ContainerConnectionInfo containerConnectionInfo = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        Future<ContainerConnectionInfo> createFuture = new CompletedFuture<>(containerConnectionInfo);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_containerManager.createContainer(containerID)).andReturn(createFuture);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            ContainerConnectionInfo fetchedContainer = bl.createContainer(containerID);

            // Verify the expected Results of the Test
            assertNotNull(fetchedContainer);
            assertEquals(containerID, fetchedContainer.getContainerID());

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testCreateContainerWhenContainerDoesNotExistInDataModel() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();

        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        Future<ContainerInfo> containerInfoFuture = (Future<ContainerInfo>) new CompletedFuture<>(containerInfo);

        String containerState = "STOPPED";

        String rpcURL = "http://localhost:1234";
        String p2pURL = "http:localhost:5678/";
        ContainerConnectionInfo containerConnectionInfo = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        Future<ContainerConnectionInfo> createFuture = new CompletedFuture<>(containerConnectionInfo);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(false);
        EasyMock.expect(_smscManager.getContainerInfo(containerID)).andReturn(containerInfoFuture);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_containerManager.createContainer(containerID)).andReturn(createFuture);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            ContainerConnectionInfo fetchedContainer = bl.createContainer(containerID);

            // Verify the expected Results of the Test
            assertNotNull(fetchedContainer);
            assertEquals(containerID, fetchedContainer.getContainerID());

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testCreateContainerWhenContainerDoesNotExistAtAll() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();

        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        Future<ContainerInfo> containerInfoFuture = new CompletedFuture<>(null);

        String containerState = "STOPPED";

//        String rpcURL = "http://localhost:1234";
//        String p2pURL = "http:localhost:5678/";
//        ContainerConnectionInfo containerConnectionInfo = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        Future<ContainerConnectionInfo> createFuture = new CompletedFuture<>(null);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(false);
        EasyMock.expect(_smscManager.getContainerInfo(containerID)).andReturn(containerInfoFuture);
//        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
//        EasyMock.expect(_containerManager.createContainer(containerID)).andReturn(createFuture);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

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
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testCreateContainerWhenContainerIsAlreadyRunning() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();

        String containerState = "RUNNING";

        Future<ContainerConnectionInfo> createFuture = new CompletedFuture<>(null);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.createContainer(containerID);
                fail("Expected ContainerAlreadyExistsException was not thrown");
            } catch (ContainerAlreadyExistsException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testCreateContainerFailsToCreateContainer() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();

        String containerState = "STOPPED";

//        String rpcURL = "http://localhost:1234";
//        String p2pURL = "http:localhost:5678/";
//        ContainerConnectionInfo containerConnectionInfo = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        CompletedFuture<ContainerConnectionInfo> createFuture = new CompletedFuture<>(null);
        createFuture.setException(new Exception("Creation failed", null));

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_containerManager.createContainer(containerID)).andReturn(createFuture);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                ContainerConnectionInfo fetchedContainer = bl.createContainer(containerID);
                fail("Expected ContainerAlreadyExistsException was not thrown");
            } catch (FailedToCreateContainer e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    // -------- replicateContainer() --------

    @Test
    public void testReplicateContainerWhenContainerExistsInDataModel() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String peerNodeID = UUID.randomUUID().toString();
        String containerState = "STOPPED";

        String rpcURL = "http://localhost:1234";
        String p2pURL = "http:localhost:5678/";
        ContainerConnectionInfo containerConnectionInfo = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        Future<ContainerConnectionInfo> createFuture = new CompletedFuture<>(containerConnectionInfo);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_containerManager.replicateContainer(containerID, peerNodeID)).andReturn(createFuture);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            ContainerConnectionInfo fetchedContainer = bl.replicateContainer(containerID, peerNodeID);

            // Verify the expected Results of the Test
            assertNotNull(fetchedContainer);
            assertEquals(containerID, fetchedContainer.getContainerID());

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testReplicateContainerWhenContainerDoesNotExistInDataModel() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String peerNodeID = UUID.randomUUID().toString();
        String containerState = "STOPPED";

        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        Future<ContainerInfo> containerInfoFuture = (Future<ContainerInfo>) new CompletedFuture<>(containerInfo);

        String rpcURL = "http://localhost:1234";
        String p2pURL = "http:localhost:5678/";
        ContainerConnectionInfo containerConnectionInfo = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        Future<ContainerConnectionInfo> createFuture = new CompletedFuture<>(containerConnectionInfo);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(false);
        EasyMock.expect(_smscManager.getContainerInfo(containerID)).andReturn(containerInfoFuture);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_containerManager.replicateContainer(containerID, peerNodeID)).andReturn(createFuture);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            ContainerConnectionInfo fetchedContainer = bl.replicateContainer(containerID, peerNodeID);

            // Verify the expected Results of the Test
            assertNotNull(fetchedContainer);
            assertEquals(containerID, fetchedContainer.getContainerID());

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testReplicateContainerWhenContainerDoesNotExistAtAll() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String peerNodeID = UUID.randomUUID().toString();
        String containerState = "STOPPED";

        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        Future<ContainerInfo> containerInfoFuture =  new CompletedFuture<>(null);

        String rpcURL = "http://localhost:1234";
        String p2pURL = "http:localhost:5678/";
        ContainerConnectionInfo containerConnectionInfo = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        Future<ContainerConnectionInfo> createFuture = new CompletedFuture<>(null);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(false);
        EasyMock.expect(_smscManager.getContainerInfo(containerID)).andReturn(containerInfoFuture);
//        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
//        EasyMock.expect(_containerManager.replicateContainer(containerID, peerNodeID)).andReturn(createFuture);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.replicateContainer(containerID, peerNodeID);
                fail("Expected NoSuchContainerException was not thrown");
            } catch (NoSuchContainerException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testReplicateContainerWhenContainerIsAlreadyRunning() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String peerNodeID = UUID.randomUUID().toString();
        String containerState = "RUNNING";

        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        Future<ContainerInfo> containerInfoFuture = (Future<ContainerInfo>) new CompletedFuture<>(containerInfo);

        String rpcURL = "http://localhost:1234";
        String p2pURL = "http:localhost:5678/";
        ContainerConnectionInfo containerConnectionInfo = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        Future<ContainerConnectionInfo> createFuture = new CompletedFuture<>(containerConnectionInfo);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
//        EasyMock.expect(_smscManager.getContainerInfo(containerID)).andReturn(containerInfoFuture);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
//        EasyMock.expect(_containerManager.replicateContainer(containerID, peerNodeID)).andReturn(createFuture);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.replicateContainer(containerID, peerNodeID);
                fail("Expeted MicroNetworkAlreadyExistsException was not thrown");
            } catch (ContainerAlreadyExistsException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    // -------- removeContainer() --------

    @Test
    public void testRemoveContainer() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();

        Future<Void> removeFuture = new CompletedFuture<>(null);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.removeContainer(containerID)).andReturn(removeFuture);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            bl.removeContainer(containerID);

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testRemoveNonExistentContainer() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();

        Future<Void> removeFuture = new CompletedFuture<>(null);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(false);
//        EasyMock.expect(_containerManager.removeContainer(containerID)).andReturn(removeFuture);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.removeContainer(containerID);
                fail("Expected NoSuchContainerException was not thrown");
            } catch (NoSuchContainerException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    // -------- storeChunk() --------

    @Test
    public void testStoreChunk() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();

        String containerState = "RUNNING";

        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        ByteArrayInputStream dataStream = new ByteArrayInputStream(data);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_dataStorageManager.hasData(containerID, chunkID)).andReturn(false);
        EasyMock.expect(_dataStorageManager.saveData(containerID, chunkID, dataHash, dataStream)).andReturn((long) data.length);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            bl.storeChunk(containerID, chunkID, dataHash, dataStream);

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testStoreChunkToNonExistentContainer() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();

        String containerState = "RUNNING";

        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        ByteArrayInputStream dataStream = new ByteArrayInputStream(data);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(false);
//        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
//        EasyMock.expect(_dataStorageManager.hasData(containerID, chunkID)).andReturn(false);
//        EasyMock.expect(_dataStorageManager.saveData(containerID, chunkID, dataHash, dataStream)).andReturn((long) data.length);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.storeChunk(containerID, chunkID, dataHash, dataStream);
                fail("Expected NoSuchContainerException was not thrown");
            } catch (NoSuchContainerException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testStoreChunkAlreadyExistsInContainer() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();

        String containerState = "RUNNING";

        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        ByteArrayInputStream dataStream = new ByteArrayInputStream(data);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_dataStorageManager.hasData(containerID, chunkID)).andReturn(true);
//        EasyMock.expect(_dataStorageManager.saveData(containerID, chunkID, dataHash, dataStream)).andReturn((long) data.length);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.storeChunk(containerID, chunkID, dataHash, dataStream);
                fail("Expected DataItemAlreadyExistsException was not thrown");
            } catch (DataItemAlreadyExistsException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testStoreChunkBadHash() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();

        String containerState = "RUNNING";

        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        ByteArrayInputStream dataStream = new ByteArrayInputStream(data);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_dataStorageManager.hasData(containerID, chunkID)).andReturn(false);
        EasyMock.expect(_dataStorageManager.saveData(containerID, chunkID, dataHash, dataStream)).andThrow(new CorruptDataItemException("Data Item is Corrupt"));

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.storeChunk(containerID, chunkID, dataHash, dataStream);
                fail("Expected CorruptDataItemException was not thrown");
            } catch (CorruptDataItemException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    // -------- hasChunk() --------

    @Test
    public void testHasChunk() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, data.length, dataHash);

        String containerState = "RUNNING";

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_dataStorageManager.hasData(containerID, chunkID)).andReturn(true);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            boolean hasChunk = bl.hasChunk(containerID, chunkID);

            // Verify the expected Results of the Test
            assertTrue(hasChunk);

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testHasChunkInModelButNotOnDisk() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, data.length, dataHash);

        String containerState = "RUNNING";

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_dataStorageManager.hasData(containerID, chunkID)).andReturn(false);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            boolean hasChunk = bl.hasChunk(containerID, chunkID);

            // Verify the expected Results of the Test
            assertFalse(hasChunk);

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testHasChunkNonExistentChunk() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();

        String containerState = "RUNNING";

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_dataStorageManager.hasData(containerID, chunkID)).andReturn(false);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            boolean hasChunk = bl.hasChunk(containerID, chunkID);

            // Verify the expected Results of the Test
            assertFalse(hasChunk);

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }


    @Test
    public void testHasChunkNonExistentContainer() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, data.length, dataHash);

        String containerState = "RUNNING";

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(false);
//        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
//        EasyMock.expect(_dataStorageManager.hasData(containerID, chunkID)).andReturn(false);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                boolean hasChunk = bl.hasChunk(containerID, chunkID);
                fail ( "Expected NoSuchContainerException was not thrown");
            } catch ( NoSuchContainerException e ){
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
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

        String containerState = "RUNNING";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo("foo", containerID, "/dev/null", MicroNetworkState.STARTING, "http://localhost:1234/", "http://localhost:8765/");
        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, data.length, dataHash);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_dataStorageManager.hasData(containerID, chunkID)).andReturn(true);
        _dataStorageManager.fetchData(containerID, chunkID, outputStream);
        EasyMock.expectLastCall().andAnswer(() -> {
            outputStream.write(data);
            return null;
        });

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            bl.getChunk(containerID, chunkID, outputStream);

            // Verify the expected Results of the Test
            byte[] retrievedData = outputStream.toByteArray();
            assertTrue(Arrays.equals(data, retrievedData));

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
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

        String containerState = "RUNNING";

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_dataStorageManager.hasData(containerID, chunkID)).andReturn(false);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

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
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testGetChunkNonExistentContainer() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String otherContainerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        String containerState = "RUNNING";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, data.length, dataHash);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(false);
//        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
//        EasyMock.expect(_dataStorageManager.hasData(containerID, chunkID)).andThrow(new NoSuchContainerException());
//        EasyMock.expect(_dataModel.getDataItem(chunkID)).andReturn(dataItemInfo);
//        EasyMock.expect(_dataModel.isDataItemInContainer(chunkID, containerID)).andThrow(new NoSuchContainerException(""));

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.getChunk(containerID, chunkID, outputStream);
                fail("Expected NoSuchContainerException not thrown");
            } catch (NoSuchContainerException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
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

        String containerState="RUNNING";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, data.length, dataHash);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_dataStorageManager.hasData(containerID, chunkID)).andReturn(true);
        _dataStorageManager.fetchData(containerID, chunkID, outputStream);
        EasyMock.expectLastCall().andThrow(new CorruptDataItemException("Data Item Is Corrupt"));

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

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
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    // -------- removeChunk() --------

    @Test
    public void testRemoveChunk() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo("foo", containerID, "/dev/null", MicroNetworkState.STARTING, "http://localhost:1234/", "http://localhost:8765/");
        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, data.length, dataHash);

        String containerState = "RUNNING";

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_dataStorageManager.removeData(containerID, chunkID)).andReturn(true);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            bl.removeChunk(containerID, chunkID);

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testRemoveNonExistentChunk() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo("foo", containerID, "/dev/null", MicroNetworkState.STARTING, "http://localhost:1234/", "http://localhost:8765/");
        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, data.length, dataHash);

        String containerState = "RUNNING";

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_dataStorageManager.removeData(containerID, chunkID)).andReturn(false);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.removeChunk(containerID, chunkID);
                fail("Expected NoSuchDataItemException was not thrown");
            } catch (NoSuchDataItemException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testRemoveChunkFromNonExistentContainer() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        String dataHash = HashUtilities.generateHash("SHA-256", data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo("foo", containerID, "/dev/null", MicroNetworkState.STARTING, "http://localhost:1234/", "http://localhost:8765/");
        DataItemInfo dataItemInfo = new DataItemInfo(chunkID, data.length, dataHash);

        String containerState = "RUNNING";

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(false);
//        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
//        EasyMock.expect(_dataStorageManager.removeData(containerID, chunkID)).andReturn(false);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.removeChunk(containerID, chunkID);
                fail("Expected NoSuchContainerException was not thrown");
            } catch (NoSuchContainerException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }


    // -------- submitChallenge() --------

    @Test
    public void testSubmitChallenge() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        ContainerInfo containerInfo = new ContainerInfo(containerID, 0, null);

        String chunkID = UUID.randomUUID().toString();
        int offset = 1234;
        int length = 5678;
        List<ChallengeChunkInfo> chunks = new ArrayList<>();
        chunks.add(new ChallengeChunkInfo(chunkID, offset, length));

        Challenge challenege = new Challenge(containerID, chunks);

        ContainerInfo updatedContainerInfo = new ContainerInfo(containerID, 0, challenege);

        String verificationValue = "foo";
        String transactionID = "bar";
        long blockNumber = 1234567;
        String chunkHash = "SHA-256:deadbeef";
        ChallengeSolution solution = new ChallengeSolution(verificationValue, transactionID, blockNumber, chunkHash);

        Future<Void> submitFuture = new CompletedFuture<>(null);

        String containerState = "RUNNING";

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_proofSolver.generateSolution(challenege)).andReturn(solution);
        EasyMock.expect(_smscManager.submitProofSolution(containerID, solution)).andReturn(submitFuture);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            bl.submitChallenge(challenege);

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    @Test
    public void testSubmitInvalidChallenge() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        ContainerInfo containerInfo = new ContainerInfo(containerID, 0, null);

        String chunkID = UUID.randomUUID().toString();
        int offset = 1234;
        int length = 5678;
        List<ChallengeChunkInfo> chunks = new ArrayList<>();
        chunks.add(new ChallengeChunkInfo(chunkID, offset, length));

        Challenge challenege = new Challenge(containerID, chunks);

        ContainerInfo updatedContainerInfo = new ContainerInfo(containerID, 0, challenege);

        String verificationValue = "foo";
        String transactionID = "bar";
        long blockNumber = 1234567;
        String chunkHash = "SHA-256:deadbeef";
        ChallengeSolution solution = new ChallengeSolution(verificationValue, transactionID, blockNumber, chunkHash);

        Future<Void> submitFuture = new CompletedFuture<>(null);

        String containerState = "RUNNING";

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_containerManager.hasContainer(containerID)).andReturn(true);
        EasyMock.expect(_containerManager.getContainerState(containerID)).andReturn(containerState);
        EasyMock.expect(_proofSolver.generateSolution(challenege)).andReturn(null);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataStorageManager, _containerManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = getConfiguredBusinessLogic();

        try {
            // Execute the Test
            try {
                bl.submitChallenge(challenege);
                fail("Expected InvalidChallengeException not thrown");
            } catch (InvalidChallengeException e) {
                // NOOP - Expected Exception
            }

            // Verify the expected Results of the Test
            // -- None --

            // Verify the Mock Objects have been called correctly.
            EasyMock.verify(_dataStorageManager, _containerManager, _proofSolver, _smscManager);
        } finally {
            bl.shutdown();
        }
    }

    // -------- Private Methods --------

    private BusinessLogic getConfiguredBusinessLogic() {
        BusinessLogic bl = new BusinessLogic();
//        bl.setDataModel(_dataModel);
        bl.setDataStorageManager(_dataStorageManager);
        bl.setContainerManager(_containerManager);
        bl.setProofSolver(_proofSolver);
        bl.setSmscManager(_smscManager);

        bl.initialize();

        return bl;
    }


    private static class CompletedFuture<T> implements Future<T> {
        private T result;
        private Throwable throwable;
        private boolean interrupted;

        public CompletedFuture(T result) {
            this.result = result;
        }

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
        public T get() throws InterruptedException, ExecutionException {
            if ( this.throwable != null) {
                throw new ExecutionException(this.throwable);
            }
            if ( this.interrupted ) {
                throw new InterruptedException();
            }
            return result;
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return result;
        }

        public void setException ( Throwable t ) {
            this.throwable = t;
        }

        public void setInterrupted(boolean interrupted) {
            this.interrupted = interrupted;
        }
    }
}
