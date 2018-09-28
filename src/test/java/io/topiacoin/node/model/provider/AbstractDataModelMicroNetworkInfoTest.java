package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchMicroNetworkException;
import io.topiacoin.node.model.MicroNetworkInfo;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.MicroNetworkState;
import org.junit.After;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public abstract class AbstractDataModelMicroNetworkInfoTest {

    public abstract DataModel initDataModel();

    public abstract void tearDownDataModel();

    @After
    public void destroy() {
        tearDownDataModel();
    }

    @Test
    public void testMicroNetworkInfoCRUD() throws Exception {
        MicroNetworkInfo testMicroNetworkInfo = new MicroNetworkInfo();
        testMicroNetworkInfo.setId("An ID");
        testMicroNetworkInfo.setContainerID("Another ID");
        testMicroNetworkInfo.setPath("Some Path");
        testMicroNetworkInfo.setState(new MicroNetworkState("state"));
        testMicroNetworkInfo.setRpcURL("arpcUrl");
        testMicroNetworkInfo.setP2pURL("ap2pUrl");

        DataModel dataModel = initDataModel();

        try {
            dataModel.getMicroNetwork("An ID");
            fail();
        } catch (NoSuchMicroNetworkException e) {
            //Good
        }

        MicroNetworkInfo createdMicroNetworkInfo = dataModel.createMicroNetwork(testMicroNetworkInfo.getId(), testMicroNetworkInfo.getContainerID(), testMicroNetworkInfo.getPath(), testMicroNetworkInfo.getState(), testMicroNetworkInfo.getRpcURL(), testMicroNetworkInfo.getP2pURL());

        MicroNetworkInfo fetchedMicroNetworkInfo = dataModel.getMicroNetwork(testMicroNetworkInfo.getId());

        assertEquals(createdMicroNetworkInfo, testMicroNetworkInfo);
        assertEquals(testMicroNetworkInfo, fetchedMicroNetworkInfo);
        assertEquals(fetchedMicroNetworkInfo, createdMicroNetworkInfo);

        assertEquals("An ID", testMicroNetworkInfo.getId());
        assertEquals("Another ID", testMicroNetworkInfo.getContainerID());
        assertEquals("Some Path", testMicroNetworkInfo.getPath());
        assertEquals(new MicroNetworkState("state"), testMicroNetworkInfo.getState());
        assertEquals("arpcUrl", testMicroNetworkInfo.getRpcURL());
        assertEquals("ap2pUrl", testMicroNetworkInfo.getP2pURL());

        assertEquals("An ID", createdMicroNetworkInfo.getId());
        assertEquals("Another ID", createdMicroNetworkInfo.getContainerID());
        assertEquals("Some Path", createdMicroNetworkInfo.getPath());
        assertEquals(new MicroNetworkState("state"), createdMicroNetworkInfo.getState());
        assertEquals("arpcUrl", createdMicroNetworkInfo.getRpcURL());
        assertEquals("ap2pUrl", createdMicroNetworkInfo.getP2pURL());

        assertEquals("An ID", fetchedMicroNetworkInfo.getId());
        assertEquals("Another ID", fetchedMicroNetworkInfo.getContainerID());
        assertEquals("Some Path", fetchedMicroNetworkInfo.getPath());
        assertEquals(new MicroNetworkState("state"), fetchedMicroNetworkInfo.getState());
        assertEquals("arpcUrl", fetchedMicroNetworkInfo.getRpcURL());
        assertEquals("ap2pUrl", fetchedMicroNetworkInfo.getP2pURL());

        testMicroNetworkInfo.setContainerID("new ID");
        testMicroNetworkInfo.setPath("new Path");
        testMicroNetworkInfo.setState(new MicroNetworkState("state2"));
        testMicroNetworkInfo.setRpcURL("another rpcUrl");
        testMicroNetworkInfo.setP2pURL("another p2pUrl");

        dataModel.updateMicroNetwork(testMicroNetworkInfo);

        fetchedMicroNetworkInfo = dataModel.getMicroNetwork(testMicroNetworkInfo.getId());
        assertEquals("An ID", testMicroNetworkInfo.getId());
        assertEquals("new ID", testMicroNetworkInfo.getContainerID());
        assertEquals("new Path", testMicroNetworkInfo.getPath());
        assertEquals(new MicroNetworkState("state2"), testMicroNetworkInfo.getState());
        assertEquals("another rpcUrl", testMicroNetworkInfo.getRpcURL());
        assertEquals("another p2pUrl", testMicroNetworkInfo.getP2pURL());
    }

    @Test
    public void testModifyingMicroNetworkInfoObjectsDoesNotModifyModel() throws Exception {
        MicroNetworkInfo testMicroNetworkInfo = new MicroNetworkInfo();
        testMicroNetworkInfo.setId("An ID");
        testMicroNetworkInfo.setContainerID("Another ID");
        testMicroNetworkInfo.setPath("Some Path");
        testMicroNetworkInfo.setState(new MicroNetworkState("state"));
        testMicroNetworkInfo.setRpcURL("arpcUrl");
        testMicroNetworkInfo.setP2pURL("ap2pUrl");

        DataModel dataModel = initDataModel();

        try {
            dataModel.getMicroNetwork("An ID");
            fail();
        } catch (NoSuchMicroNetworkException e) {
            //Good
        }

        MicroNetworkInfo createdMicroNetworkInfo = dataModel.createMicroNetwork(testMicroNetworkInfo.getId(), testMicroNetworkInfo.getContainerID(), testMicroNetworkInfo.getPath(), testMicroNetworkInfo.getState(), testMicroNetworkInfo.getRpcURL(), testMicroNetworkInfo.getP2pURL());

        MicroNetworkInfo fetchedMicroNetworkInfo = dataModel.getMicroNetwork(testMicroNetworkInfo.getId());
        fetchedMicroNetworkInfo.setContainerID("new ID");
        testMicroNetworkInfo.setPath("new Path");
        testMicroNetworkInfo.setState(new MicroNetworkState("state2"));
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
        testMicroNetworkInfo.setState(new MicroNetworkState("state"));
        testMicroNetworkInfo.setRpcURL("arpcUrl");
        testMicroNetworkInfo.setP2pURL("ap2pUrl");

        DataModel dataModel = initDataModel();

        dataModel.createMicroNetwork(testMicroNetworkInfo.getId(), testMicroNetworkInfo.getContainerID(), testMicroNetworkInfo.getPath(), testMicroNetworkInfo.getState(), testMicroNetworkInfo.getRpcURL(), testMicroNetworkInfo.getP2pURL());
        dataModel.createMicroNetwork(testMicroNetworkInfo.getId(), testMicroNetworkInfo.getContainerID(), testMicroNetworkInfo.getPath(), testMicroNetworkInfo.getState(), testMicroNetworkInfo.getRpcURL(), testMicroNetworkInfo.getP2pURL());
    }

    @Test(expected = NoSuchMicroNetworkException.class)
    public void testUpdateNonExistentContainer() throws Exception {
        MicroNetworkInfo testMicroNetworkInfo = new MicroNetworkInfo();
        testMicroNetworkInfo.setId("An ID");
        testMicroNetworkInfo.setContainerID("Another ID");
        testMicroNetworkInfo.setPath("Some Path");
        testMicroNetworkInfo.setState(new MicroNetworkState("state"));
        testMicroNetworkInfo.setRpcURL("arpcUrl");
        testMicroNetworkInfo.setP2pURL("ap2pUrl");

        DataModel dataModel = initDataModel();

        dataModel.updateMicroNetwork(testMicroNetworkInfo);
    }
}
