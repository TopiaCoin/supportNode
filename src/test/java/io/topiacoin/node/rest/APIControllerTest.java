package io.topiacoin.node.rest;

import io.topiacoin.node.BusinessLogic;
import io.topiacoin.node.exceptions.BadRequestException;
import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchNodeException;
import io.topiacoin.node.model.ContainerConnectionInfo;
import io.topiacoin.node.model.ContainerInfo;
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

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
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

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.getContainer(containerID)).andThrow(new NoSuchContainerException());

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
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

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
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

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
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

        String containerID = UUID.randomUUID().toString();

        ContainerCreationRequest request = new ContainerCreationRequest(containerID);
        ContainerInfo info = new ContainerInfo(containerID, 0, null);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.createContainer(containerID)).andReturn(info);

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        ResponseEntity<Void> response = controller.createContainer(request);

        // Verify the expected Results
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testCreateAlreadyExistingContainer() throws Exception  {

        String containerID = UUID.randomUUID().toString();

        ContainerCreationRequest request = new ContainerCreationRequest(containerID);
        ContainerInfo info = new ContainerInfo(containerID, 0, null);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.createContainer(containerID)).andThrow(new ContainerAlreadyExistsException());

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.createContainer(request);
            fail ( "Expected ContainerAlreadyExistsException was not thrown");
        } catch ( ContainerAlreadyExistsException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }
    @Test
    public void testCreateContainerWithBlankID() throws Exception  {

        String containerID = "";

        ContainerCreationRequest request = new ContainerCreationRequest(containerID);
        ContainerInfo info = new ContainerInfo(containerID, 0, null);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.createContainer(request);
            fail ( "Expected BadRequestException was not thrown") ;
        } catch ( BadRequestException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testCreateContainerWithNullID() throws Exception  {

        String containerID = null;

        ContainerCreationRequest request = new ContainerCreationRequest(containerID);
        ContainerInfo info = new ContainerInfo(containerID, 0, null);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.createContainer(request);
            fail ( "Expected BadRequestException was not thrown") ;
        } catch ( BadRequestException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    // -------- replicateContainer() --------

    @Test
    public void testReplicateContainer() throws Exception  {

        String containerID = UUID.randomUUID().toString();
        String peerID = UUID.randomUUID().toString();

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);
        ContainerInfo info = new ContainerInfo(containerID, 0, null);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.replicateContainer(containerID, peerID)).andReturn(info);

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        ResponseEntity<Void> response = controller.replicateContainer(request);

        // Verify the expected Results
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateAlreadyExistingContainer() throws Exception  {

        String containerID = UUID.randomUUID().toString();
        String peerID = UUID.randomUUID().toString();

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);
        ContainerInfo info = new ContainerInfo(containerID, 0, null);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.replicateContainer(containerID, peerID)).andThrow(new MicroNetworkAlreadyExistsException()) ;

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            ResponseEntity<Void> response = controller.replicateContainer(request);
            fail ( "Expected ContainerAlreadyExistsException was not thrown" );
        } catch ( ContainerAlreadyExistsException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateNonExistentContainer() throws Exception  {

        String containerID = UUID.randomUUID().toString();
        String peerID = UUID.randomUUID().toString();

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);
        ContainerInfo info = new ContainerInfo(containerID, 0, null);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.replicateContainer(containerID, peerID)).andThrow(new NoSuchContainerException()) ;

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            ResponseEntity<Void> response = controller.replicateContainer(request);
            fail ( "Expected NoSuchContainerException was not thrown" );
        } catch ( NoSuchContainerException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateContainerWithNonExistentPeerID() throws Exception  {

        String containerID = UUID.randomUUID().toString();
        String peerID = UUID.randomUUID().toString();

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);
        ContainerInfo info = new ContainerInfo(containerID, 0, null);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.replicateContainer(containerID, peerID)).andThrow(new NoSuchNodeException()) ;

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            ResponseEntity<Void> response = controller.replicateContainer(request);
            fail ( "Expected NoSuchNodeException was not thrown" );
        } catch ( NoSuchNodeException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateContainerWithBlankID() throws Exception  {

        String containerID = "";
        String peerID = UUID.randomUUID().toString();

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);
        ContainerInfo info = new ContainerInfo(containerID, 0, null);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            ResponseEntity<Void> response = controller.replicateContainer(request);
            fail ( "Expected BadRequestException was not thrown" );
        } catch ( BadRequestException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateContainerWithNullID() throws Exception  {

        String containerID = null;
        String peerID = UUID.randomUUID().toString();

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);
        ContainerInfo info = new ContainerInfo(containerID, 0, null);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            ResponseEntity<Void> response = controller.replicateContainer(request);
            fail ( "Expected BadRequestException was not thrown" );
        } catch ( BadRequestException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateContainerWithBlankPeerID() throws Exception  {

        String containerID = UUID.randomUUID().toString();
        String peerID = "";

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);
        ContainerInfo info = new ContainerInfo(containerID, 0, null);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            ResponseEntity<Void> response = controller.replicateContainer(request);
            fail ( "Expected BadRequestException was not thrown" );
        } catch ( BadRequestException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateContainerWithNullPeerID() throws Exception  {

        String containerID = UUID.randomUUID().toString();
        String peerID = null;

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);
        ContainerInfo info = new ContainerInfo(containerID, 0, null);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            ResponseEntity<Void> response = controller.replicateContainer(request);
            fail ( "Expected BadRequestException was not thrown" );
        } catch ( BadRequestException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    // -------- removeContainer() --------

    @Test
    public void testRemoveContainer() throws Exception  {

        String containerID = UUID.randomUUID().toString();

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        businessLogic.removeContainer(containerID);
        EasyMock.expectLastCall();

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        ResponseEntity<Void> response = controller.removeContainer(containerID);

        // Verify the expected Results
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testRemoveNonExistentContainer() throws Exception  {

        String containerID = UUID.randomUUID().toString();

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        businessLogic.removeContainer(containerID);
        EasyMock.expectLastCall().andThrow(new NoSuchContainerException());

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.removeContainer(containerID);
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
    public void testRemoveContainerWithBlankID() throws Exception  {

        String containerID = "";

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.removeContainer(containerID);
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
    public void testRemoveContainerWithNullID() throws Exception  {

        String containerID = null;

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.removeContainer(containerID);
            fail ( "Expected BadRequestException was not thrown");
        } catch ( BadRequestException e ) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
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
