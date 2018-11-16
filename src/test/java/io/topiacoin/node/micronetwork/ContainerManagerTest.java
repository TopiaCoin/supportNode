package io.topiacoin.node.micronetwork;

import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.FailedToCreateContainer;
import io.topiacoin.node.exceptions.FailedToReplicateContainer;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ChallengeChunkInfo;
import io.topiacoin.node.model.ContainerConnectionInfo;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.MicroNetworkInfo;
import io.topiacoin.node.model.MicroNetworkState;
import io.topiacoin.node.model.provider.DataModelProvider;
import io.topiacoin.node.model.provider.MemoryDataModelProvider;
import io.topiacoin.node.utility.CompletedFuture;
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static io.topiacoin.node.micronetwork.ContainerManager.ContainerState.*;
import static org.junit.Assert.*;

public class ContainerManagerTest {

    // -------- getContainer() --------

    @Test
    public void testGetContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        // -- None --

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        ContainerInfo containerInfo = containerManager.getContainer(containerID);

        // Verify the Test Results
        assertNotNull(containerInfo);
        assertEquals(containerID, containerInfo.getId());

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);

    }

    @Test
    public void testGetContainerForNonExistentContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        // -- None --

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        ContainerInfo containerInfo = containerManager.getContainer(containerID);

        // Verify the Test Results
        assertNull(containerInfo);

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);

    }

    // -------- hasContainer() --------

    @Test
    public void tesHasContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        // -- None --

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        boolean hasContainer = containerManager.hasContainer(containerID);

        // Verify the Test Results
        assertTrue(hasContainer);

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void tesHasContainerForNonExistentContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        // -- None --

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        boolean hasContainer = containerManager.hasContainer(containerID);

        // Verify the Test Results
        assertFalse(hasContainer);

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }


    // -------- getContainerState() --------

    @Test
    public void testGetContainerState() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.RUNNING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);
        // -- None --

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        ContainerManager.ContainerState containerState = containerManager.getContainerState(containerID);

        // Verify the Test Results
        assertEquals(RUNNING, containerState);

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testGetContainerStateForNonExistentContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(null);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        ContainerManager.ContainerState containerState = containerManager.getContainerState(containerID);

        // Verify the Test Results
        assertEquals(UNKNOWN, containerState);

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testGetContainerStateForStoppedContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.STOPPED,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        ContainerManager.ContainerState containerState = containerManager.getContainerState(containerID);

        // Verify the Test Results
        assertEquals(STOPPED, containerState);

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testGetContainerStateForStartingContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.STARTING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        ContainerManager.ContainerState containerState = containerManager.getContainerState(containerID);

        // Verify the Test Results
        assertEquals(STARTING, containerState);

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testGetContainerStateForStoppingContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.STOPPING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        ContainerManager.ContainerState containerState = containerManager.getContainerState(containerID);

        // Verify the Test Results
        assertEquals(STOPPING, containerState);

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }


    // -------- getContainerConnectionInfo() --------

    @Test
    public void testGetContainerConnectionInfo() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.RUNNING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        ContainerConnectionInfo containerConnectionInfo = containerManager.getContainerConnectionInfo(containerID);

        // Verify the Test Results
        assertNotNull(containerConnectionInfo);
        assertEquals(containerID, containerConnectionInfo.getContainerID());
        assertEquals(rpcURL, containerConnectionInfo.getConnectionURL());
        assertEquals(p2pURL, containerConnectionInfo.getP2PURL());

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testGetContainerConnectionInfoForNonExistentContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(null);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        ContainerConnectionInfo containerConnectionInfo = containerManager.getContainerConnectionInfo(containerID);

        // Verify the Test Results
        assertNull(containerConnectionInfo);

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testGetContainerConnectionInfoForNonRunningContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.STOPPED,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        ContainerConnectionInfo containerConnectionInfo = containerManager.getContainerConnectionInfo(containerID);

        // Verify the Test Results
        assertNull(containerConnectionInfo);

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testGetContainerConnectionInfoForStartingContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.STARTING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        ContainerConnectionInfo containerConnectionInfo = containerManager.getContainerConnectionInfo(containerID);

        // Verify the Test Results
        assertNull(containerConnectionInfo);

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testGetContainerConnectionInfoForStoppingContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.STOPPING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        ContainerConnectionInfo containerConnectionInfo = containerManager.getContainerConnectionInfo(containerID);

        // Verify the Test Results
        assertNull(containerConnectionInfo);

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }


    // -------- createContainer() --------

    @Test
    public void testCreateContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.RUNNING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(null);
        microNetworkManager.createBlockchain(containerID);
        EasyMock.expectLastCall();
        microNetworkManager.startBlockchain(containerID);
        EasyMock.expectLastCall();
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        Future<ContainerConnectionInfo> createFuture = containerManager.createContainer(containerID);

        // Verify the Test Results
        assertNotNull(createFuture);
        ContainerConnectionInfo containerConnectionInfo = createFuture.get();
        assertNotNull(containerConnectionInfo);
        assertEquals(containerID, containerConnectionInfo.getContainerID());
        assertEquals(rpcURL, containerConnectionInfo.getConnectionURL());
        assertEquals(p2pURL, containerConnectionInfo.getP2PURL());

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testCreateContainerForNonExistentContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        // -- None --

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        try {
            containerManager.createContainer(containerID);
            fail ( "Expected NoSuchContainerException was not thrown");
        } catch ( NoSuchContainerException e ){
            // NOOP - Expected Exception
        }

        // Verify the Test Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testCreateContainerForAlreadyRunningContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.RUNNING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        try {
            containerManager.createContainer(containerID);
            fail ("Expected ContainerAlreadyExistsException was not thrown");
        } catch ( ContainerAlreadyExistsException e ) {
            // NOOP - Expected Exception
        }

        // Verify the Test Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testCreateContainerWhenContainerDoesNotStart() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.STOPPED,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(null);
        microNetworkManager.createBlockchain(containerID);
        EasyMock.expectLastCall();
        microNetworkManager.startBlockchain(containerID);
        EasyMock.expectLastCall();
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        Future<ContainerConnectionInfo> createFuture = containerManager.createContainer(containerID);

        // Verify the Test Results
        assertNotNull(createFuture);
        try {
            createFuture.get();
            fail ("Expected ExecutionException was not thrown" ) ;
        } catch ( ExecutionException e ) {
            // Expected Exception
            assertEquals(FailedToCreateContainer.class, e.getCause().getClass());
        }

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }


    // -------- replicateContainer() --------

    @Test
    public void testReplicateContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String peerNodeID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        String peerRPCURL = "http://remotehost:8765/";
        String peerP2PURL = "http://remotehost:9876/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.RUNNING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);
        dataModel.createNodeConnectionInfo(peerNodeID, peerRPCURL);

        Future<MicroNetworkInfo> syncFuture = new CompletedFuture<>(microNetworkInfo);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(null);
        EasyMock.expect(microNetworkManager.syncBlockchain(peerP2PURL, containerID)).andReturn(syncFuture);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        Future<ContainerConnectionInfo> createFuture = containerManager.replicateContainer(containerID, peerNodeID);

        // Verify the Test Results
        assertNotNull(createFuture);
        ContainerConnectionInfo containerConnectionInfo = createFuture.get();
        assertNotNull(containerConnectionInfo);
        assertEquals(containerID, containerConnectionInfo.getContainerID());
        assertEquals(rpcURL, containerConnectionInfo.getConnectionURL());
        assertEquals(p2pURL, containerConnectionInfo.getP2PURL());

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testReplicateContainerForNonExistentContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String peerNodeID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        // -- None --

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        try {
            containerManager.replicateContainer(containerID, peerNodeID);
            fail ("Expected NoSuchContainerException was not thrown") ;
        } catch ( NoSuchContainerException e ) {
            // NOOP - Expected Exception
        }

        // Verify the Test Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testReplicateContainerForAlreadyRunningContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String peerNodeID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.RUNNING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        try {
            containerManager.replicateContainer(containerID, peerNodeID);
            fail ( "Expected ContainerAlreadyExistsException was not thrown");
        } catch ( ContainerAlreadyExistsException e ) {
            // NOOP - Expected Exception
        }

        // Verify the Test Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testReplicateContainerWhenReplicationDoesNotStart() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String peerNodeID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        String peerRPCURL = "http://remotehost:8765/";
        String peerP2PURL = "http://remotehost:9876/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.STOPPED,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);
        dataModel.createNodeConnectionInfo(peerNodeID, peerRPCURL);

        CompletedFuture<MicroNetworkInfo> syncFuture = new CompletedFuture<>(microNetworkInfo);
        syncFuture.setException(new FailedToReplicateContainer());

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(null);
        EasyMock.expect(microNetworkManager.syncBlockchain(peerP2PURL, containerID)).andReturn(syncFuture);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        Future<ContainerConnectionInfo> createFuture = containerManager.replicateContainer(containerID, peerNodeID);

        // Verify the Test Results
        assertNotNull(createFuture);
        try {
            createFuture.get();
            fail ( "Expected ExecutionException was not thrown");
        } catch ( ExecutionException e ) {
            // Expected Exception
            assertEquals(FailedToReplicateContainer.class, e.getCause().getClass());
        }

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }


    // -------- removeContainer() --------

    @Test
    public void testRemoveContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.RUNNING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        // -- None --
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);
        microNetworkManager.destroyBlockchain(containerID);
        EasyMock.expectLastCall();

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        Future<Void> removeFuture = containerManager.removeContainer(containerID);

        // Verify the Test Results
        assertNotNull(removeFuture);
        removeFuture.get() ;

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);

    }

    @Test
    public void testRemoveContainerForNonExistentContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        // -- None --

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        try {
            containerManager.removeContainer(containerID);
            fail ( "Expected NoSuchContainerException was not thrown");
        } catch ( NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Verify the Test Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }

    @Test
    public void testRemoveContainerForNonRunningContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        // -- None --
        EasyMock.expect(microNetworkManager.getBlockchainInfo(containerID)).andReturn(null);

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        try {
            containerManager.removeContainer(containerID);
            fail ( "Expected NoSuchContainerException was not thrown");
        } catch ( NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Verify the Test Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);
    }


    // -------- saveChallenge() --------

    @Test
    public void testSaveChallenge() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.RUNNING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        List<ChallengeChunkInfo> chunkList = new ArrayList<>();
        Challenge challenge = new Challenge(containerID, chunkList);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        // -- None --

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        containerManager.saveChallenge(challenge);

        // Verify the Test Results
        ContainerInfo fetchedInfo = dataModel.getContainer(containerID);
        assertEquals(challenge, fetchedInfo.getChallenge());

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);

    }

    @Test
    public void testSaveChallengeForNonExistentContainer() throws Exception {

        // Create Test Data
        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        MicroNetworkInfo microNetworkInfo = new MicroNetworkInfo(
                containerID,
                containerID,
                "/dev/null",
                MicroNetworkState.RUNNING,
                rpcURL,
                p2pURL);

        DataModel dataModel = getDataModel();
//        dataModel.createContainer(containerID, 0, null);

        List<ChallengeChunkInfo> chunkList = new ArrayList<>();
        Challenge challenge = new Challenge(containerID, chunkList);

        // Create Mock Objects
        MicroNetworkManager microNetworkManager = EasyMock.createMock(MicroNetworkManager.class);

        // Set Mock Expectations
        // -- None --

        // Prepare Mock Objects for Replay
        EasyMock.replay(microNetworkManager);

        // Setup the Test Object
        ContainerManager containerManager = new ContainerManager();
        containerManager.setDataModel(dataModel);
        containerManager.setMicroNetworkManager(microNetworkManager);
        containerManager.initialize();

        // Execute the Test
        try {
            containerManager.saveChallenge(challenge);
            fail("Expected NoSuchContainerException was not thrown");
        } catch ( NoSuchContainerException e ) {
            // NOOP - Expected Exception
        }

        // Verify the Test Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(microNetworkManager);

    }

    // ======== Utility Methods ========

    private DataModel getDataModel() {
        DataModelProvider dataModelProvider = new MemoryDataModelProvider();
        dataModelProvider.initialize();

        DataModel dataModel = new DataModel();
        dataModel.setProvider(dataModelProvider);
        dataModel.initialize();
        return dataModel;
    }

}
