package io.topiacoin.node.smsc;

import io.topiacoin.eosrpcadapter.exceptions.ChainException;
import io.topiacoin.eosrpcadapter.exceptions.WalletException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.model.ChallengeSolution;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.Dispute;
import io.topiacoin.node.model.NodeConnectionInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public abstract class AbstractSMSCManagerTest {

    protected abstract SMSCManager getSMSCManager() ;
    protected abstract String createContainer() throws WalletException, ChainException;
    protected abstract void terminateContainer(String containerID) throws WalletException, ChainException;
    protected abstract void submitProofSolutionHash(String containerID, String nodeID, String verificationValue, String transactionID, int blockNumber, String chunkHash) throws Exception;
    protected abstract String fileDispute(String containerID, String nodeID, String nodeURL, List<String> chunkIDs) throws Exception;

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
            if ( smscManager.isRegistered() ) {
                Future<Void> unregisterFuture = smscManager.unregisterNode(nodeID);
                unregisterFuture.get();
            }
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
            if ( smscManager.isRegistered() ) {
                Future<Void> unregisterFuture = smscManager.unregisterNode(nodeID);
                unregisterFuture.get();
            }
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
            if ( smscManager.isRegistered() ) {
                Future<Void> unregisterFuture = smscManager.unregisterNode(nodeID);
                unregisterFuture.get();
            }
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
            if ( smscManager.isRegistered() ) {
                Future<Void> unregisterFuture = smscManager.unregisterNode(nodeID);
                unregisterFuture.get();
            }
        }
    }

    @Test
    public void testSubmitProofSolution() throws Exception {
        SMSCManager smscManager = getSMSCManager();

        String nodeID = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String containerID = null;

        try {
            // We must register the Node in order to fetch assigned containers
            Future<Void> registerFuture = smscManager.registerNode(nodeID);
            registerFuture.get();

            // Create a Container
            containerID = createContainer();

            // Submit a Proof Solution
            String verificationValue = "foo";
            String transactionID = "bar";
            int blockNumber = 100;
            String chunkHash = "baz";

            submitProofSolutionHash(containerID, nodeID, verificationValue, transactionID, blockNumber, chunkHash);

            // Submit Proof Solution
            ChallengeSolution solution = new ChallengeSolution(verificationValue, transactionID, blockNumber, chunkHash);
            Future<Void> solutionFuture = smscManager.submitProofSolution(containerID, solution);

            solutionFuture.get();
        } finally {
            if ( containerID != null ) {
                terminateContainer(containerID);
            }
            if ( smscManager.isRegistered() ) {
                Future<Void> unregisterFuture = smscManager.unregisterNode(nodeID);
                unregisterFuture.get();
            }
        }
    }

    @Test
    public void testGetAssignedDisputes() throws Exception {
        // In order to file a dispute, there must be at least 3 available
        // nodes to act as arbitrators.
        SMSCManager smscManager1 = getSMSCManager();
        SMSCManager smscManager2 = getSMSCManager();
        SMSCManager smscManager3 = getSMSCManager();
        SMSCManager smscManager4 = getSMSCManager();

        String nodeID1 = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String nodeID2 = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String nodeID3 = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String nodeID4 = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String containerID = null;

        try {
            // We must register a Node in order to create a container
            Future<Void> registerFuture = smscManager1.registerNode(nodeID1);
            registerFuture.get();

            // Create a Container - It will be assigned to NodeID 1 as it is the only node.
            containerID = createContainer();

            // We must register a Node in order to have available arbitrators
            registerFuture = smscManager2.registerNode(nodeID2);
            registerFuture.get();
            registerFuture = smscManager3.registerNode(nodeID3);
            registerFuture.get();
            registerFuture = smscManager4.registerNode(nodeID4);
            registerFuture.get();

            // Grab the list of Disputes assigned to the node 1.  Should be empty.
            Future<List<Dispute>> disputesFuture = smscManager1.getAssignedDisputes();
            List<Dispute> disputeList = disputesFuture.get();
            assertNotNull(disputeList);
            assertEquals(0, disputeList.size());

            // Grab the list of Disputes assigned to the node 2.  Should be empty.
            disputesFuture = smscManager2.getAssignedDisputes();
            disputeList = disputesFuture.get();
            assertNotNull(disputeList);
            assertEquals(0, disputeList.size());

            // File a Dispute
            List<String> chunkList = new ArrayList<>();
            String disputeID = fileDispute(containerID, nodeID1, "url", chunkList);

            assertNotNull(disputeID);

            // Grab the list of Disputes assigned to node 1.  Should be empty.
            disputesFuture = smscManager1.getAssignedDisputes();
            disputeList = disputesFuture.get();
            assertNotNull(disputeList);
            assertEquals(0, disputeList.size());

            // Grab the list of Disputes assigned to node 2.  Should have 1.
            disputesFuture = smscManager2.getAssignedDisputes();
            disputeList = disputesFuture.get();
            assertNotNull(disputeList);
            assertEquals(1, disputeList.size());

            // Grab the list of Disputes assigned to node 3.  Should have 1.
            disputesFuture = smscManager3.getAssignedDisputes();
            disputeList = disputesFuture.get();
            assertNotNull(disputeList);
            assertEquals(1, disputeList.size());

            // Grab the list of Disputes assigned to node 4.  Should have 1.
            disputesFuture = smscManager4.getAssignedDisputes();
            disputeList = disputesFuture.get();
            assertNotNull(disputeList);
            assertEquals(1, disputeList.size());
        } finally {
            if ( containerID != null ) {
                terminateContainer(containerID);
            }
            if ( smscManager1.isRegistered() ) {
                try {
                    Future<Void> unregisterFuture = smscManager1.unregisterNode(nodeID1);
                    unregisterFuture.get();
                } catch (Exception e) {
                    // NOOP
                }
            }

            if ( smscManager2.isRegistered() ) {
                try {
                    Future<Void> unregisterFuture = smscManager2.unregisterNode(nodeID2);
                    unregisterFuture.get();
                } catch (Exception e) {
                    // NOOP
                }
            }

            if ( smscManager3.isRegistered() ) {
                try {
                    Future<Void> unregisterFuture = smscManager3.unregisterNode(nodeID3);
                    unregisterFuture.get();
                } catch (Exception e) {
                    // NOOP
                }
            }

            if ( smscManager4.isRegistered() ) {
                try {
                    Future<Void> unregisterFuture = smscManager4.unregisterNode(nodeID4);
                    unregisterFuture.get();
                } catch (Exception e) {
                    // NOOP
                }
            }
        }
    }

    @Test
    public void testGetDispute() throws Exception {
        // In order to file a dispute, there must be at least 3 available
        // nodes to act as arbitrators.
        SMSCManager smscManager1 = getSMSCManager();
        SMSCManager smscManager2 = getSMSCManager();
        SMSCManager smscManager3 = getSMSCManager();
        SMSCManager smscManager4 = getSMSCManager();

        String nodeID1 = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String nodeID2 = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String nodeID3 = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String nodeID4 = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String containerID = null;

        try {
            // We must register a Node in order to create a container
            Future<Void> registerFuture = smscManager1.registerNode(nodeID1);
            registerFuture.get();

            // Create a Container - It will be assigned to NodeID 1 as it is the only node.
            containerID = createContainer();

            // We must register a Node in order to have available arbitrators
            registerFuture = smscManager2.registerNode(nodeID2);
            registerFuture.get();
            registerFuture = smscManager3.registerNode(nodeID3);
            registerFuture.get();
            registerFuture = smscManager4.registerNode(nodeID4);
            registerFuture.get();

            // File a Dispute
            List<String> chunkList = new ArrayList<>();
            String disputeID = fileDispute(containerID, nodeID1, "url", chunkList);

            assertNotNull(disputeID);

            // Fetch the Dispute
            Future<Dispute> disputeFuture = smscManager1.getDispute(disputeID);
            Dispute dispute = disputeFuture.get();
            assertEquals(containerID, dispute.getContainerID());
            assertEquals(nodeID1, dispute.getDisputedNodeID());
        } finally {
            if ( containerID != null ) {
                terminateContainer(containerID);
            }

            if ( smscManager1.isRegistered()) {
                try {
                    Future<Void> unregisterFuture = smscManager1.unregisterNode(nodeID1);
                    unregisterFuture.get();
                } catch (Exception e) {
                    // NOOP
                }
            }

            if ( smscManager2.isRegistered() ) {
                try {
                    Future<Void> unregisterFuture = smscManager2.unregisterNode(nodeID2);
                    unregisterFuture.get();
                } catch (Exception e) {
                    // NOOP
                }
            }

            if ( smscManager3.isRegistered() ) {
                try {
                    Future<Void> unregisterFuture = smscManager3.unregisterNode(nodeID3);
                    unregisterFuture.get();
                } catch (Exception e) {
                    // NOOP
                }
            }

            if ( smscManager4.isRegistered()) {
                try {
                    Future<Void> unregisterFuture = smscManager4.unregisterNode(nodeID4);
                    unregisterFuture.get();
                } catch (Exception e) {
                    // NOOP
                }
            }
        }
    }

    @Test
    public void testSendDisputeResolution() throws Exception {
        // In order to file a dispute, there must be at least 3 available
        // nodes to act as arbitrators.
        SMSCManager smscManager1 = getSMSCManager();
        SMSCManager smscManager2 = getSMSCManager();
        SMSCManager smscManager3 = getSMSCManager();
        SMSCManager smscManager4 = getSMSCManager();

        String nodeID1 = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String nodeID2 = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String nodeID3 = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String nodeID4 = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());
        String containerID = null;

        try {
            // We must register a Node in order to create a container
            Future<Void> registerFuture = smscManager1.registerNode(nodeID1);
            registerFuture.get();

            // Create a Container - It will be assigned to NodeID 1 as it is the only node.
            containerID = createContainer();

            // We must register a Node in order to have available arbitrators
            registerFuture = smscManager2.registerNode(nodeID2);
            registerFuture.get();
            registerFuture = smscManager3.registerNode(nodeID3);
            registerFuture.get();
            registerFuture = smscManager4.registerNode(nodeID4);
            registerFuture.get();

            // File a Dispute
            List<String> chunkList = new ArrayList<>();
            String disputeID = fileDispute(containerID, nodeID1, "url", chunkList);

            assertNotNull(disputeID);

            // Fetch the Dispute
            Future<Dispute> disputeFuture = smscManager2.getDispute(disputeID);
            Dispute dispute = disputeFuture.get();
            assertEquals(containerID, dispute.getContainerID());
            assertEquals(nodeID1, dispute.getDisputedNodeID());
            assertEquals("PENDING", dispute.getStatus());

            // Node 2 respond to the dispute
            smscManager2.sendDisputeResolution(disputeID, "NODE");

            // Fetch the Dispute
            disputeFuture = smscManager2.getDispute(disputeID);
            dispute = disputeFuture.get();
            assertEquals(containerID, dispute.getContainerID());
            assertEquals(nodeID1, dispute.getDisputedNodeID());
            assertEquals("PENDING", dispute.getStatus());

            // Node 3 respond to the dispute
            smscManager3.sendDisputeResolution(disputeID, "NODE");

            // Fetch the Dispute
            disputeFuture = smscManager3.getDispute(disputeID);
            dispute = disputeFuture.get();
            assertEquals(containerID, dispute.getContainerID());
            assertEquals(nodeID1, dispute.getDisputedNodeID());
            assertEquals("PENDING", dispute.getStatus());

            // Node 4 respond to the dispute
            smscManager4.sendDisputeResolution(disputeID, "NODE");

            // Fetch the Dispute
            disputeFuture = smscManager4.getDispute(disputeID);
            dispute = disputeFuture.get();
            assertEquals(containerID, dispute.getContainerID());
            assertEquals(nodeID1, dispute.getDisputedNodeID());
            assertEquals("RESOLVED", dispute.getStatus());

        } finally {
            if ( containerID != null ) {
                terminateContainer(containerID);
            }

            if ( smscManager1.isRegistered()) {
                try {
                    Future<Void> unregisterFuture = smscManager1.unregisterNode(nodeID1);
                    unregisterFuture.get();
                } catch (Exception e) {
                    // NOOP
                }
            }

            if ( smscManager2.isRegistered() ) {
                try {
                    Future<Void> unregisterFuture = smscManager2.unregisterNode(nodeID2);
                    unregisterFuture.get();
                } catch (Exception e) {
                    // NOOP
                }
            }

            if ( smscManager3.isRegistered() ) {
                try {
                    Future<Void> unregisterFuture = smscManager3.unregisterNode(nodeID3);
                    unregisterFuture.get();
                } catch (Exception e) {
                    // NOOP
                }
            }

            if ( smscManager4.isRegistered()) {
                try {
                    Future<Void> unregisterFuture = smscManager4.unregisterNode(nodeID4);
                    unregisterFuture.get();
                } catch (Exception e) {
                    // NOOP
                }
            }
        }
    }

}
