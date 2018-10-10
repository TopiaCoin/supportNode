package io.topiacoin.node.rest;

import io.topiacoin.node.BusinessLogic;
import io.topiacoin.node.exceptions.BadRequestException;
import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.exceptions.NoSuchNodeException;
import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ChallengeChunkInfo;
import io.topiacoin.node.model.ContainerConnectionInfo;
import io.topiacoin.node.model.ContainerInfo;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

public class APIControllerTest {

    // -------- getContainer() --------

    @Test
    public void testGetContainer() throws Exception {

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
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ContainerConnectionInfo fetchedInfo = response.getBody();
        assertNotNull(fetchedInfo);
        assertEquals(containerID, fetchedInfo.getContainerID());
        assertEquals(rpcURL, fetchedInfo.getConnectionURL());
        assertEquals(p2pURL, fetchedInfo.getP2PURL());

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testGetNonExistentContainer() throws Exception {

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
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testGetContainerWithBlankID() throws Exception {

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
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testGetContainerWithNullID() throws Exception {

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
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    // -------- createContainer() --------

    @Test
    public void testCreateContainer() throws Exception {

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
    public void testCreateAlreadyExistingContainer() throws Exception {

        String containerID = UUID.randomUUID().toString();

        ContainerCreationRequest request = new ContainerCreationRequest(containerID);

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
            fail("Expected ContainerAlreadyExistsException was not thrown");
        } catch (ContainerAlreadyExistsException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testCreateNonExistentContainer() throws Exception {

        String containerID = UUID.randomUUID().toString();

        ContainerCreationRequest request = new ContainerCreationRequest(containerID);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.createContainer(containerID)).andThrow(new NoSuchContainerException());

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.createContainer(request);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testCreateContainerWithNullRequest() throws Exception {

        String containerID = UUID.randomUUID().toString();

        ContainerCreationRequest request = null;

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
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testCreateContainerWithBlankID() throws Exception {

        String containerID = "";

        ContainerCreationRequest request = new ContainerCreationRequest(containerID);

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
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testCreateContainerWithNullID() throws Exception {

        String containerID = null;

        ContainerCreationRequest request = new ContainerCreationRequest(containerID);

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
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    // -------- replicateContainer() --------

    @Test
    public void testReplicateContainer() throws Exception {

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
    public void testReplicateAlreadyExistingContainer() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String peerID = UUID.randomUUID().toString();

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.replicateContainer(containerID, peerID)).andThrow(new MicroNetworkAlreadyExistsException());

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            ResponseEntity<Void> response = controller.replicateContainer(request);
            fail("Expected ContainerAlreadyExistsException was not thrown");
        } catch (ContainerAlreadyExistsException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateNonExistentContainer() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String peerID = UUID.randomUUID().toString();

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.replicateContainer(containerID, peerID)).andThrow(new NoSuchContainerException());

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            ResponseEntity<Void> response = controller.replicateContainer(request);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateContainerWithNonExistentPeerID() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String peerID = UUID.randomUUID().toString();

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.replicateContainer(containerID, peerID)).andThrow(new NoSuchNodeException());

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            ResponseEntity<Void> response = controller.replicateContainer(request);
            fail("Expected NoSuchNodeException was not thrown");
        } catch (NoSuchNodeException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateContainerWithNullRequest() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String peerID = UUID.randomUUID().toString();

        ContainerReplicationRequest request = null;

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
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateContainerWithBlankID() throws Exception {

        String containerID = "";
        String peerID = UUID.randomUUID().toString();

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);

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
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateContainerWithNullID() throws Exception {

        String containerID = null;
        String peerID = UUID.randomUUID().toString();

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);

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
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateContainerWithBlankPeerID() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String peerID = "";

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);

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
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testReplicateContainerWithNullPeerID() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String peerID = null;

        ContainerReplicationRequest request = new ContainerReplicationRequest(containerID, peerID);

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
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    // -------- removeContainer() --------

    @Test
    public void testRemoveContainer() throws Exception {

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
    public void testRemoveNonExistentContainer() throws Exception {

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
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testRemoveContainerWithBlankID() throws Exception {

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
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }


    @Test
    public void testRemoveContainerWithNullID() throws Exception {

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
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    // -------- addChunk() --------

    @Test
    public void testAddChunk() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        String dataHash = "SHA-256:deadbeef";
        ServletInputStream dataStream = null;

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);

        // Setup Expectations
        businessLogic.storeChunk(containerID, chunkID, dataHash, dataStream);
        EasyMock.expectLastCall();
        EasyMock.expect(request.getInputStream()).andReturn(dataStream);

        // Replay Mock Objects
        EasyMock.replay(businessLogic, request);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        ResponseEntity<Void> response = controller.addChunk(chunkID, containerID, dataHash, request);

        // Verify the expected Results
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Verify the Mock Objects
        EasyMock.verify(businessLogic, request);
    }

    @Test
    public void testAddChunkWithNonExistentContainerID() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        String dataHash = "SHA-256:deadbeef";
        ServletInputStream dataStream = null;

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);

        // Setup Expectations
        EasyMock.expect(request.getInputStream()).andReturn(dataStream);
        businessLogic.storeChunk(containerID, chunkID, dataHash, dataStream);
        EasyMock.expectLastCall().andThrow(new NoSuchContainerException());

        // Replay Mock Objects
        EasyMock.replay(businessLogic, request);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.addChunk(chunkID, containerID, dataHash, request);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic, request);
    }

    @Test
    public void testAddChunkWithBlankID() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = "";
        String dataHash = "SHA-256:deadbeef";

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic, request);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.addChunk(chunkID, containerID, dataHash, request);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic, request);
    }

    @Test
    public void testAddChunkWithNullID() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = null;
        String dataHash = "SHA-256:deadbeef";

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic, request);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.addChunk(chunkID, containerID, dataHash, request);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic, request);
    }

    @Test
    public void testAddChunkWithBlankContainerID() throws Exception {

        String containerID = "";
        String chunkID = UUID.randomUUID().toString();
        String dataHash = "SHA-256:deadbeef";

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic, request);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.addChunk(chunkID, containerID, dataHash, request);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic, request);
    }

    @Test
    public void testAddChunkWithNullContainerID() throws Exception {

        String containerID = null;
        String chunkID = UUID.randomUUID().toString();
        String dataHash = "SHA-256:deadbeef";

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);
        HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic, request);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.addChunk(chunkID, containerID, dataHash, request);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic, request);
    }

    // -------- getChunk() --------

    @Test
    public void testGetChunk() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        ServletOutputStream dataStream = null;
        byte[] data = new byte[1024];
        new Random().nextBytes(data);

        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        ContainerConnectionInfo containerInfo = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.getContainer(containerID)).andReturn(containerInfo);
        EasyMock.expect(businessLogic.hasChunk(containerID, chunkID)).andReturn(true);
        Capture<OutputStream> outputStreamCapture = EasyMock.newCapture();
        businessLogic.getChunk(EasyMock.eq(containerID), EasyMock.eq(chunkID), EasyMock.capture(outputStreamCapture));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                OutputStream os = outputStreamCapture.getValue();
                os.write(data);
                return null;
            }
        });

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        ResponseEntity<StreamingResponseBody> response = controller.getChunk(chunkID, containerID);

        // Verify the expected Results
        assertNotNull(response);
        StreamingResponseBody responseBody = response.getBody();
        assertNotNull(responseBody);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        responseBody.writeTo(baos);
        byte[] fetchedData = baos.toByteArray();

        assertArrayEquals(data, fetchedData);

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }


    @Test
    public void testGetNonExistentChunk() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        ServletOutputStream dataStream = null;
        byte[] data = new byte[1024];
        new Random().nextBytes(data);

        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        ContainerConnectionInfo containerInfo = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.getContainer(containerID)).andReturn(containerInfo);
        EasyMock.expect(businessLogic.hasChunk(containerID, chunkID)).andReturn(false);

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.getChunk(chunkID, containerID);
            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testGetChunkFromNonExistentContainer() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();
        ServletOutputStream dataStream = null;
        byte[] data = new byte[1024];
        new Random().nextBytes(data);

        String rpcURL = "http://localhost:1234/";
        String p2pURL = "http://localhost:2345/";
        ContainerConnectionInfo containerInfo = new ContainerConnectionInfo(containerID, rpcURL, p2pURL);

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
            controller.getChunk(chunkID, containerID);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testGetChunkWithBlankID() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = "";

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);
        HttpServletResponse servResponse = EasyMock.createMock(HttpServletResponse.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic, servResponse);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.getChunk(chunkID, containerID);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic, servResponse);
    }

    @Test
    public void testGetChunkWithNullID() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = null;

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);
        HttpServletResponse servResponse = EasyMock.createMock(HttpServletResponse.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic, servResponse);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.getChunk(chunkID, containerID);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic, servResponse);
    }

    @Test
    public void testGetChunkWithBlankContainerID() throws Exception {

        String containerID = "";
        String chunkID = UUID.randomUUID().toString();

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);
        HttpServletResponse servResponse = EasyMock.createMock(HttpServletResponse.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic, servResponse);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.getChunk(chunkID, containerID);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic, servResponse);
    }

    @Test
    public void testGetChunkWithNullContainerID() throws Exception {

        String containerID = null;
        String chunkID = UUID.randomUUID().toString();

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);
        HttpServletResponse servResponse = EasyMock.createMock(HttpServletResponse.class);

        // Setup Expectations
        // -- None --

        // Replay Mock Objects
        EasyMock.replay(businessLogic, servResponse);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.getChunk(chunkID, containerID);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic, servResponse);
    }

    // -------- removeChunk() --------

    @Test
    public void testRemoveChunk() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        businessLogic.removeChunk(containerID, chunkID);
        EasyMock.expectLastCall();

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        controller.removeChunk(chunkID, containerID);

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testRemoveNonExistentChunk() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        businessLogic.removeChunk(containerID, chunkID);
        EasyMock.expectLastCall().andThrow(new NoSuchDataItemException());

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.removeChunk(chunkID, containerID);
            fail("Expected NoSuchDataItemException was not thrown");
        } catch (NoSuchDataItemException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testRemoveChunkFromNonExistentContainer() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        businessLogic.removeChunk(containerID, chunkID);
        EasyMock.expectLastCall().andThrow(new NoSuchContainerException());

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.removeChunk(chunkID, containerID);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testRemoveChunkWithBlankID() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = "";

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
            controller.removeChunk(chunkID, containerID);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testRemoveChunkWithNullID() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = null;

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
            controller.removeChunk(chunkID, containerID);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testRemoveChunkWithBlankContainerID() throws Exception {

        String containerID = "";
        String chunkID = UUID.randomUUID().toString();

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
            controller.removeChunk(chunkID, containerID);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testRemoveChunkWithNullContainerID() throws Exception {

        String containerID = null;
        String chunkID = UUID.randomUUID().toString();

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
            controller.removeChunk(chunkID, containerID);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    // -------- hasChunks() --------

    @Test
    public void testHasChunk() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.hasChunk(containerID, chunkID)).andReturn(true);

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        ResponseEntity<Void> response = controller.hasChunk(chunkID, containerID);

        // Verify the expected Results
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testHasNonExistentChunk() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = UUID.randomUUID().toString();

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        EasyMock.expect(businessLogic.hasChunk(containerID, chunkID)).andReturn(false);

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        ResponseEntity<Void> response = controller.hasChunk(chunkID, containerID);

        // Verify the expected Results
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testHasChunkWithBlankID() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = "";

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
            controller.hasChunk(chunkID, containerID);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testHasChunkWithNullID() throws Exception {

        String containerID = UUID.randomUUID().toString();
        String chunkID = null;

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
            controller.hasChunk(chunkID, containerID);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testHasChunkWithBlankContainerID() throws Exception {

        String containerID = "";
        String chunkID = UUID.randomUUID().toString();

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
            controller.hasChunk(chunkID, containerID);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testHasChunkWithNullContainerID() throws Exception {

        String containerID = null;
        String chunkID = UUID.randomUUID().toString();

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
            controller.hasChunk(chunkID, containerID);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    // -------- submitChallenge() --------

    @Test
    public void testSubmitChallenge() throws Exception {

        String containerID = UUID.randomUUID().toString();

        String chunkID1 = UUID.randomUUID().toString();
        String chunkID2 = UUID.randomUUID().toString();
        String chunkID3 = UUID.randomUUID().toString();

        List<ChallengeChunkInfo> chunks = new ArrayList<>();
        chunks.add(new ChallengeChunkInfo(chunkID1, 0, 100));
        chunks.add(new ChallengeChunkInfo(chunkID2, 100, 100));
        chunks.add(new ChallengeChunkInfo(chunkID3, 200, 100));

        Challenge challenge = new Challenge(containerID, chunks);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        businessLogic.submitChallenge(challenge);
        EasyMock.expectLastCall();

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        ResponseEntity<Void> response = controller.submitChallenge(challenge);

        // Verify the expected Results
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testSubmitChallengeForNonExistentContainer() throws Exception {

        String containerID = UUID.randomUUID().toString();

        String chunkID1 = UUID.randomUUID().toString();
        String chunkID2 = UUID.randomUUID().toString();
        String chunkID3 = UUID.randomUUID().toString();

        List<ChallengeChunkInfo> chunks = new ArrayList<>();
        chunks.add(new ChallengeChunkInfo(chunkID1, 0, 100));
        chunks.add(new ChallengeChunkInfo(chunkID2, 100, 100));
        chunks.add(new ChallengeChunkInfo(chunkID3, 200, 100));

        Challenge challenge = new Challenge(containerID, chunks);

        // Create the Mock Objects
        BusinessLogic businessLogic = EasyMock.createMock(BusinessLogic.class);

        // Setup Expectations
        businessLogic.submitChallenge(challenge);
        EasyMock.expectLastCall().andThrow(new NoSuchContainerException());

        // Replay Mock Objects
        EasyMock.replay(businessLogic);

        // Setup the Test Object
        APIController controller = new APIController();
        controller.setBusinessLogic(businessLogic);
        controller.initialize();

        // Execute the Test
        try {
            controller.submitChallenge(challenge);
            fail("Expected NoSuchContainerException was not thrown");
        } catch (NoSuchContainerException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testSubmitChallengeWithNullChallenge() throws Exception {

        Challenge challenge = null;

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
            controller.submitChallenge(challenge);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testSubmitChallengeWithBlankContainerID() throws Exception {

        String containerID = "";

        String chunkID1 = UUID.randomUUID().toString();
        String chunkID2 = UUID.randomUUID().toString();
        String chunkID3 = UUID.randomUUID().toString();

        List<ChallengeChunkInfo> chunks = new ArrayList<>();
        chunks.add(new ChallengeChunkInfo(chunkID1, 0, 100));
        chunks.add(new ChallengeChunkInfo(chunkID2, 100, 100));
        chunks.add(new ChallengeChunkInfo(chunkID3, 200, 100));

        Challenge challenge = new Challenge(containerID, chunks);

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
            controller.submitChallenge(challenge);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }

    @Test
    public void testSubmitChallengeWithNullContainerID() throws Exception {

        String containerID = null;

        String chunkID1 = UUID.randomUUID().toString();
        String chunkID2 = UUID.randomUUID().toString();
        String chunkID3 = UUID.randomUUID().toString();

        List<ChallengeChunkInfo> chunks = new ArrayList<>();
        chunks.add(new ChallengeChunkInfo(chunkID1, 0, 100));
        chunks.add(new ChallengeChunkInfo(chunkID2, 100, 100));
        chunks.add(new ChallengeChunkInfo(chunkID3, 200, 100));

        Challenge challenge = new Challenge(containerID, chunks);

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
            controller.submitChallenge(challenge);
            fail("Expected BadRequestException was not thrown");
        } catch (BadRequestException e) {
            // NOOP - Expected Exception
        }

        // Verify the expected Results
        // -- None --

        // Verify the Mock Objects
        EasyMock.verify(businessLogic);
    }


    // ======== Scrath Test Method - Delete ========
    // TODO Delete these test methods

}
