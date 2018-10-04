package io.topiacoin.node;

import io.topiacoin.node.micronetwork.MicroNetworkManager;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.MicroNetworkInfo;
import io.topiacoin.node.model.MicroNetworkState;
import io.topiacoin.node.proof.ProofSolver;
import io.topiacoin.node.smsc.SMSCManager;
import io.topiacoin.node.storage.DataStorageManager;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

    @Test
    public void testGetContainer() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_dataModel.getContainer(containerID)).andReturn(containerInfo);

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = new BusinessLogic();
        bl.setDataModel(_dataModel);
        bl.setDataStorageManager(_dataStorageMember);
        bl.setMicroNetworkManager(_microNetworkManager);
        bl.setProofSolver(_proofSolver);
        bl.setSmscManager(_smscManager);

        bl.initialize();

        // Execute the Test
        ContainerInfo fetchedContainer = bl.getContainer(containerID) ;

        // Verify the expected Results of the Test
        assertNotNull(fetchedContainer) ;
        assertEquals ( containerID, fetchedContainer.getId()) ;

        // Verify the Mock Objects have been called correctly.
        EasyMock.verify(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);
    }

    @Test
    public void testCreateContainerWhenContainerExistsInDataModel() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        MicroNetworkInfo microNetworkInfo= new MicroNetworkInfo("foo", containerID, "/dev/null", new MicroNetworkState("Sane"), "http://localhost:1234/", "http://localhost:8765/");

        // Configure the Mock Objects with Expected Behavior
        EasyMock.expect(_dataModel.getContainer(containerID)).andReturn(containerInfo);
//        EasyMock.expect(_smscManager.getContainerInfo(containerID)).andReturn(emptyFuture);
//        EasyMock.expect(_dataModel.createContainer(containerID, 0, null)).andReturn(containerInfo);
        EasyMock.expect(_microNetworkManager.getBlockchainInfo(containerID)).andReturn(null);
        _microNetworkManager.createBlockchain(containerID);
        EasyMock.expect(_microNetworkManager.getBlockchainInfo(containerID)).andReturn(microNetworkInfo);
        EasyMock.expectLastCall();

        // Switch the Mock Objects into Test Mode
        EasyMock.replay(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);

        // Create and Configure the Test Object
        BusinessLogic bl = new BusinessLogic();
        bl.setDataModel(_dataModel);
        bl.setDataStorageManager(_dataStorageMember);
        bl.setMicroNetworkManager(_microNetworkManager);
        bl.setProofSolver(_proofSolver);
        bl.setSmscManager(_smscManager);

        bl.initialize();

        // Execute the Test
        ContainerInfo fetchedContainer = bl.createContainer(containerID) ;

        // Verify the expected Results of the Test
        assertNotNull(fetchedContainer) ;
        assertEquals ( containerID, fetchedContainer.getId()) ;

        // Verify the Mock Objects have been called correctly.
        EasyMock.verify(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);
    }

    @Test
    public void testCreateContainerWhenContainerDoesNotExistInDataModel() throws Exception {

        // Test Data
        String containerID = UUID.randomUUID().toString();
        ContainerInfo containerInfo = new ContainerInfo(containerID, 0);
        MicroNetworkInfo microNetworkInfo= new MicroNetworkInfo("foo", containerID, "/dev/null", new MicroNetworkState("Sane"), "http://localhost:1234/", "http://localhost:8765/");

        Future<ContainerInfo> containerInfoFuture = new Future<ContainerInfo>() {
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
        BusinessLogic bl = new BusinessLogic();
        bl.setDataModel(_dataModel);
        bl.setDataStorageManager(_dataStorageMember);
        bl.setMicroNetworkManager(_microNetworkManager);
        bl.setProofSolver(_proofSolver);
        bl.setSmscManager(_smscManager);

        bl.initialize();

        // Execute the Test
        ContainerInfo fetchedContainer = bl.createContainer(containerID) ;

        // Verify the expected Results of the Test
        assertNotNull(fetchedContainer) ;
        assertEquals (containerID, fetchedContainer.getId()) ;

        // Verify the Mock Objects have been called correctly.
        EasyMock.verify(_dataModel, _dataStorageMember, _microNetworkManager, _proofSolver, _smscManager);
    }
}
