package io.topiacoin.node;

import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.CorruptDataItemException;
import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.InitializationException;
import io.topiacoin.node.exceptions.InvalidChallengeException;
import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.exceptions.NoSuchNodeException;
import io.topiacoin.node.micronetwork.MicroNetworkManager;
import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ChallengeSolution;
import io.topiacoin.node.model.ContainerConnectionInfo;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataItemInfo;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.MicroNetworkInfo;
import io.topiacoin.node.model.NodeConnectionInfo;
import io.topiacoin.node.proof.ProofSolver;
import io.topiacoin.node.smsc.SMSCManager;
import io.topiacoin.node.storage.DataStorageManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
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

    public ContainerConnectionInfo getContainer(String containerID)
            throws NoSuchContainerException {

        ContainerConnectionInfo containerConnectionInfo = null;
        ContainerInfo containerInfo = null;
        MicroNetworkInfo microNetworkInfo = null;

        // Fetch the Container Info for the requested container from the Data Model
        containerInfo = _dataModel.getContainer(containerID);
        if (containerInfo == null) {
            throw new NoSuchContainerException("The requested container (" + containerID + ") does not exist");
        }
        microNetworkInfo = _microNetworkManager.getBlockchainInfo(containerID);
        if (microNetworkInfo == null) {
            throw new NoSuchContainerException("The requested container (" + containerID + ") is not hosted on this node.");
        }

        containerConnectionInfo = new ContainerConnectionInfo(containerID, microNetworkInfo.getRpcURL(), microNetworkInfo.getP2pURL());

        return containerConnectionInfo;
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

    public ContainerInfo replicateContainer(String containerID, String peerNodeID) throws NoSuchContainerException, MicroNetworkAlreadyExistsException, NoSuchNodeException {

        ContainerInfo containerInfo = null;

        try {
            // TODO - Check to see if this container ID is assigned to this node.
            containerInfo = _dataModel.getContainer(containerID);
            if (containerInfo == null) {
                Future<ContainerInfo> containerInfoFuture = _smscManager.getContainerInfo(containerID);
                ;
                containerInfo = containerInfoFuture.get();

                if (containerInfo != null) {
                    try {
                        _dataModel.createContainer(containerInfo.getId(), containerInfo.getExpirationDate(), containerInfo.getChallenge());
                    } catch (ContainerAlreadyExistsException e) {
                        // NOOP - This might occur if an auto-sync has added it to the model while we were independently fetching it.
                    }
                }
            }

            if (containerInfo == null) {
                throw new NoSuchContainerException("The specified container (" + containerID + ") does not exit");
            }
            // Check to see if we are already hosting a MicroNetwork for this container ID
            MicroNetworkInfo microNetworkInfo = _microNetworkManager.getBlockchainInfo(containerID);
            if (microNetworkInfo != null) {
                throw new MicroNetworkAlreadyExistsException("The specified container is already synced on this node");
            }

            // Check to see if the the peerNode is assigned to this container ID.
            List<NodeConnectionInfo> nodesConnectionList = null;
            Future<List<NodeConnectionInfo>> nodeInfoFuture = _smscManager.getNodesForContainer(containerID);
            nodesConnectionList = nodeInfoFuture.get();

            String p2pURL = null;
            for (NodeConnectionInfo nodeConnectionInfo : nodesConnectionList) {
                if (nodeConnectionInfo.getNodeID().equalsIgnoreCase(peerNodeID)) {
                    p2pURL = nodeConnectionInfo.getP2PURL();
                    break;
                }
            }

            if (p2pURL == null) {
                throw new NoSuchNodeException("The specified Peer Node (" + peerNodeID + ") does not exist for the specified container (" + containerID + ")");
            }

            // Replicate the MicroNetwork for the container from the peer Node.
            Future syncFuture = _microNetworkManager.syncBlockchain(p2pURL, containerID);
            syncFuture.get();

            microNetworkInfo = _microNetworkManager.getBlockchainInfo(containerID);
        } catch (InterruptedException e) {
            _log.info("Container Syncing was interrupted");
        } catch (ExecutionException e) {
            _log.info("An Exception occurred syncing a container", e);
        }

        return containerInfo;
    }

    public void removeContainer(String containerID)
            throws NoSuchContainerException {

        ContainerInfo containerInfo = _dataModel.getContainer(containerID);
        if ( containerInfo == null ) {
            throw new NoSuchContainerException();
        }
        MicroNetworkInfo microNetworkInfo = _microNetworkManager.getBlockchainInfo(containerID);
        if ( microNetworkInfo != null ) {
            _microNetworkManager.destroyBlockchain(microNetworkInfo.getId());
        }
    }

    public void storeChunk(String containerID, String chunkID, String dataHash, InputStream dataStream)
            throws NoSuchContainerException, DataItemAlreadyExistsException, IOException, CorruptDataItemException {

        DataItemInfo dataItemInfo = null;

        // Check if this node is hosting the specified container
        MicroNetworkInfo microNetworkInfo = _microNetworkManager.getBlockchainInfo(containerID);
        if (microNetworkInfo == null) {
            throw new NoSuchContainerException("This node is not hosting the specified container(" + containerID + ")");
        }

        // Check to see if we already have this chunk for the specified container
        if (_dataStorageManager.hasData(chunkID)) {
            throw new DataItemAlreadyExistsException("The specified Data item already exists");
        }

        // Add the chunk to the Data Storage Manager
        long size = _dataStorageManager.saveData(chunkID, dataHash, dataStream);

        // Add the chunk to the Data Model
        _dataModel.createDataItem(chunkID, size, dataHash);
    }

    public boolean hasChunk(String containerID, String chunkID)
            throws NoSuchContainerException {

        boolean found = true ;

        // Check the Data Model to see if we have this chunk listed for this container ID
        try {
            DataItemInfo chunkInfo = _dataModel.getDataItem(chunkID);
            found &= (chunkInfo != null);
        } catch ( NoSuchDataItemException e) {
            found = false ;
        }

        if ( found ) {
            found &= _dataModel.isDataItemInContainer(chunkID, containerID);
        }

        // Check the Data Storage Manger to see if we have this chunk.
        if ( found ) {
            try {
                found &= _dataStorageManager.hasData(chunkID);
            } catch (IOException e) {
                found = false ;
            }
        }

        return found;
    }

    public void getChunk(String containerID, String chunkID, OutputStream dataStream)
            throws NoSuchDataItemException, CorruptDataItemException, NoSuchContainerException {

        try {
            // Check the Data Model to see if we have this chunk listed for this container ID
            DataItemInfo chunkInfo = _dataModel.getDataItem(chunkID);
            if (chunkInfo == null) {
                throw new NoSuchDataItemException("The requested Chunk does not exist");
            }
            if ( !_dataModel.isDataItemInContainer(chunkID, containerID)) {
                throw new NoSuchDataItemException("The requested Chunk does not exist");
            }
            // Check the Data Storage Manger to see if we have this chunk.
            if (!_dataStorageManager.hasData(chunkID)) {
                throw new NoSuchDataItemException("The Requested Chunk is not available");
            }
            // Retrieve the chunk and write it to the provided output stream.
            _dataStorageManager.fetchData(chunkInfo.getId(), chunkInfo.getDataHash(), dataStream);
        } catch (IOException e) {
            _log.warn("IOException getting chunk " + chunkID, e);
        }
    }

    public void removeChunk(String containerID, String chunkID)
        throws NoSuchContainerException, NoSuchDataItemException {

        try {
            DataItemInfo chunkInfo = _dataModel.getDataItem(chunkID);
            if (chunkInfo == null) {
                throw new NoSuchDataItemException("The requested Chunk does not exist");
            }
            if (!_dataModel.isDataItemInContainer(chunkID, containerID)) {
                throw new NoSuchDataItemException("The requested Chunk does not exist");
            }

            if ( _dataModel.removeDataItemFromContainer(chunkID, containerID)) {
                _dataStorageManager.removeData(chunkID);
            }
        } catch (IOException e) {
            _log.warn("IOException removing chunk " + chunkID, e);
        }
    }


    public void submitChallenge(Challenge challenge) throws NoSuchContainerException, InvalidChallengeException {

        // Verify that we are actually hosing the container this challenege is for
        ContainerInfo containerInfo = _dataModel.getContainer(challenge.getContainerID());

        // Verify that we can successfully solve the provided challenge
        ChallengeSolution solution = _proofSolver.generateSolution(challenge);
        if (solution == null) {
            throw new InvalidChallengeException();
        }

        // Store the Challenge in the data model
        containerInfo.setChallenge(challenge);
        _dataModel.updateContainer(containerInfo);

        // Submit the solution to this challenge to the SMSC
        Future<?> solutionFuture = _smscManager.submitProofSolution(challenge.getContainerID(), solution);

        try {
            solutionFuture.get();
        } catch (InterruptedException e) {
            _log.info("Solution Submission was interrupted", e);
        } catch (ExecutionException e) {
            _log.info("Exception submitting solution", e);
            // TODO - Schedule the Periodic Proof Executor to rerun the proof in the near future.
        }
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
