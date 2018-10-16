package io.topiacoin.node;

import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.CorruptDataItemException;
import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.FailedToCreateContainer;
import io.topiacoin.node.exceptions.FailedToRemoveContainer;
import io.topiacoin.node.exceptions.InitializationException;
import io.topiacoin.node.exceptions.InvalidChallengeException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.exceptions.NoSuchNodeException;
import io.topiacoin.node.micronetwork.ContainerManager;
import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ChallengeSolution;
import io.topiacoin.node.model.ContainerConnectionInfo;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataItemInfo;
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

import static io.topiacoin.node.micronetwork.ContainerManager.ContainerState.*;

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
    private ContainerManager _containerManager;

    // -------- Lifecycle Methods --------

    @PostConstruct
    public void initialize() {
        _log.info("Initializing Business Logic");

        if (_dataStorageManager == null ||
                _smscManager == null ||
                _proofSolver == null ||
                _containerManager == null) {
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

        ContainerConnectionInfo containerConnectionInfo = _containerManager.getContainerConnectionInfo(containerID);

        if (containerConnectionInfo == null) {
            throw new NoSuchContainerException("The specified container is not hosted on this node");
        }

        return containerConnectionInfo;
    }

    public ContainerConnectionInfo createContainer(String containerID)
            throws ContainerAlreadyExistsException, NoSuchContainerException, FailedToCreateContainer {

        ContainerConnectionInfo containerConnectionInfo = null;
        ContainerInfo containerInfo = null;

        // Fetch the Container Info for the requested container from the Data Model
        if (!_containerManager.hasContainer(containerID)) {
            Future<ContainerInfo> future = _smscManager.getContainerInfo(containerID);

            try {
                containerInfo = future.get();
            } catch (InterruptedException e) {
                throw new NoSuchContainerException("Failed to fetch the container info from the smart contract", e);
            } catch (ExecutionException e) {
                throw new NoSuchContainerException("Failed to fetch the container info from the smart contract", e);
            }

            if (containerInfo == null) {
                throw new NoSuchContainerException("The specified container does not exist");
            }
        }

        ContainerManager.ContainerState containerState = _containerManager.getContainerState(containerID);
        if (containerState == RUNNING) {
            throw new ContainerAlreadyExistsException("The specified container is already running");
        }

        Future<ContainerConnectionInfo> createFuture = _containerManager.createContainer(containerID);

        try {
            containerConnectionInfo = createFuture.get();
        } catch (InterruptedException e) {
            throw new FailedToCreateContainer("Failed to create the specified container", e);
        } catch (ExecutionException e) {
            throw new FailedToCreateContainer("Failed to create the specified container", e);
        }

        return containerConnectionInfo;
    }

    public ContainerConnectionInfo replicateContainer(String containerID, String peerNodeID)
            throws NoSuchContainerException, ContainerAlreadyExistsException, NoSuchNodeException, FailedToCreateContainer {

        ContainerConnectionInfo containerConnectionInfo = null;
        ContainerInfo containerInfo = null;

        // Fetch the Container Info for the requested container from the Data Model
        if (!_containerManager.hasContainer(containerID)) {
            Future<ContainerInfo> future = _smscManager.getContainerInfo(containerID);

            try {
                containerInfo = future.get();
            } catch (InterruptedException e) {
                throw new NoSuchContainerException("Failed to fetch the container info from the smart contract", e);
            } catch (ExecutionException e) {
                throw new NoSuchContainerException("Failed to fetch the container info from the smart contract", e);
            }

            if (containerInfo == null) {
                throw new NoSuchContainerException("The specified container does not exist");
            }
        }

        ContainerManager.ContainerState containerState = _containerManager.getContainerState(containerID);
        if (containerState == RUNNING) {
            throw new ContainerAlreadyExistsException("The specified container is already running");
        }

        Future<ContainerConnectionInfo> createFuture = _containerManager.replicateContainer(containerID, peerNodeID);

        try {
            containerConnectionInfo = createFuture.get();
        } catch (InterruptedException e) {
            throw new FailedToCreateContainer("Failed to create the specified container", e);
        } catch (ExecutionException e) {
            throw new FailedToCreateContainer("Failed to create the specified container", e);
        }

        return containerConnectionInfo;
    }

    public void removeContainer(String containerID)
            throws NoSuchContainerException, FailedToRemoveContainer {

        if (!_containerManager.hasContainer(containerID)) {
            throw new NoSuchContainerException("The specified container does not exist.");
        }

        Future<Void> removeFuture = _containerManager.removeContainer(containerID);

        try {
            removeFuture.get();
        } catch (InterruptedException e) {
            throw new FailedToRemoveContainer("Failed to remove the specified container");
        } catch (ExecutionException e) {
            throw new FailedToRemoveContainer("Failed to remove the specified container");
        }
    }

    public void storeChunk(String containerID, String chunkID, String dataHash, InputStream dataStream)
            throws NoSuchContainerException, DataItemAlreadyExistsException, IOException, CorruptDataItemException {

        DataItemInfo dataItemInfo = null;

        // Check if this node is hosting the specified container
        if (!_containerManager.hasContainer(containerID)) {
            throw new NoSuchContainerException("This node is not hosting the specified container(" + containerID + ")");
        }

        if (_containerManager.getContainerState(containerID) != RUNNING) {
            throw new NoSuchContainerException("This node is not hosting the specified container(" + containerID + ")");
        }

        // Check to see if we already have this chunk for the specified container
        if (_dataStorageManager.hasData(containerID, chunkID)) {
            throw new DataItemAlreadyExistsException("The specified Data item already exists");
        }

        // Add the chunk to the Data Storage Manager
        long size = _dataStorageManager.saveData(containerID, chunkID, dataHash, dataStream);
    }

    public boolean hasChunk(String containerID, String chunkID)
            throws NoSuchContainerException {

        boolean found = true;

        // Check if this node is hosting the specified container
        if (!_containerManager.hasContainer(containerID)) {
            throw new NoSuchContainerException("This node is not hosting the specified container(" + containerID + ")");
        }

        if (_containerManager.getContainerState(containerID) != RUNNING) {
            throw new NoSuchContainerException("This node is not hosting the specified container(" + containerID + ")");
        }

        // Check the Data Storage Manger to see if we have this chunk.
        if (found) {
            try {
                found &= _dataStorageManager.hasData(containerID, chunkID);
            } catch (IOException e) {
                found = false;
            }
        }

        return found;
    }

    public void getChunk(String containerID, String chunkID, OutputStream dataStream)
            throws NoSuchDataItemException, CorruptDataItemException, NoSuchContainerException {

        try {
            // Check if this node is hosting the specified container
            if (!_containerManager.hasContainer(containerID)) {
                throw new NoSuchContainerException("This node is not hosting the specified container(" + containerID + ")");
            }

            if (_containerManager.getContainerState(containerID) != RUNNING) {
                throw new NoSuchContainerException("This node is not hosting the specified container(" + containerID + ")");
            }

            // Check the Data Storage Manger to see if we have this chunk.
            if (!_dataStorageManager.hasData(containerID, chunkID)) {
                throw new NoSuchDataItemException("The Requested Chunk is not available");
            }
            // Retrieve the chunk and write it to the provided output stream.
            _dataStorageManager.fetchData(containerID, chunkID, dataStream);
        } catch (IOException e) {
            _log.warn("IOException getting chunk " + chunkID, e);
        }
    }

    public void removeChunk(String containerID, String chunkID)
            throws NoSuchContainerException, NoSuchDataItemException {

        try {
            // Check if this node is hosting the specified container
            if (!_containerManager.hasContainer(containerID)) {
                throw new NoSuchContainerException("This node is not hosting the specified container(" + containerID + ")");
            }

            if (_containerManager.getContainerState(containerID) != RUNNING) {
                throw new NoSuchContainerException("This node is not hosting the specified container(" + containerID + ")");
            }

            if (!_dataStorageManager.removeData(containerID, chunkID)) {
                throw new NoSuchDataItemException("The requested chunk does not exist");
            }
        } catch (IOException e) {
            _log.warn("IOException removing chunk " + chunkID, e);
        }
    }


    public void submitChallenge(Challenge challenge)
            throws NoSuchContainerException, InvalidChallengeException {

        // Verify that we are actually hosting the container this challenege is for
        // Check if this node is hosting the specified container
        String containerID = challenge.getContainerID();
        if (!_containerManager.hasContainer(containerID)) {
            throw new NoSuchContainerException("This node is not hosting the specified container(" + containerID + ")");
        }

        if (_containerManager.getContainerState(containerID) != RUNNING) {
            throw new NoSuchContainerException("This node is not hosting the specified container(" + containerID + ")");
        }


        // Verify that we can successfully solve the provided challenge
        ChallengeSolution solution = _proofSolver.generateSolution(challenge);
        if (solution == null) {
            throw new InvalidChallengeException();
        }

        // Store the Challenge in the data model
        _containerManager.saveChallenge(challenge);

        // Submit the solution to this challenge to the SMSC
        Future<?> solutionFuture = _smscManager.submitProofSolution(containerID, solution);

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

    public void setContainerManager(ContainerManager containerManager) {
        _containerManager = containerManager;
    }
}
