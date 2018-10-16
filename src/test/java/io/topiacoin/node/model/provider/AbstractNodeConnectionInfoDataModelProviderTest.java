package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchMicroNetworkException;
import io.topiacoin.node.exceptions.NoSuchNodeException;
import io.topiacoin.node.exceptions.NodeConnectionInfoAlreadyExistsException;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.MicroNetworkInfo;
import io.topiacoin.node.model.MicroNetworkState;
import io.topiacoin.node.model.NodeConnectionInfo;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public abstract class AbstractNodeConnectionInfoDataModelProviderTest {

    public abstract DataModel getDataModel();

    @Test
    public void testNodeConnectionInfoCRUD() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String nodeID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String rpcURL2 = "http://remotehost:1234/";
        String p2pURL = "http://localhost:2345/";
        String p2pURL2 = "http://remotehost:2345/";

        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);

        // Verify the Node Connection Info is not in the model
        NodeConnectionInfo fetchedInfo = dataModel.getNodeConnectionInfo(containerID, nodeID);
        assertNull(fetchedInfo);

        // Create new info and verify the return object
        fetchedInfo = dataModel.createNodeConnectionInfo(containerID, nodeID, rpcURL, p2pURL);
        assertNotNull(fetchedInfo);
        assertEquals(containerID, fetchedInfo.getContainerID());
        assertEquals(nodeID, fetchedInfo.getNodeID());
        assertEquals(rpcURL, fetchedInfo.getRpcURL());
        assertEquals(p2pURL, fetchedInfo.getP2PURL());

        // Fetch the created object and verify it
        fetchedInfo = dataModel.getNodeConnectionInfo(containerID, nodeID);
        assertNotNull(fetchedInfo);
        assertEquals(containerID, fetchedInfo.getContainerID());
        assertEquals(nodeID, fetchedInfo.getNodeID());
        assertEquals(rpcURL, fetchedInfo.getRpcURL());
        assertEquals(p2pURL, fetchedInfo.getP2PURL());

        // Update the object
        fetchedInfo.setP2pURL(p2pURL2);
        fetchedInfo.setRpcURL(rpcURL2);
        dataModel.updateNodeConnectionInfo(fetchedInfo);

        // Fetch the updated object and verify it
        fetchedInfo = dataModel.getNodeConnectionInfo(containerID, nodeID);
        assertNotNull(fetchedInfo);
        assertEquals(containerID, fetchedInfo.getContainerID());
        assertEquals(nodeID, fetchedInfo.getNodeID());
        assertEquals(rpcURL2, fetchedInfo.getRpcURL());
        assertEquals(p2pURL2, fetchedInfo.getP2PURL());

        // Remove the Object
        boolean removed = dataModel.removeNodeConnectionInfo(containerID, nodeID);
        assertTrue(removed);

        // Verify the Node Connection Info is no longer in the model
        fetchedInfo = dataModel.getNodeConnectionInfo(containerID, nodeID);
        assertNull(fetchedInfo);
    }

    @Test
    public void testAddNodeConnectionInfoAlreadyExists() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String nodeID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";

        // Setup the Data Model for the Test
        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);
        dataModel.createNodeConnectionInfo(containerID, nodeID, rpcURL, p2pURL);

        // Create new info and verify the return object
        try {
            dataModel.createNodeConnectionInfo(containerID, nodeID, rpcURL, p2pURL);
            fail("Expected NodeConnectionInfoAlreadyExistsException was not thrown");
        } catch (NodeConnectionInfoAlreadyExistsException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testAddNodeConnectionInfoNonExistentContainer() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String nodeID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";

        // Setup the Data Model for the Test
        DataModel dataModel = getDataModel();

        // Create new info and verify the return object
        try {
            dataModel.createNodeConnectionInfo(containerID, nodeID, rpcURL, p2pURL);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testRemoveNodeConnectionInfoDoesNotExist() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String nodeID = UUID.randomUUID().toString();

        // Setup the Data Model for the Test
        DataModel dataModel = getDataModel();

        // Create new info and verify the return object
        boolean removed = dataModel.removeNodeConnectionInfo(containerID, nodeID);
        assertFalse(removed);
    }

    @Test
    public void testUpdateNodeConnectionInfoDoesNotExist() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String nodeID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";

        DataModel dataModel = getDataModel();

        // Update the object
        NodeConnectionInfo nodeInfo = new NodeConnectionInfo(containerID, nodeID, rpcURL, p2pURL);
        try {
            dataModel.updateNodeConnectionInfo(nodeInfo);
        } catch (NoSuchNodeException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testGetNodeConnectionInfoDoesNotExist() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String nodeID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();

        // Update the object
        NodeConnectionInfo fetchedNodeInfo = dataModel.getNodeConnectionInfo(containerID, nodeID);
        assertNull(fetchedNodeInfo) ;
    }

    @Test
    public void testUpdateNodeConnectionInfoDoesNotModifyOtherObjects() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String nodeID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String rpcURL2 = "http://remotehost:1234/";
        String p2pURL = "http://localhost:2345/";
        String p2pURL2 = "http://remotehost:2345/";

        // Setup the Data Model for the Test
        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);
        NodeConnectionInfo createdInfo = dataModel.createNodeConnectionInfo(containerID, nodeID, rpcURL, p2pURL);

        // Make sure that updating the data model does not update previously fetched objects
        NodeConnectionInfo fetchedInfo = dataModel.getNodeConnectionInfo(containerID, nodeID);
        createdInfo.setRpcURL(rpcURL2);
        createdInfo.setP2pURL(p2pURL2);
        dataModel.updateNodeConnectionInfo(createdInfo);
        assertEquals(rpcURL, fetchedInfo.getRpcURL());
        assertEquals(p2pURL, fetchedInfo.getP2PURL());

        // Make sure that updating a previously fetched object doesn't update the data model
        fetchedInfo = dataModel.getNodeConnectionInfo(containerID, nodeID);

    }

    @Test
    public void testModifyNodeConnectionInfoDoesNotUpdateDataModel() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String nodeID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String rpcURL2 = "http://remotehost:1234/";
        String p2pURL = "http://localhost:2345/";
        String p2pURL2 = "http://remotehost:2345/";

        // Setup the Data Model for the Test
        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);
        NodeConnectionInfo createdInfo = dataModel.createNodeConnectionInfo(containerID, nodeID, rpcURL, p2pURL);

        // Make sure that updating a previously fetched object doesn't update the data model
        createdInfo.setRpcURL(rpcURL2);
        createdInfo.setP2pURL(p2pURL2);

        NodeConnectionInfo fetchedInfo = dataModel.getNodeConnectionInfo(containerID, nodeID);
        assertEquals(rpcURL, fetchedInfo.getRpcURL());
        assertEquals(p2pURL, fetchedInfo.getP2PURL());
    }
}
