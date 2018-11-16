package io.topiacoin.node.smsc;

import io.topiacoin.node.model.NodeConnectionInfo;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public abstract class AbstractSMSCManagerTest {

    protected abstract SMSCManager getSMSCManager() ;

    @Test
    public void testSanity() {
        fail ( "Test Cases Not yet Implemented" ) ;
    }

    @Test
    public void testRegisterUnregisterNode() throws Exception {
        SMSCManager smscManager = getSMSCManager();

        long nodeID = UUID.randomUUID().getLeastSignificantBits();

        Future<Void> future = smscManager.registerNode(nodeID);

        future.get();

        future = smscManager.unregisterNode(nodeID);

        future.get();
    }

    @Test
    public void testGetNodeInfo() throws Exception {
        SMSCManager smscManager = getSMSCManager();

        long nodeID = UUID.randomUUID().getLeastSignificantBits();

        try {
            // We must register the Node in order to fetch assigned containers
            Future<Void> registerFuture = smscManager.registerNode(nodeID);
            registerFuture.get();

            // Fetch the list of containers assigned to this node.
            Future<NodeConnectionInfo> containersFuture = smscManager.getNodeInfo(nodeID);
            NodeConnectionInfo nodeConnectionInfo = containersFuture.get();

            assertNotNull(nodeConnectionInfo);
            assertEquals((Long)nodeID, nodeConnectionInfo.getNodeID()) ;
            assertNotNull(nodeConnectionInfo.getNodeURL());
        } finally {
            Future<Void> unregisterFuture = smscManager.unregisterNode(nodeID);
            unregisterFuture.get();
        }
    }

    @Test
    public void testGetContainers() throws Exception {
        SMSCManager smscManager = getSMSCManager();

        long nodeID = UUID.randomUUID().getLeastSignificantBits();
            
        try {
            // We must register the Node in order to fetch assigned containers
            Future<Void> registerFuture = smscManager.registerNode(nodeID);
            registerFuture.get();

            // Fetch the list of containers assigned to this node.
            Future<List<String>> containersFuture = smscManager.getContainers();
            List<String> assignedContainersList = containersFuture.get();

            assertNotNull(assignedContainersList);
            assertEquals(0, assignedContainersList.size());
        } finally {
            Future<Void> unregisterFuture = smscManager.unregisterNode(nodeID);
            unregisterFuture.get();
        }
    }
}
