package io.topiacoin.node;

import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.micronetwork.MicroNetworkManager;
import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.proof.ProofSolver;
import io.topiacoin.node.smsc.SMSCManager;
import io.topiacoin.node.storage.DataStorageManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.io.OutputStream;

@Component
public class BusinessLogic {

    private Log _log = LogFactory.getLog(this.getClass());

    @Autowired
    private DataStorageManager _dataStorageManager;

    @Autowired
    private SMSCManager _smscManager;

    @Autowired
    private ProofSolver _proofSolver;

    @Autowired
    private MicroNetworkManager _microNetworkManager;

    @Autowired
    private DataModel _dataModel;

    // -------- Lifecycle Methods --------

    @PostConstruct
    public void initialize() {
        _log.info("Initializing Business Logic");
        _log.info("Initialized Business Logic");
    }

    @PreDestroy
    public void shutdown() {
        _log.info("Shutting Down Business Logic");
        _log.info("Shut Down Business Logic");
    }

    // -------- Business Logic Methods --------

    public ContainerInfo getContainer(String containerID)
            throws NoSuchContainerException {

        ContainerInfo containerInfo = null;

        // Fetch the Container Info for the requested container from the Data Model
        containerInfo = _dataModel.getContainer(containerID);

        return containerInfo;
    }

    public ContainerInfo createContainer(String containerID)
            throws ContainerAlreadyExistsException {

        ContainerInfo containerInfo = null;

        // TODO - Check to see if this container ID is assigned to this node.
        // Check to see if we are already hosting a micronetwork for this container ID
        // Create the new Micronetwork for the container

        return containerInfo;
    }

    public ContainerInfo replicateContainer(String containerID, String peerNodeID) {

        ContainerInfo containerInfo = null;

        // TODO - Check to see if this container ID is assigned to this node.
        // Check to see if the the peerNode is assigned to this container ID.
        // Check to see if we are already hosting a MicroNetwork for this container ID
        // Replicate the MicroNetwork for the container from the peer Node.

        return containerInfo;

    }

    public void addChunk(String containerID, String chunkID, String dataHash, InputStream dataStream) {

        // Check to see if we already have this chunk for the specified container
        // Add the chunk to the Data Storage Manager
        // Add the chunk to the Data Model
    }

    public boolean hasChunk(String containerID, String chunkID) {

        // Check the Data Model to see if we have this chunk listed for this container ID
        // Check the Data Storage Manger to see if we have this chunk.

        return false;
    }

    public void getChunk(String containerID, String chunkID, OutputStream dataStream) {

        // Check the Data Model to see if we have this chunk listed for this container ID
        // Check the Data Storage Manger to see if we have this chunk.
        // Retrieve the chunk and write it to the provided output stream.

    }

    public void submitChallenge(Challenge challenge) {

        // Verify that we can successfully solve the provided challenge
        // Store the Challenge in the data model
        // Schedule submission of a solution to this challenge
    }


    // -------- Accessor Methods --------


    public void setDataStorageManager(DataStorageManager dataStorageManager) {
        _dataStorageManager = dataStorageManager;
    }

    public void setSmscManager(SMSCManager smscManager) {
        _smscManager = smscManager;
    }

    public void setProofSolver(ProofSolver proofSolver) {
        _proofSolver = proofSolver;
    }

    public void setMicroNetworkManager(MicroNetworkManager microNetworkManager) {
        _microNetworkManager = microNetworkManager;
    }

    public void setDataModel(DataModel dataModel) {
        _dataModel = dataModel;
    }
}
