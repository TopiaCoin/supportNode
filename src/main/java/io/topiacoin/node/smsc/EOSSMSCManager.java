package io.topiacoin.node.smsc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.topiacoin.eosrpcadapter.EOSRPCAdapter;
import io.topiacoin.eosrpcadapter.exceptions.ChainException;
import io.topiacoin.eosrpcadapter.exceptions.WalletException;
import io.topiacoin.eosrpcadapter.messages.Action;
import io.topiacoin.eosrpcadapter.messages.TableRows;
import io.topiacoin.eosrpcadapter.messages.Transaction;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NotRegisteredException;
import io.topiacoin.node.model.ChallengeSolution;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.Dispute;
import io.topiacoin.node.model.NodeConnectionInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class EOSSMSCManager implements SMSCManager {

    private Log _log = LogFactory.getLog(this.getClass());

    // -- Spring Autowired Members --

//    @Autowired
    private String _stakingAccount;

//    @Autowired
    private String _signingAccount;

//    @Autowired
    private String _walletName;

//    @Autowired
    private String _contractAccount;

//    @Autowired
    private EOSRPCAdapter _eosRPCAdapter;

    // -- Internal Members --

    private ExecutorService _executorService;
    private String _nodeID;

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
    public Future<Void> submitProofSolution(String containerID, ChallengeSolution solution)
            throws NotRegisteredException {

        return _executorService.submit(() -> {

            List<Transaction.Authorization> authorizations = new ArrayList<>();
            authorizations.add(new Transaction.Authorization(_signingAccount, "active"));

            ObjectMapper objectMapper = new ObjectMapper();
            String solutionString = objectMapper.writeValueAsString(solution);

            Map<String, Object> args = new HashMap<>();
            args.put("containerID", containerID);
            args.put("nodeID", _nodeID);
            args.put("solution", solutionString);

            Action action = new Action(_contractAccount, "solveproof", authorizations, args);
            Date expirationDate = new Date(System.currentTimeMillis() + 60000);
            _eosRPCAdapter.pushTransaction(action, expirationDate, _walletName);

            return null;
        });
    }

    /**
     * Retrieves the list of container IDs assigned to this node from the SMSC.
     *
     * @return A Future that will resolve with the List of Container IDs assigned to this node.
     */
    @Override
    public Future<List<String>> getContainers()
            throws NotRegisteredException {

        if ( _nodeID == null  ) {
            throw new NotRegisteredException("This node is not yet registered");
        }

        return _executorService.submit(() -> {

            List<String> containerIDs = new ArrayList<>();

            // Search the Container/Node Assignment table by _nodeID to get a list of all of the Container IDs to which this node is assigned.
            String scope = _contractAccount;
            String table = "assignments";
            int indexPosition = 3 ; // This is the Node ID Index
            String keyType = "i64";
            String lowerBound = _nodeID;
            String upperBound = Long.toUnsignedString(Long.parseUnsignedLong(_nodeID) + 1);
            int limit = Integer.MAX_VALUE;
            boolean json = true;
            TableRows tableRows = _eosRPCAdapter.chain().getTableRows(_contractAccount, scope, table, indexPosition, keyType, lowerBound, upperBound, limit, json);

            // Grab the Container ID from the Table Row, if we are assigned to the Container.
            for ( Map<String, Object> row : tableRows.rows) {
                if ( row.get("nodeID").equals(_nodeID)) {
                    containerIDs.add(row.get("containerID").toString());
                }
            }

            return containerIDs;
        });
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

        return _executorService.submit(() -> {

            boolean assigned = false;
            ContainerInfo containerInfo = null;

            long contID = stringToEosID(containerID);
            {
                // Search the Container/Node Assignment table by containerID to see if we are assigned to the specified container.
                String scope = _contractAccount;
                String table = "assignments";
                int indexPosition = 2 ; // This is the Container ID Index
                String keyType = "i64";
                String lowerBound = Long.toUnsignedString(contID);
                String upperBound = Long.toUnsignedString(contID + 1);
                int limit = Integer.MAX_VALUE;
                boolean json = true;
                TableRows assignmentTableRows = _eosRPCAdapter.chain().getTableRows(_contractAccount, scope, table, indexPosition, keyType, lowerBound, upperBound, limit, json);

                // Iterate through the table rows and see if this Node ID is assigned to the container.
                for ( Map<String,Object> row : assignmentTableRows.rows){
                    if ( row.get("nodeID").equals(_nodeID)) {
                        assigned = true ;
                        break ;
                    }
                }
            }

            if (assigned) {
                // Search the Container table by container ID to get the info about the specified container
                String scope = _contractAccount;
                String table = "containers";
                String lowerBound = Long.toUnsignedString(contID);
                String upperBound = Long.toUnsignedString(contID + 1);
                int limit = Integer.MAX_VALUE;
                boolean json = true;
                TableRows containerTableRows = _eosRPCAdapter.chain().getTableRows(_contractAccount, scope, table, lowerBound, upperBound, limit, json);

                // Grab the Container Info from the Table Row.
                if ( containerTableRows.rows.size() > 0 ) {
                    Map<String, Object> row = containerTableRows.rows.get(0);
                    Integer expDateInt = (Integer) row.get("expirationTime");
                    long expirationDate = (long)expDateInt;
                    containerInfo = new ContainerInfo(containerID, expirationDate);
                }
            } else {
                throw new NoSuchContainerException("This node is not assigned to the specified container");
            }

            return containerInfo;
        });
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
    public Future<List<NodeConnectionInfo>> getNodesForContainer(String containerID)
            throws NotRegisteredException {

        return _executorService.submit(() -> {

            List<NodeConnectionInfo> nodeInfoList = new ArrayList<>();

            long containerIDValue = stringToEosID(containerID);

            // Search the Container/Node Assignment table by containerID to get a list of all of the Node IDs to which the container is assigned.
            String scope = _contractAccount;
            String table = "assignments";
            int indexPosition = 2 ; // This is the Node ID Index
            String keyType = "i64";
            String lowerBound = Long.toUnsignedString(containerIDValue);
            String upperBound = Long.toUnsignedString(containerIDValue + 1);
            int limit = Integer.MAX_VALUE;
            boolean json = true;
            TableRows tableRows = _eosRPCAdapter.chain().getTableRows(_contractAccount, scope, table, indexPosition, keyType, lowerBound, upperBound, limit, json);

            // Grab the Node Info from the Table Row, if it is assigned to the Container.
            for ( Map<String, Object> row : tableRows.rows) {
                if ( row.get("containerID").equals(containerID)) {
                    String nodeID = (String) row.get("nodeID");
                    NodeConnectionInfo nodeInfo = getNodeInfoInternal(nodeID) ;
                    nodeInfoList.add(nodeInfo);
                }
            }

            return nodeInfoList;
        });
    }

    @Override
    public Future<NodeConnectionInfo> getNodeInfo(String nodeID) throws ChainException {

        return _executorService.submit(() -> {
            return getNodeInfoInternal(nodeID);
        });
    }

    private NodeConnectionInfo getNodeInfoInternal(String nodeID) throws ChainException {
        NodeConnectionInfo nodeConnectionInfo = null;

        long nodeIDValue = stringToEosID(nodeID);

        // Search the Container/Node Assignment table by containerID to get a list of all of the Node IDs to which the container is assigned.
        String scope = _contractAccount;
        String table = "nodes";
        String lowerBound = Long.toUnsignedString(nodeIDValue);
        String upperBound = Long.toUnsignedString(nodeIDValue + 1);
        int limit = 1;
        boolean json = true;
        TableRows tableRows = _eosRPCAdapter.chain().getTableRows(_contractAccount, scope, table, lowerBound, upperBound, limit, json);

        if (tableRows.rows.size() > 0) {
            Map<String, Object> row = tableRows.rows.get(0);
            if (row.get("nodeID").equals(nodeID)) {
                String nodeURL = (String) row.get("nodeURL");
                nodeConnectionInfo = new NodeConnectionInfo(nodeID, nodeURL);
            }
        }

        return nodeConnectionInfo;
    }


    /**
     * Registers this node with the SMSC.  This process will involve the staking of tokens with the SMSC from the
     * configured staking account.
     *
     * @return A Future that can be used to wait for the completion of the registration process.
     * @param nodeID
     */
    @Override
    public Future<Void> registerNode(String nodeID) {

        return _executorService.submit(() -> {

            _nodeID = nodeID;

            // Transaction expires 60 seconds from now
            Date expirationDate = new Date(System.currentTimeMillis() + 60000);

            // Create the Actions list
            List<Action> actions = new ArrayList<>();

            // Transfer topia coin from the staking account to the contract account
            Action transferAction;
            {
                String tokenContract = "eosio.token";
                String methodName = "transfer";

                String amount = "10.0002 TPC";

                // Setup the Method ARgs
                Map<String, Object> args = new HashMap<>();
                args.put("from", _stakingAccount);
                args.put("to", _contractAccount);
                args.put("quantity", amount);
                args.put("memo", "Stake Transfer");

                // Specify the authorizations of this transaction
                List<Transaction.Authorization> authorizations = new ArrayList<>();
                authorizations.add(new Transaction.Authorization(_stakingAccount, "active"));

                transferAction = new Action(tokenContract, methodName, authorizations, args);
            }
            actions.add(transferAction);

            // Execute the stakenode method
            Action stakeAction;
            {
                // Set the name of the contract method that will be invoked
                String methodName = "stakenode";

                // Setup the arguments to the "stakenode" method of the SMSC
                Map<String, Object> args = new HashMap<>();
                args.put("nodeid", nodeID);
                args.put("url", "http://localhost:1234/");
                args.put("chainCapacity", 5);
                args.put("replicationCapacity", 10);
                args.put("registeringAccount", _stakingAccount);
                args.put("operatingAccount", _signingAccount);

                // Specify the authorizations of this transaction
                List<Transaction.Authorization> authorizations = new ArrayList<>();
                authorizations.add(new Transaction.Authorization(_stakingAccount, "active"));
                authorizations.add(new Transaction.Authorization(_signingAccount, "active"));

                stakeAction = new Action(_contractAccount, methodName, authorizations, args);


            }
            actions.add(stakeAction);

            try {
                _eosRPCAdapter.pushTransaction(actions, expirationDate, _walletName);
            } catch (WalletException e) {
                e.printStackTrace();
                throw new RuntimeException("Wallet Exception", e);
            } catch (ChainException e) {
                e.printStackTrace();
                throw new RuntimeException("Chain Exception", e);
            }

            return null;
        });

    }

    /**
     * Unregisters this node with the SMSC.  This process involves the unstaking of tokens from the SMSC as well as the
     * transfer of any containers hosted on this node to other containers to maintain availability of end user data.
     *
     * @return A Future that can be used to wait for the completion of the unregistration process.
     * @param nodeID
     */
    @Override
    public Future<Void> unregisterNode(String nodeID)
            throws NotRegisteredException {

        return _executorService.submit(() -> {

            // Clear the internally stored nodeID.
            _nodeID = null ;

            // Transaction expires 60 seconds from now
            Date expirationDate = new Date(System.currentTimeMillis() + 60000);

            // Create the Actions list
            List<Action> actions = new ArrayList<>();

            // Execute the stakenode method
            Action stakeAction;
            {
                // Set the name of the contract method that will be invoked
                String methodName = "unstakenode";

                // Setup the arguments to the "stakenode" method of the SMSC
                Map<String, Object> args = new HashMap<>();
                args.put("nodeid", nodeID);

                // Specify the authorizations of this transaction
                List<Transaction.Authorization> authorizations = new ArrayList<>();
                authorizations.add(new Transaction.Authorization(_stakingAccount, "active"));
                authorizations.add(new Transaction.Authorization(_signingAccount, "active"));

                stakeAction = new Action(_contractAccount, methodName, authorizations, args);


            }
            actions.add(stakeAction);

            try {
                _eosRPCAdapter.pushTransaction(actions, expirationDate, _walletName);
            } catch (WalletException e) {
                e.printStackTrace();
                throw new RuntimeException("Wallet Exception", e);
            } catch (ChainException e) {
                e.printStackTrace();
                throw new RuntimeException("Chain Exception", e);
            }

            return null;
        });
    }

    @Override
    public boolean isRegistered() {
        return _nodeID != null;
    }

    @Override
    public String getNodeID() {
        return _nodeID;
    }

    /**
     * Retrieves a list of disputes that are assigned to this node that have not been handled yet.
     *
     * @return A Future that will resolve to the list of Disputes assigned to this node.
     */
    @Override
    public Future<List<Dispute>> getAssignedDisputes()
            throws NotRegisteredException {
        return _executorService.submit(() -> {

            List<Dispute> disputeList = new ArrayList<>();

            String scope = _contractAccount;
            String table = "decisions";

            int indexPosition = 3;
            String keyType = "i64";
            String lowerBound = _nodeID;
            String upperBound = eosIDToString(stringToEosID(_nodeID) + 1);
            int limit = Integer.MAX_VALUE;

            TableRows assignmentTableRows = _eosRPCAdapter.chain().getTableRows(_contractAccount, scope, table, indexPosition, keyType, lowerBound, upperBound, limit, true);

            for ( Map<String, Object> row : assignmentTableRows.rows) {
                if ( row.get("nodeID").equals(_nodeID)) {
                    Dispute dispute = getDisputeInternal(row.get("disputeID")) ;
                    disputeList.add(dispute);
                }
            }

            return disputeList;
        });
    }

    @Override
    public Future<Dispute> getDispute(String disputeID) {
        return _executorService.submit(() ->{
            return getDisputeInternal(disputeID);
        });
    }

    private Dispute getDisputeInternal(Object disputeID) throws ChainException {
        Dispute dispute = null;

        String scope = _contractAccount;
        String table = "disputes";

        int indexPosition = 1;
        String keyType = "i64";
        String lowerBound = disputeID.toString();
        int limit = 1;

        TableRows assignmentTableRows = _eosRPCAdapter.chain().getTableRows(
                _contractAccount,
                scope,
                table,
                indexPosition,
                keyType,
                lowerBound,
                "-1",
                limit,
                true);

        if ( assignmentTableRows.rows.size() > 0 ) {
            Map<String, Object> row = assignmentTableRows.rows.get(0);

            String containerID = (String) row.get("containerID");
            String disputingAccount = (String) row.get("disputingAccount");
            String disputedNodeID = (String) row.get("nodeID");
            String chainURL = (String) row.get("chainURL");
            String status = (String) row.get("status");
            List<String> chunkIDs = (List<String>) row.get("disputedChunkIDs");

            dispute = new Dispute (
                    disputeID.toString(),
                    containerID,
                    disputedNodeID,
                    disputingAccount,
                    status,
                    chainURL,
                    chunkIDs);
        }

        return dispute;
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
    public Future<Void> sendDisputeResolution(String disputeID, String ruling)
            throws NotRegisteredException {
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
        _stakingAccount = stakingAccount;
    }

    /**
     * The ID of the account that should be used for signing normal transactions to the SMSC.  The configured blockchain
     * wallet should contain the keys for this account to insure that transactions are properly signed.
     *
     * @param signingAccount The ID of the account to use for signing transactions.
     */
    @Override
    public void setSigningAccount(String signingAccount) {
        _signingAccount = signingAccount;
    }

    @Override
    public void setContractAccount(String contractAccount) {
        _contractAccount = contractAccount;
    }

    @Override
    public void setWalletName(String walletName) {
        _walletName = walletName;
    }


    // -------- Private Helper Methods --------

    /**
     * Converts a String ID into a Long, which are used as the EOS IDs
     *
     * @param eosIDString The String containing the ID.
     *
     * @return A long representation of the EOS ID String.
     */
    private long stringToEosID(String eosIDString) {
        return Long.parseUnsignedLong(eosIDString);
    }

    /**
     * Converts a Long into an EOS ID String.
     *
     * @param eosID A long representation of an EOS ID.
     *
     * @return A String representation of the EOS ID.
     */
    private String eosIDToString(long eosID) {
        return Long.toUnsignedString(eosID);
    }

    // -------- Accessor Methods --------


    public void setEosRPCAdapter(EOSRPCAdapter eosRPCAdapter) {
        _eosRPCAdapter = eosRPCAdapter;
    }
}
