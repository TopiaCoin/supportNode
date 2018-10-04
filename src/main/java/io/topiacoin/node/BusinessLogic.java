package io.topiacoin.node;

import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.CorruptDataItemException;
import io.topiacoin.node.exceptions.InitializationException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.micronetwork.MicroNetworkManager;
import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataItemInfo;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.MicroNetworkInfo;
import io.topiacoin.node.proof.ProofSolver;
import io.topiacoin.node.smsc.SMSCManager;
import io.topiacoin.node.storage.DataStorageManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

        if (_dataStorageManager == null ||
                _dataModel == null ||
                _smscManager == null ||
                _proofSolver == null ||
                _microNetworkManager == null) {
            throw new InitializationException("Business Logic is missing required components");
        }
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
            throws ContainerAlreadyExistsException, NoSuchContainerException {

        ContainerInfo containerInfo = null;

        // Check the Data Model to see if this container ID is assigned to the this node.
        containerInfo = _dataModel.getContainer(containerID);
        if (containerInfo == null) {
            // If no info in Data Model, check the SMSC to see if this container ID is assigned to this node.
            try {
                Future<ContainerInfo> containerInfoFuture = _smscManager.getContainerInfo(containerID);
                containerInfo = containerInfoFuture.get();

                if (containerInfo != null) {
                    // Save the Container Info to the Data Model
                    containerInfo = _dataModel.createContainer(containerInfo.getId(), containerInfo.getExpirationDate(), containerInfo.getChallenge());
                }
            } catch (InterruptedException e) {
                _log.info("Interrupted while getting container info from the SMSC", e);
                // If we have been interrupted, then assume the call to the SMSC failed and proceed
                // with the null containerInfo.
            } catch (ExecutionException e) {
                _log.info("Exception getting container info from the SMSC", e);
                // The retrieval of info from the SMSC has failed.
                // Proceed with the null containerInfo and let the rest of the code react accordingly.
            }
        }
        if (containerInfo == null) {
            throw new NoSuchContainerException("The specified container ID is not valid.");
        }
        // If we should be hosting this container, check the MNM to see if we are already hosting a micronetwork for this container ID
        MicroNetworkInfo microNetworkInfo = _microNetworkManager.getBlockchainInfo(containerID);
        if (microNetworkInfo != null) {
            throw new ContainerAlreadyExistsException("The specified container has already been created");
        }
        // If we are not yet hosting the continer, create the new Micronetwork for the container
        _microNetworkManager.createBlockchain(containerID);
        microNetworkInfo = _microNetworkManager.getBlockchainInfo(containerID);

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

    public void getChunk(String containerID, String chunkID, OutputStream dataStream)
            throws NoSuchDataItemException, CorruptDataItemException {

        try {
            // Check the Data Model to see if we have this chunk listed for this container ID
            DataItemInfo chunkInfo = _dataModel.getDataItem(chunkID);
            if (chunkInfo == null || !containerID.equalsIgnoreCase(chunkInfo.getContainerID())) {
                throw new NoSuchDataItemException("The requested Chunk does not exist");
            }
            // Check the Data Storage Manger to see if we have this chunk.
            if (!_dataStorageManager.hasData(chunkID, containerID)) {
                throw new NoSuchDataItemException("The Requested Chunk is not available");
            }
            // Retrieve the chunk and write it to the provided output stream.
            _dataStorageManager.fetchData(chunkInfo.getId(), chunkInfo.getContainerID(), chunkInfo.getDataHash(), dataStream);
        } catch ( IOException e ) {
            _log.warn ( "IOException getting chunk " + chunkID, e ) ;
        }
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
