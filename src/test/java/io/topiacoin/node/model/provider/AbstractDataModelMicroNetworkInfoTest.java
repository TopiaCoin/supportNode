package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchMicroNetworkException;
import io.topiacoin.node.model.MicroNetworkInfo;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.MicroNetworkState;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public abstract class AbstractDataModelMicroNetworkInfoTest {

    public abstract DataModel getDataModel();

    @Test
    public void testMicroNetworkInfoCRUD() throws Exception {
        MicroNetworkInfo testMicroNetworkInfo = new MicroNetworkInfo();
        testMicroNetworkInfo.setId("An ID");
        testMicroNetworkInfo.setContainerID("Another ID");
        testMicroNetworkInfo.setPath("Some Path");
        testMicroNetworkInfo.setState(MicroNetworkState.STARTING);
        testMicroNetworkInfo.setRpcURL("arpcUrl");
        testMicroNetworkInfo.setP2pURL("ap2pUrl");

        DataModel dataModel = getDataModel();

        MicroNetworkInfo fetchedMicroNetworkInfo = dataModel.getMicroNetwork("An ID");

        assertNull(fetchedMicroNetworkInfo);

        MicroNetworkInfo createdMicroNetworkInfo = dataModel.createMicroNetwork(testMicroNetworkInfo.getId(), testMicroNetworkInfo.getContainerID(), testMicroNetworkInfo.getPath(), testMicroNetworkInfo.getState(), testMicroNetworkInfo.getRpcURL(), testMicroNetworkInfo.getP2pURL());

        fetchedMicroNetworkInfo = dataModel.getMicroNetwork(testMicroNetworkInfo.getId());

        assertEquals(createdMicroNetworkInfo, testMicroNetworkInfo);
        assertEquals(testMicroNetworkInfo, fetchedMicroNetworkInfo);
        assertEquals(fetchedMicroNetworkInfo, createdMicroNetworkInfo);

        assertEquals("An ID", testMicroNetworkInfo.getId());
        assertEquals("Another ID", testMicroNetworkInfo.getContainerID());
        assertEquals("Some Path", testMicroNetworkInfo.getPath());
        assertEquals(MicroNetworkState.STARTING, testMicroNetworkInfo.getState());
        assertEquals("arpcUrl", testMicroNetworkInfo.getRpcURL());
        assertEquals("ap2pUrl", testMicroNetworkInfo.getP2pURL());

        assertEquals("An ID", createdMicroNetworkInfo.getId());
        assertEquals("Another ID", createdMicroNetworkInfo.getContainerID());
        assertEquals("Some Path", createdMicroNetworkInfo.getPath());
        assertEquals(MicroNetworkState.STARTING, createdMicroNetworkInfo.getState());
        assertEquals("arpcUrl", createdMicroNetworkInfo.getRpcURL());
        assertEquals("ap2pUrl", createdMicroNetworkInfo.getP2pURL());

        assertEquals("An ID", fetchedMicroNetworkInfo.getId());
        assertEquals("Another ID", fetchedMicroNetworkInfo.getContainerID());
        assertEquals("Some Path", fetchedMicroNetworkInfo.getPath());
        assertEquals(MicroNetworkState.STARTING, fetchedMicroNetworkInfo.getState());
        assertEquals("arpcUrl", fetchedMicroNetworkInfo.getRpcURL());
        assertEquals("ap2pUrl", fetchedMicroNetworkInfo.getP2pURL());

        testMicroNetworkInfo.setContainerID("new ID");
        testMicroNetworkInfo.setPath("new Path");
        testMicroNetworkInfo.setState(MicroNetworkState.RUNNING);
        testMicroNetworkInfo.setRpcURL("another rpcUrl");
        testMicroNetworkInfo.setP2pURL("another p2pUrl");

        dataModel.updateMicroNetwork(testMicroNetworkInfo);

        fetchedMicroNetworkInfo = dataModel.getMicroNetwork(testMicroNetworkInfo.getId());
        assertEquals("An ID", testMicroNetworkInfo.getId());
        assertEquals("new ID", testMicroNetworkInfo.getContainerID());
        assertEquals("new Path", testMicroNetworkInfo.getPath());
        assertEquals(MicroNetworkState.RUNNING, testMicroNetworkInfo.getState());
        assertEquals("another rpcUrl", testMicroNetworkInfo.getRpcURL());
        assertEquals("another p2pUrl", testMicroNetworkInfo.getP2pURL());
    }

    @Test
    public void testModifyingMicroNetworkInfoObjectsDoesNotModifyModel() throws Exception {
        MicroNetworkInfo testMicroNetworkInfo = new MicroNetworkInfo();
        testMicroNetworkInfo.setId("An ID");
        testMicroNetworkInfo.setContainerID("Another ID");
        testMicroNetworkInfo.setPath("Some Path");
        testMicroNetworkInfo.setState(MicroNetworkState.STARTING);
        testMicroNetworkInfo.setRpcURL("arpcUrl");
        testMicroNetworkInfo.setP2pURL("ap2pUrl");

        DataModel dataModel = getDataModel();

        MicroNetworkInfo fetchedMicroNetworkInfo = dataModel.getMicroNetwork("An ID");
        assertNull(fetchedMicroNetworkInfo);

        MicroNetworkInfo createdMicroNetworkInfo = dataModel.createMicroNetwork(testMicroNetworkInfo.getId(), testMicroNetworkInfo.getContainerID(), testMicroNetworkInfo.getPath(), testMicroNetworkInfo.getState(), testMicroNetworkInfo.getRpcURL(), testMicroNetworkInfo.getP2pURL());

        fetchedMicroNetworkInfo = dataModel.getMicroNetwork(testMicroNetworkInfo.getId());
        fetchedMicroNetworkInfo.setContainerID("new ID");
        testMicroNetworkInfo.setPath("new Path");
        testMicroNetworkInfo.setState(MicroNetworkState.RUNNING);
        testMicroNetworkInfo.setRpcURL("another rpcUrl");
        testMicroNetworkInfo.setP2pURL("another p2pUrl");

        MicroNetworkInfo fetchedMicroNetworkInfo2 = dataModel.getMicroNetwork(testMicroNetworkInfo.getId());
        assertNotEquals(fetchedMicroNetworkInfo, fetchedMicroNetworkInfo2);
    }

    @Test(expected = MicroNetworkAlreadyExistsException.class)
    public void testCreateDuplicateContainer() throws Exception {
        MicroNetworkInfo testMicroNetworkInfo = new MicroNetworkInfo();
        testMicroNetworkInfo.setId("An ID");
        testMicroNetworkInfo.setContainerID("Another ID");
        testMicroNetworkInfo.setPath("Some Path");
        testMicroNetworkInfo.setState(MicroNetworkState.STARTING);
        testMicroNetworkInfo.setRpcURL("arpcUrl");
        testMicroNetworkInfo.setP2pURL("ap2pUrl");

        DataModel dataModel = getDataModel();

        dataModel.createMicroNetwork(testMicroNetworkInfo.getId(), testMicroNetworkInfo.getContainerID(), testMicroNetworkInfo.getPath(), testMicroNetworkInfo.getState(), testMicroNetworkInfo.getRpcURL(), testMicroNetworkInfo.getP2pURL());
        dataModel.createMicroNetwork(testMicroNetworkInfo.getId(), testMicroNetworkInfo.getContainerID(), testMicroNetworkInfo.getPath(), testMicroNetworkInfo.getState(), testMicroNetworkInfo.getRpcURL(), testMicroNetworkInfo.getP2pURL());
    }

    @Test(expected = NoSuchMicroNetworkException.class)
    public void testUpdateNonExistentContainer() throws Exception {
        MicroNetworkInfo testMicroNetworkInfo = new MicroNetworkInfo();
        testMicroNetworkInfo.setId("An ID");
        testMicroNetworkInfo.setContainerID("Another ID");
        testMicroNetworkInfo.setPath("Some Path");
        testMicroNetworkInfo.setState(MicroNetworkState.STARTING);
        testMicroNetworkInfo.setRpcURL("arpcUrl");
        testMicroNetworkInfo.setP2pURL("ap2pUrl");

        DataModel dataModel = getDataModel();

        dataModel.updateMicroNetwork(testMicroNetworkInfo);
    }

    @Test
    public void testRemoveMicroNetwork() throws Exception {

        String netID = UUID.randomUUID().toString();
        String containerID = UUID.randomUUID().toString();
        String path = "/dev/null";
        MicroNetworkState state = MicroNetworkState.STARTING;
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";

        DataModel dataModel = getDataModel();

        dataModel.createMicroNetwork(netID, containerID, path, state, rpcURL, p2pURL);

        dataModel.removeMicroNetwork(netID);

        MicroNetworkInfo fetchedMicroNetworkInfo = dataModel.getMicroNetwork(netID);
        assertNull(fetchedMicroNetworkInfo);
    }

    @Test
    public void testRemoveNonExistentMicroNetwork() throws Exception {

        String netID = UUID.randomUUID().toString();

        DataModel dataModel = getDataModel();

        dataModel.removeMicroNetwork(netID);
    }
}
