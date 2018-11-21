package io.topiacoin.node.smsc;

import io.topiacoin.eosrpcadapter.exceptions.ChainException;
import io.topiacoin.node.exceptions.NotRegisteredException;
import io.topiacoin.node.model.ChallengeSolution;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.Dispute;
import io.topiacoin.node.model.NodeConnectionInfo;

import java.util.List;
import java.util.concurrent.Future;

public interface SMSCManager {

    /**
     * Submits the solution to a Proof of Hosting/Replication to the SMSC for validation and payment.
     *
     * @param containerID The ID of the container to which the solution belongs.
     * @param solution    The solution to the replication challenge for the specified container.
     *
     * @return A Future that can be used to wait for the completion of the proof submisison.
     */
    Future<Void> submitProofSolution(String containerID, ChallengeSolution solution);

    /**
     * Retrieves the list of container IDs assigned to this node from the SMSC.
     *
     * @return A Future that will resolve with the List of Container IDs assigned to this node.
     */
    Future<List<String>> getContainers() throws NotRegisteredException;

    /**
     * Retrieves the Container Information for the specified container from the SMSC.
     *
     * @param containerID The ID of the container whose information is being retrieved.
     *
     * @return A Future that will resolve to the Container Info of the requested container.   The future will throw an
     * exception if the container ID does not exist, or if this node is not assigned to the container.
     */
    Future<ContainerInfo> getContainerInfo(String containerID)throws NotRegisteredException;

    /**
     * Retrieves the list of Node IDs that are assigned to the specified container from the SMSC.
     *
     * @param containerID The ID of the container whose node list is being retrieved.
     *
     * @return A Future that will resolve to the List of Nodes assigned to the container ID.  The future will throw an
     * exception if the container ID does not exist, or if this node is not assigned to the container.
     */
    Future<List<NodeConnectionInfo>> getNodesForContainer(String containerID)throws NotRegisteredException;

    Future<NodeConnectionInfo> getNodeInfo(String nodeID) throws ChainException;

    /**
     * Registers this node with the SMSC.  This process will involve the staking of tokens with the SMSC from the
     * configured staking account.
     *
     * @return A Future that can be used to wait for the completion of the registration process.
     * @param nodeID
     */
    Future<Void> registerNode(String nodeID);

    /**
     * Unregisters this node with the SMSC.  This process involves the unstaking of tokens from the SMSC as well as the
     * transfer of any containers hosted on this node to other containers to maintain availability of end user data.
     *
     * @return A Future that can be used to wait for the completion of the unregistration process.
     * @param nodeID
     */
    Future<Void> unregisterNode(String nodeID)throws NotRegisteredException;

    /**
     * The ID of the account that should be used for staking tokens on registration.  The configured blockchain wallet
     * should contain the keys for this account to insure that transactions can be properly signed.
     *
     * @param stakingAccount The ID of the account to use for staking tokens.
     */
    void setStakingAccount(String stakingAccount);

    /**
     * The ID of the account that should be used for signing normal transactions to the SMSC.  The configured blockchain
     * wallet should contain the keys for this account to insure that transactions are properly signed.
     *
     * @param signingAccount The ID of the account to use for signing transactions.
     */
    void setSigningAccount(String signingAccount);

    void setContractAccount(String contractAccount);

    void setWalletName(String walletName);

    boolean isRegistered();

    String getNodeID();

    /**
     * Retrieves a list of disputes that are assigned to this node that have not been handled yet.
     *
     * @return A Future that will resolve to the list of Disputes assigned to this node.
     */
    Future<List<Dispute>> getAssignedDisputes()throws NotRegisteredException;

    Future<Dispute> getDispute(String disputeID);

    /**
     * Submits a ruling on a dispute to the SMSC.
     *
     * @param disputeID The ID of the dispute for which a ruling is being submitted.
     * @param ruling    The dispute ruling being submitted.
     *
     * @return A Future that can be used to wait for the completion of the dispute resolution submission.
     */
    Future<Void> sendDisputeResolution(String disputeID, String ruling)throws NotRegisteredException;

}
