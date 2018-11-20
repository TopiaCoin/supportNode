package io.topiacoin.node.smsc;

import io.topiacoin.eosrpcadapter.EOSRPCAdapter;
import io.topiacoin.eosrpcadapter.exceptions.ChainException;
import io.topiacoin.eosrpcadapter.exceptions.WalletException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.NodeConnectionInfo;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public abstract class AbstractSMSCManagerTest {

    protected abstract SMSCManager getSMSCManager() ;
    protected abstract String createContainer() throws WalletException, ChainException;
    protected abstract void terminateContainer(String containerID) throws WalletException, ChainException;

    @Test
    public void testSanity() {
        fail ( "Test Cases Not yet Implemented" ) ;
    }

    @Test
    public void testRegisterUnregisterNode() throws Exception {
        SMSCManager smscManager = getSMSCManager();

        String nodeID = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());

        Future<Void> future = smscManager.registerNode(nodeID);

        future.get();

        future = smscManager.unregisterNode(nodeID);

        future.get();
    }

    @Test
    public void testGetNodeInfo() throws Exception {
        SMSCManager smscManager = getSMSCManager();

        String nodeID = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());

        try {
            // We must register the Node in order to fetch assigned containers
            Future<Void> registerFuture = smscManager.registerNode(nodeID);
            registerFuture.get();

            // Fetch the list of containers assigned to this node.
            Future<NodeConnectionInfo> containersFuture = smscManager.getNodeInfo(nodeID);
            NodeConnectionInfo nodeConnectionInfo = containersFuture.get();

            assertNotNull(nodeConnectionInfo);
            assertEquals(nodeID, nodeConnectionInfo.getNodeID()) ;
            assertNotNull(nodeConnectionInfo.getNodeURL());
        } finally {
            Future<Void> unregisterFuture = smscManager.unregisterNode(nodeID);
            unregisterFuture.get();
        }
    }

    @Test
    public void testGetContainers() throws Exception {
        SMSCManager smscManager = getSMSCManager();

        String nodeID = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String containerID = null;

        try {
            // We must register the Node in order to fetch assigned containers
            Future<Void> registerFuture = smscManager.registerNode(nodeID);
            registerFuture.get();

            // Fetch the list of containers assigned to this node.
            Future<List<String>> containersFuture = smscManager.getContainers();
            List<String> assignedContainersList = containersFuture.get();

            assertNotNull(assignedContainersList);
            assertEquals(0, assignedContainersList.size());

            // Create a Container
            containerID = createContainer();

            // Fetch the list of containers and make sure there is one assigned to this node.
            containersFuture = smscManager.getContainers();
            assignedContainersList = containersFuture.get();

            assertNotNull(assignedContainersList);
            assertEquals(1, assignedContainersList.size());
            assertTrue(assignedContainersList.contains(containerID));
        } finally {
            if ( containerID != null ) {
                terminateContainer(containerID);
            }
            Future<Void> unregisterFuture = smscManager.unregisterNode(nodeID);
            unregisterFuture.get();
        }
    }

    @Test
    public void testGetNodesForContainer() throws Exception {
        SMSCManager smscManager = getSMSCManager();

        String nodeID = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String containerID = null;

        try {
            // We must register the Node in order to fetch assigned containers
            Future<Void> registerFuture = smscManager.registerNode(nodeID);
            registerFuture.get();

            // Fetch the list of containers and make sure there is one assigned to this node.
            Future<List<NodeConnectionInfo>> containersFuture = smscManager.getNodesForContainer("0");
            List<NodeConnectionInfo> nodeConnectionInfoList = containersFuture.get();

            assertNotNull(nodeConnectionInfoList);
            assertEquals(0, nodeConnectionInfoList.size());

            // Create a Container
            containerID = createContainer();

            // Fetch the list of containers and make sure there is one assigned to this node.
            containersFuture = smscManager.getNodesForContainer(containerID);
            nodeConnectionInfoList = containersFuture.get();

            assertNotNull(nodeConnectionInfoList);
            assertEquals(1, nodeConnectionInfoList.size());
            NodeConnectionInfo nodeConnectionInfo = nodeConnectionInfoList.get(0);
            assertTrue(nodeConnectionInfo.getNodeID().equals(nodeID));
        } finally {
            if ( containerID != null ) {
                terminateContainer(containerID);
            }
            Future<Void> unregisterFuture = smscManager.unregisterNode(nodeID);
            unregisterFuture.get();
        }
    }

    @Test
    public void testGetContainerInfo() throws Exception {
        SMSCManager smscManager = getSMSCManager();

        String nodeID = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String containerID = null;

        try {
            // We must register the Node in order to fetch assigned containers
            Future<Void> registerFuture = smscManager.registerNode(nodeID);
            registerFuture.get();

            // Fetch the list of containers and make sure there is one assigned to this node.
            try {
                Future<ContainerInfo> containersFuture = smscManager.getContainerInfo("0");
                ContainerInfo containerInfo = containersFuture.get();
            } catch ( ExecutionException e ) {
                assertEquals (NoSuchContainerException.class, e.getCause().getClass() );
            }

            // Create a Container
            containerID = createContainer();

            // Fetch the list of containers and make sure there is one assigned to this node.
            Future<ContainerInfo> containersFuture = smscManager.getContainerInfo(containerID);
            ContainerInfo containerInfo = containersFuture.get();

            assertNotNull(containerInfo);
            assertTrue(containerInfo.getId().equals(containerID));
        } finally {
            if ( containerID != null ) {
                terminateContainer(containerID);
            }
            Future<Void> unregisterFuture = smscManager.unregisterNode(nodeID);
            unregisterFuture.get();
        }
    }

}
