package io.topiacoin.node.micronetwork;

import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.FailedToCreateContainer;
import io.topiacoin.node.exceptions.InitializationException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchNodeException;
import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ContainerConnectionInfo;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.MicroNetworkInfo;
import io.topiacoin.node.model.MicroNetworkState;
import io.topiacoin.node.model.NodeConnectionInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class ContainerManager {

    private Log _log = LogFactory.getLog(this.getClass());

    @Autowired
    private DataModel _dataModel;

    @Autowired
    private MicroNetworkManager _microNetworkManager;

    private ExecutorService _executorService;

    @PostConstruct
    public void initialize() throws Exception {
        _log.info("Initializing Container Manager");
        if (_dataModel == null ||
                _microNetworkManager == null) {
            throw new InitializationException("Container Manager is missing required references");
        }
        _executorService = Executors.newSingleThreadExecutor();
        _log.info("Initialized Container Manager");
    }

    @PreDestroy
    public void shutdown() throws Exception {
        _log.info("Shutting Down Container Manager");
        _executorService.shutdown();
        _executorService = null;
        _log.info("Shutdown Container Manager");
    }

    public ContainerInfo getContainer(String containerID) {

        ContainerInfo containerInfo = null;

        containerInfo = _dataModel.getContainer(containerID);

        return containerInfo;
    }

    public boolean hasContainer(String containerID) {
        return (_dataModel.getContainer(containerID) != null);
    }

    public ContainerConnectionInfo getContainerConnectionInfo(String containerID) {
        ContainerConnectionInfo containerConnectionInfo = null;

        MicroNetworkInfo info = _microNetworkManager.getBlockchainInfo(containerID);
        if (info != null && info.getState() == MicroNetworkState.RUNNING) {
            containerConnectionInfo = new ContainerConnectionInfo(containerID, info.getRpcURL(), info.getP2pURL());
        }

        return containerConnectionInfo;
    }

    public ContainerState getContainerState(String containerID) {
        ContainerState containerState = ContainerState.UNKNOWN;

        MicroNetworkInfo info = _microNetworkManager.getBlockchainInfo(containerID);
        if (info != null) {
            switch (info.getState()) {
                case STARTING:
                    containerState = ContainerState.STARTING;
                    break;
                case RUNNING:
                    containerState = ContainerState.RUNNING;
                    break;
                case STOPPED:
                    containerState = ContainerState.STOPPED;
                    break;
                case STOPPING:
                    containerState = ContainerState.STOPPING;
                    break;
            }
        }
        return containerState;
    }

    public Future<ContainerConnectionInfo> createContainer(String containerID)
            throws ContainerAlreadyExistsException, NoSuchContainerException, FailedToCreateContainer {

        ContainerInfo containerInfo = _dataModel.getContainer(containerID);
        if (containerInfo == null) {
            throw new NoSuchContainerException("The specified container does not exist");
        }
        MicroNetworkInfo microNetworkInfo = _microNetworkManager.getBlockchainInfo(containerID);
        if (microNetworkInfo != null) {
            throw new ContainerAlreadyExistsException("The specified container already exists");
        }

        // Creation and Startup of the container are handled asynchronously.
        // The future is returned to the calling code.
        Future<ContainerConnectionInfo> future = _executorService.submit(() -> {

            _microNetworkManager.createBlockchain(containerID);
            _microNetworkManager.startBlockchain(containerID);

            MicroNetworkInfo info = _microNetworkManager.getBlockchainInfo(containerID);
            if (info.getState() != MicroNetworkState.RUNNING) {
                throw new FailedToCreateContainer("Failed to create the container");
            }

            ContainerConnectionInfo connectionInfo = new ContainerConnectionInfo(
                    info.getContainerID(),
                    info.getRpcURL(),
                    info.getP2pURL());

            return connectionInfo;
        });

        return future;
    }

    public Future<ContainerConnectionInfo> replicateContainer(String containerID, String peerID)
            throws NoSuchContainerException, ContainerAlreadyExistsException, NoSuchNodeException {

        if (_dataModel.getContainer(containerID) == null) {
            throw new NoSuchContainerException("The specified container does not exist");
        }
        if (_microNetworkManager.getBlockchainInfo(containerID) != null) {
            throw new ContainerAlreadyExistsException("The specified container already exists");
        }

        // Retrieve the Peer URL
        NodeConnectionInfo nodeInfo = _dataModel.getNodeConnectionInfo(peerID) ;
        if ( nodeInfo == null ) {
            throw new NoSuchNodeException("The specified peer node cannot be found");
        }
        String peerURL = nodeInfo.getNodeURL();

        // TODO - Must contact the Node and retrieve the P2PUrl from it

        Future<MicroNetworkInfo> syncFuture = _microNetworkManager.syncBlockchain(peerURL, containerID);

        Future<ContainerConnectionInfo> future = _executorService.submit(() -> {
            try {
                ContainerConnectionInfo containerConnectionInfo = null;
                MicroNetworkInfo microNetworkInfo = syncFuture.get();
                containerConnectionInfo = new ContainerConnectionInfo(microNetworkInfo.getContainerID(), microNetworkInfo.getRpcURL(), microNetworkInfo.getP2pURL());
                return containerConnectionInfo;
            } catch ( ExecutionException e) {
                throw (Exception)e.getCause();
            }
        });

        return future;
    }

    public Future<Void> removeContainer(String containerID)
            throws NoSuchContainerException {

        if (_dataModel.getContainer(containerID) == null) {
            throw new NoSuchContainerException("The specified container does not exist");
        }
        if (_microNetworkManager.getBlockchainInfo(containerID) == null) {
            throw new NoSuchContainerException("The specified container is not hosted on this node");
        }

        Future<Void> future = _executorService.submit(() -> {
            _microNetworkManager.destroyBlockchain(containerID);
            return null;
        });

        return future;
    }

    // -------- Accessor Methods --------


    public void setDataModel(DataModel dataModel) {
        _dataModel = dataModel;
    }

    public void setMicroNetworkManager(MicroNetworkManager microNetworkManager) {
        _microNetworkManager = microNetworkManager;
    }

    public void saveChallenge(Challenge challenge) throws NoSuchContainerException {
        ContainerInfo containerInfo = _dataModel.getContainer(challenge.getContainerID());
        if ( containerInfo == null) {
            throw new NoSuchContainerException("The container this challenge belongs to does not exist");
        }
        containerInfo.setChallenge(challenge);
        _dataModel.updateContainer(containerInfo);
    }

    // ======== Container State Enum ========

    public static enum ContainerState {
        UNKNOWN,
        STARTING,
        RUNNING,
        STOPPING,
        STOPPED
    }
}
