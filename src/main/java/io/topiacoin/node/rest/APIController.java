package io.topiacoin.node.rest;

import io.topiacoin.node.BusinessLogic;
import io.topiacoin.node.exceptions.BadRequestException;
import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.CorruptDataItemException;
import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.InitializationException;
import io.topiacoin.node.exceptions.InvalidChallengeException;
import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.exceptions.NoSuchNodeException;
import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ContainerConnectionInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RestController
public class APIController {

    private Log _log = LogFactory.getLog(this.getClass());

    @Autowired
    private BusinessLogic _businessLogic;

    // -------- Lifecycle Methods --------

    @PostConstruct
    public void initialize() {
        _log.info("Initializing Master Controller");

        if (_businessLogic == null) {
            throw new InitializationException("Business Logic Reference Not Provided");
        }

        _log.info("Initialized Master Controller");
    }

    @PreDestroy
    public void shutdown() {
        _log.info("Shutting Down Master Controller");
        _log.info("Shut Down Master Controller");
    }

    // -------- Container Methods --------

    @RequestMapping(value = "/container", method = RequestMethod.GET)
    public ResponseEntity<ContainerConnectionInfo> getContainer(
            @RequestParam("containerID") String containerID)
            throws NoSuchContainerException {

        if (TextUtils.isBlank(containerID)) {
            throw new BadRequestException("ContainerID not specified.");
        }

        // This will throw if the container doesn't exist.
        ContainerConnectionInfo container = _businessLogic.getContainer(containerID);

        return new ResponseEntity<>(container, HttpStatus.OK);
    }

    @RequestMapping(value = "/container", method = RequestMethod.POST)
    public ResponseEntity<Void> createContainer(
            @RequestBody ContainerCreationRequest creationRequest)
            throws ContainerAlreadyExistsException {

        if (creationRequest == null) {
            throw new BadRequestException("Invalid Body.");
        }

        _log.info("Creation request: " + creationRequest);

        if (TextUtils.isBlank(creationRequest.getContainerID())) {
            throw new BadRequestException("ContainerID not specified.");
        }

        try {
            _businessLogic.createContainer(creationRequest.getContainerID());
        } catch (NoSuchContainerException e) {
            throw new BadRequestException("The container ID (" + creationRequest.getContainerID() + ") is not valid.");
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/container", method = RequestMethod.PUT)
    public ResponseEntity<Void> replicateContainer(
            @RequestBody ContainerReplicationRequest replicationRequest)
            throws ContainerAlreadyExistsException, NoSuchNodeException, NoSuchContainerException {

        if (replicationRequest == null) {
            throw new BadRequestException("Invalid Body.");
        }

        _log.info("Replication request: " + replicationRequest);

        if (TextUtils.isBlank(replicationRequest.getContainerID())) {
            throw new BadRequestException("ContainerID not specified.");
        }
        if (TextUtils.isBlank(replicationRequest.getPeerNodeID())) {
            throw new BadRequestException("Peer Node ID not specified.");
        }

        try {
            _businessLogic.replicateContainer(replicationRequest.getContainerID(), replicationRequest.getPeerNodeID());
        } catch (MicroNetworkAlreadyExistsException e) {
            throw new ContainerAlreadyExistsException(e);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/container", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeContainer(
            @RequestParam String containerID)
            throws NoSuchContainerException {

        _log.info("Remove Container: " + containerID);

        if (TextUtils.isBlank(containerID)) {
            throw new BadRequestException("ContainerID not specified.");
        }

        _businessLogic.removeContainer(containerID);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // -------- Chunk Methods --------

    @RequestMapping(value = "/chunk", method = RequestMethod.POST)
    public ResponseEntity<Void> addChunk(
            @RequestParam("chunkID") String chunkID,
            @RequestParam("containerID") String containerID,
            @RequestParam("dataHash") String dataHash,
            HttpServletRequest request)
            throws IOException, DataItemAlreadyExistsException, CorruptDataItemException, NoSuchContainerException {

        if (TextUtils.isBlank(chunkID)) {
            throw new BadRequestException("ChunkID not specified.");
        }

        if (TextUtils.isBlank(containerID)) {
            throw new BadRequestException("ContainerID not specified.");
        }

        if (TextUtils.isBlank(dataHash)) {
            throw new BadRequestException("Data Hash not specified.");
        }

        _log.info("Adding Chunk " + chunkID);

        InputStream dataStream = request.getInputStream();

        _businessLogic.storeChunk(containerID, chunkID, dataHash, dataStream);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/chunk", method = RequestMethod.HEAD)
    public ResponseEntity<Void> hasChunk(
            @RequestParam("chunkID") String chunkID,
            @RequestParam("containerID") String containerID)
            throws NoSuchContainerException {

        if (TextUtils.isBlank(chunkID)) {
            throw new BadRequestException("ChunkID not specified.");
        }
        if (TextUtils.isBlank(containerID)) {
            throw new BadRequestException("ContainerID not specified.");
        }

        _log.info("Checking on Chunk " + chunkID);

        if (_businessLogic.hasChunk(containerID, chunkID)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/chunk", method = RequestMethod.GET)
    public ResponseEntity<StreamingResponseBody> getChunk(
            @RequestParam("chunkID") String chunkID,
            @RequestParam("containerID") String containerID)
            throws IOException, CorruptDataItemException, NoSuchDataItemException, NoSuchContainerException {

        if (TextUtils.isBlank(chunkID)) {
            throw new BadRequestException("ChunkID not specified.");
        }
        if (TextUtils.isBlank(containerID)) {
            throw new BadRequestException("ContainerID not specified.");
        }

        _log.info("Getting Chunk " + chunkID);

        if (_businessLogic.getContainer(containerID) == null) {
            throw new NoSuchContainerException("The specified container does not exist");
        }
        if (!_businessLogic.hasChunk(containerID, chunkID)) {
            throw new NoSuchDataItemException("The specified chunk does not exist");
        }

        StreamingResponseBody streamingResponseBody = new StreamingResponseBody() {
            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                try {
                    _businessLogic.getChunk(containerID, chunkID, outputStream);
                } catch (NoSuchDataItemException e) {
                    throw new IOException();
                } catch (CorruptDataItemException e) {
                    throw new IOException();
                } catch (NoSuchContainerException e) {
                    throw new IOException();
                }
            }
        };
        return new ResponseEntity<>(streamingResponseBody, HttpStatus.OK);
    }

    @RequestMapping(value = "/chunk", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeChunk(
            @RequestParam("chunkID") String chunkID,
            @RequestParam("containerID") String containerID)
            throws NoSuchDataItemException, NoSuchContainerException {

        if (TextUtils.isBlank(chunkID)) {
            throw new BadRequestException("ChunkID not specified.");
        }
        if (TextUtils.isBlank(containerID)) {
            throw new BadRequestException("ContainerID not specified.");
        }

        _log.info("Removing Chunk " + chunkID);

        _businessLogic.removeChunk(containerID, chunkID);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // -------- Challenge Methods --------

    @RequestMapping(value = "/challenge", method = RequestMethod.POST)
    public ResponseEntity<Void> submitChallenge(
            @RequestBody Challenge challenge)
            throws NoSuchContainerException, InvalidChallengeException {

        if (challenge == null) {
            throw new BadRequestException("Challenge not specified.");
        }
        if (TextUtils.isBlank(challenge.getContainerID())) {
            throw new BadRequestException("Container ID not specified.");
        }

        _log.info("Received Challenge: " + challenge);

        _businessLogic.submitChallenge(challenge);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // -------- Accessor Methods --------

    public void setBusinessLogic(BusinessLogic businessLogic) {
        _businessLogic = businessLogic;
    }
}
