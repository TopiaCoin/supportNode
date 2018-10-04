package io.topiacoin.node.smsc;

import io.topiacoin.eosrpcadapter.EOSRPCAdapter;
import io.topiacoin.eosrpcadapter.messages.TableRows;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NotRegisteredException;
import io.topiacoin.node.model.ChallengeSolution;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.Dispute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class EOSSMSCManager implements SMSCManager {

    private Log _log = LogFactory.getLog(this.getClass());
    private String _stakingAccount;
    private String _signingAccount;

    @Autowired
    private String contract;

    private EOSRPCAdapter _eosRPCAdapter;

    private ExecutorService _executorService;

    private String nodeID;

    // -------- Lifecycle Methods --------

    @PostConstruct
    public void initialize() {
        _log.info("Initializing SMSC Manager");

        _executorService = Executors.newSingleThreadExecutor();

        _log.info("Initialized SMSC Manager");
    }

    @PreDestroy
    public void shutdown() {
        _log.info("Shutting Down SMSC Manager");

        _executorService.shutdown();

        _log.info("Shut Down SMSC Manager");
    }

    // -------- Public Methods --------

    /**
     * Submits the solution to a Proof of Hosting/Replication to the SMSC for validation and payment.
     *
     * @param containerID The ID of the container to which the solution belongs.
     * @param solution    The solution to the replication challenge for the specified container.
     *
     * @return A Future that can be used to wait for the completion of the proof submisison.
     */
    @Override
    public Future<?> submitProofSolution(String containerID, ChallengeSolution solution)
            throws NotRegisteredException {
        return null;
    }

    /**
     * Retrieves the list of container IDs assigned to this node from the SMSC.
     *
     * @return A Future that will resolve with the List of Container IDs assigned to this node.
     */
    @Override
    public Future<List<String>> getContainers()
            throws NotRegisteredException {

        Future<List<String>> future = _executorService.submit(() -> {

            List<String> containerIDs = new ArrayList<>();

            // TODO - Search the Container/Node Assignment table by nodeID to get a list of all of the Container IDs to which this node is assigned.
            String scope = contract;
            String table = "containerNodeAssignment";
            String key = "foo";
            String lowerBound = nodeID;
            String upperBound = nodeID;
            int limit = Integer.MAX_VALUE;
            boolean json = true;
            TableRows tableRows = _eosRPCAdapter.chain().getTableRows(contract, scope, table, key, lowerBound, upperBound, limit, json);

            // TODO - Grab the Container ID from the Table Row, if we are assigned to the Container.

            return containerIDs;
        });

        return future;
    }

    /**
     * Retrieves the Container Information for the specified container from the SMSC.
     *
     * @param containerID The ID of the container whose information is being retrieved.
     *
     * @return A Future that will resolve to the Container Info of the requested container.   The future will return
     * null if the container ID does not exist, or if this node is not assigned to the container.
     */
    @Override
    public Future<ContainerInfo> getContainerInfo(String containerID)
            throws NotRegisteredException {

        Future<ContainerInfo> future = _executorService.submit(() -> {

            boolean assigned = false;
            ContainerInfo containerInfo = null;

            {
                // TODO - Search the Container/Node Assignment table by containerID to see if we are assigned to the specified container.
                String scope = contract;
                String table = "containerNodeAssignment";
                String key = "foo";
                String lowerBound = containerID;
                String upperBound = containerID;
                int limit = Integer.MAX_VALUE;
                boolean json = true;
                TableRows assignmentTableRows = _eosRPCAdapter.chain().getTableRows(contract, scope, table, key, lowerBound, upperBound, limit, json);

                // TODO - Iterate through the table rows and see if this Node ID is assigned to the container.
                assigned = false ;
            }

            if (assigned) {
                // TODO - Search the Container table by container ID to get the info about the specified container
                String scope = contract;
                String table = "container";
                String lowerBound = containerID;
                String upperBound = containerID;
                int limit = Integer.MAX_VALUE;
                boolean json = true;
                TableRows containerTableRows = _eosRPCAdapter.chain().getTableRows(contract, scope, table, lowerBound, upperBound, limit, json);

                // TODO - Grab the Container Info from the Table Row.
            } else {
                throw new NoSuchContainerException("This node is not assigned to the specified container");
            }

            return containerInfo;
        });

        return future;
    }

    /**
     * Retrieves the list of Node IDs that are assigned to the specified container from the SMSC.
     *
     * @param containerID The ID of the container whose node list is being retrieved.
     *
     * @return A Future that will resolve to the List of Nodes assigned to the container ID.  The future will throw an
     * exception if the container ID does not exist, or if this node is not assigned to the container.
     */
    @Override
    public Future<List<String>> getNodesForContainer(String containerID)
            throws NotRegisteredException {

        Future<List<String>> future = _executorService.submit(() -> {

            List<String> nodeIDs = new ArrayList<>();

            // TODO - Search the Container/Node Assignment table by containerID to get a list of all of the Node IDs to which the container is assigned.
            String scope = contract;
            String table = "containerNodeAssignment";
            String key = "foo";
            String lowerBound = nodeID;
            String upperBound = nodeID;
            int limit = Integer.MAX_VALUE;
            boolean json = true;
            TableRows tableRows = _eosRPCAdapter.chain().getTableRows(contract, scope, table, key, lowerBound, upperBound, limit, json);

            // TODO - Grab the Node ID from the Table Row, if it is assigned to the Container.

            return nodeIDs;
        });

        return future;
    }

    /**
     * Registers this node with the SMSC.  This process will involve the staking of tokens with the SMSC from the
     * configured staking account.
     *
     * @return A Future that can be used to wait for the completion of the registration process.
     */
    @Override
    public Future<?> registerNode() {
        // Set the NodeID assigned to this node on registration.
        return null;
    }

    /**
     * Unregisters this node with the SMSC.  This process involves the unstaking of tokens from the SMSC as well as the
     * transfer of any containers hosted on this node to other containers to maintain availability of end user data.
     *
     * @return A Future that can be used to wait for the completion of the unregistration process.
     */
    @Override
    public Future<?> unregisterNode()
            throws NotRegisteredException {
        // Cancel all pending operations
        // Shutdown the Executor
        // Clear the NodeID previously assigned to this node.
        return null;
    }

    /**
     * The ID of the account that should be used for staking tokens on registration.  The configured blockchain wallet
     * should contain the keys for this account to insure that transactions can be properly signed.
     *
     * @param stakingAccount The ID of the account to use for staking tokens.
     */
    @Override
    public void setStakingAccount(String stakingAccount) {

    }

    /**
     * The ID of the account that should be used for signing normal transactions to the SMSC.  The configured blockchain
     * wallet should contain the keys for this account to insure that transactions are properly signed.
     *
     * @param signingAccount The ID of the account to use for signing transactions.
     */
    @Override
    public void setSigningAccount(String signingAccount) {

    }

    /**
     * Retrieves a list of disputes that are assigned to this node that have not been handled yet.
     *
     * @return A Future that will resolve to the list of Disputes assigned to this node.
     */
    @Override
    public Future<List<Dispute>> getAssignedDisputes()
            throws NotRegisteredException {
        return null;
    }

    /**
     * Submits a ruling on a dispute to the SMSC.
     *
     * @param disputeID The ID of the dispute for which a ruling is being submitted.
     * @param ruling    The dispute ruling being submitted.
     *
     * @return A Future that can be used to wait for the completion of the dispute resolution submission.
     */
    @Override
    public Future<?> sendDisputeResolution(String disputeID, String ruling)
            throws NotRegisteredException {
        return null;
    }


    // -------- Accessor Methods --------


    public void setEosRPCAdapter(EOSRPCAdapter eosRPCAdapter) {
        _eosRPCAdapter = eosRPCAdapter;
    }
}
