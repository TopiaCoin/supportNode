package io.topiacoin.node.rest;

import io.topiacoin.node.BusinessLogic;
import io.topiacoin.node.exceptions.BadRequestException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.model.ContainerConnectionInfo;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static junit.framework.TestCase.*;

public class APIControllerTest {

    @Test
    public void testSanity() {
        fail ( "This test isn't sane!") ;
    }

    // -------- getContainer() --------

    @Test
    public void testGetContainer() throws Exception  {

        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        ContainerConnectionInfo info = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.getContainer(containerID)).andReturn(info);

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        ResponseEntity<ContainerConnectionInfo> response = controller.getContainer(containerID);

        // Verify the expected Results
        assertNotNull(response) ;
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ContainerConnectionInfo fetchedInfo = response.getBody();
        assertNotNull ( fetchedInfo) ;
        assertEquals(containerID, fetchedInfo.getContainerID());
        assertEquals(rpcURL, fetchedInfo.getConnectionURL());
        assertEquals(p2pURL, fetchedInfo.getP2PURL());

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testGetNonExistentContainer() throws Exception  {

        String containerID = UUID.randomUUID().toString();
        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.getContainer(containerID)).andThrow(new NoSuchContainerException());

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        try {
            ResponseEntity<ContainerConnectionInfo> response = controller.getContainer(containerID);
            fail ( "Expected NoSuchContainerException was not thrown");
        } catch ( NoSuchContainerException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testGetContainerWithBlankID() throws Exception  {

        String containerID = "";

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        try {
            ResponseEntity<ContainerConnectionInfo> response = controller.getContainer(containerID);
            fail ( "Expected BadRequestException was not thrown");
        } catch ( BadRequestException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testGetContainerWithNullID() throws Exception  {

        String containerID = null;

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        try {
            ResponseEntity<ContainerConnectionInfo> response = controller.getContainer(containerID);
            fail ( "Expected BadRequestException was not thrown");
        } catch ( BadRequestException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    // -------- createContainer() --------

    @Test
    public void testCreateContainer() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testCreateContainerWithBlankID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testCreateContainerWithNullID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    // -------- replicateContainer() --------
    @Test
    public void testReplicateContainer() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testReplicateNonExistentContainer() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testReplicateContainerWithBlankID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testReplicateContainerWithNullID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testReplicateContainerWithBlankPeerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testReplicateContainerWithNullPeerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testReplicateContainerWithNonExistentPeerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testReplicateContainerWithInvalidPeerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    // -------- removeContainer() --------

    @Test
    public void testRemoveContainer() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testRemoveNonExistentContainer() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testRemoveContainerWithBlankID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testRemoveContainerWithNullID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    // -------- addChunk() --------

    @Test
    public void testAddChunk() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testAddChunkWithNonExistentChunkID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testAddChunkWithNonExistentContainerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testAddChunkWithBlankID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testAddChunkWithNullID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testAddChunkWithBlankContainerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testAddChunkWithNullContainerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    // -------- getChunk() --------

    @Test
    public void testGetChunk() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testGetNonExistentChunk() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testGetChunkFromNonExistentContainer() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testGetChunkWithBlankID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testGetChunkWithNullID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testGetChunkWithBlankContainerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testGetChunkWithNullContainerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    // -------- removeChunk() --------

    @Test
    public void testRemoveChunk() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testRemoveNonExistentChunk() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testRemoveChunkFromNonExistentContainer() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testRemoveChunkWithBlankID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testRemoveChunkWithNullID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testRemoveChunkWithBlankContainerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testRemoveChunkWithNullContainerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    // -------- submitChallenge() --------

    @Test
    public void testSubmitChallenge() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testSubmitChallengeForNonExistentWorkspace() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testSubmitChallengeWithNullChallenege() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testSubmitChallengeWithBlankContainerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testSubmitChallengeWithNullContainerID() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

    @Test
    public void testSubmitChallengeWithNullChunkList() throws Exception  {
        fail ( "Test Not Yet Implemented" ) ;
    }

}
