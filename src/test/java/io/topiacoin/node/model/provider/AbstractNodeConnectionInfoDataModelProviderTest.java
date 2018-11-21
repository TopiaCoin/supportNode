package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchNodeException;
import io.topiacoin.node.exceptions.NodeConnectionInfoAlreadyExistsException;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.NodeConnectionInfo;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public abstract class AbstractNodeConnectionInfoDataModelProviderTest {

    public abstract DataModel getDataModel();

    @Test
    public void testNodeConnectionInfoCRUD() throws Exception {

        String nodeID = UUID.randomUUID().toString();
        String nodeURL = "http://localhost:1234/";
        String nodeURL2 = "http://remotehost:1234/";

        DataModel dataModel = getDataModel();

        // Verify the Node Connection Info is not in the model
        NodeConnectionInfo fetchedInfo = dataModel.getNodeConnectionInfo(nodeID);
        assertNull(fetchedInfo);

        // Create new info and verify the return object
        fetchedInfo = dataModel.createNodeConnectionInfo(nodeID, nodeURL);
        assertNotNull(fetchedInfo);
        assertEquals(nodeID, fetchedInfo.getNodeID());
        assertEquals(nodeURL, fetchedInfo.getNodeURL());

        // Fetch the created object and verify it
        fetchedInfo = dataModel.getNodeConnectionInfo(nodeID);
        assertNotNull(fetchedInfo);
        assertEquals(nodeID, fetchedInfo.getNodeID());
        assertEquals(nodeURL, fetchedInfo.getNodeURL());

        // Update the object
        fetchedInfo.setNodeURL(nodeURL2);
        dataModel.updateNodeConnectionInfo(fetchedInfo);

        // Fetch the updated object and verify it
        fetchedInfo = dataModel.getNodeConnectionInfo(nodeID);
        assertNotNull(fetchedInfo);
        assertEquals(nodeID, fetchedInfo.getNodeID());
        assertEquals(nodeURL2, fetchedInfo.getNodeURL());

        // Remove the Object
        boolean removed = dataModel.removeNodeConnectionInfo(nodeID);
        assertTrue(removed);

        // Verify the Node Connection Info is no longer in the model
        fetchedInfo = dataModel.getNodeConnectionInfo(nodeID);
        assertNull(fetchedInfo);
    }

    @Test
    public void testAddNodeConnectionInfoAlreadyExists() throws Exception {

        String nodeID = UUID.randomUUID().toString();
        String nodeURL = "http://localhost:1234/";

        // Setup the Data Model for the Test
        DataModel dataModel = getDataModel();
        dataModel.createNodeConnectionInfo(nodeID, nodeURL);

        // Create new info and verify the return object
        try {
            dataModel.createNodeConnectionInfo(nodeID, nodeURL);
            fail("Expected NodeConnectionInfoAlreadyExistsException was not thrown");
        } catch (NodeConnectionInfoAlreadyExistsException e) {
            // NOOP - Expected Exception
        }
    }

    @Test
    public void testRemoveNodeConnectionInfoDoesNotExist() throws Exception {

        String nodeID = UUID.randomUUID().toString();

        // Setup the Data Model for the Test
        DataModel dataModel = getDataModel();

        // Create new info and verify the return object
        boolean removed = dataModel.removeNodeConnectionInfo(nodeID);
        assertFalse(removed);
    }

    @Test
    public void testUpdateNodeConnectionInfoDoesNotExist() throws Exception {

        String nodeID = UUID.randomUUID().toString();
        String nodeURL = "http://localhost:1234/";

        DataModel dataModel = getDataModel();

        // Update the object
        NodeConnectionInfo nodeInfo = new NodeConnectionInfo(nodeID, nodeURL);
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
        NodeConnectionInfo fetchedNodeInfo = dataModel.getNodeConnectionInfo(nodeID);
        assertNull(fetchedNodeInfo) ;
    }

    @Test
    public void testUpdateNodeConnectionInfoDoesNotModifyOtherObjects() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String nodeID = UUID.randomUUID().toString();
        String nodeURL = "http://localhost:1234/";
        String nodeURL2 = "http://remotehost:1234/";

        // Setup the Data Model for the Test
        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);
        NodeConnectionInfo createdInfo = dataModel.createNodeConnectionInfo(nodeID, nodeURL);

        // Make sure that updating the data model does not update previously fetched objects
        NodeConnectionInfo fetchedInfo = dataModel.getNodeConnectionInfo(nodeID);
        createdInfo.setNodeURL(nodeURL2);
        dataModel.updateNodeConnectionInfo(createdInfo);
        assertEquals(nodeURL, fetchedInfo.getNodeURL());

        // Make sure that updating a previously fetched object doesn't update the data model
        fetchedInfo = dataModel.getNodeConnectionInfo(nodeID);

    }

    @Test
    public void testModifyNodeConnectionInfoDoesNotUpdateDataModel() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String nodeID = UUID.randomUUID().toString();
        String nodeURL = "http://localhost:1234/";
        String nodeURL2 = "http://remotehost:1234/";

        // Setup the Data Model for the Test
        DataModel dataModel = getDataModel();
        dataModel.createContainer(containerID, 0, null);
        NodeConnectionInfo createdInfo = dataModel.createNodeConnectionInfo(nodeID, nodeURL);

        // Make sure that updating a previously fetched object doesn't update the data model
        createdInfo.setNodeURL(nodeURL2);

        NodeConnectionInfo fetchedInfo = dataModel.getNodeConnectionInfo(nodeID);
        assertEquals(nodeURL, fetchedInfo.getNodeURL());
    }
}
