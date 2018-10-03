package io.topiacoin.node.smsc;

import io.topiacoin.eosrpcadapter.EOSRPCAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class EOSSMSCManager implements SMSCManager {

    private Log _log = LogFactory.getLog(this.getClass());
    private String _stakingAccount;
    private String _signingAccount;

    private EOSRPCAdapter _eosRPCAdapter;

    // -------- Lifecycle Methods --------

    @PostConstruct
    public void initialize() {
        _log.info("Initializing SMSC Manager" ) ;
        _log.info("Initialized SMSC Manager" ) ;
    }

    @PreDestroy
    public void shutdown () {
        _log.info("Shutting Down SMSC Manager" ) ;
        _log.info("Shut Down SMSC Manager" ) ;
    }

    // -------- Public Methods --------

    /**
     * @param containerID
     * @param solution
     * @param callback
     */
    @Override
    public void submitProofSolution(String containerID, String solution, Callback callback) {

    }

    /**
     * @param callback
     */
    @Override
    public void getContainers(Callback callback) {

    }

    /**
     * @param containerID
     * @param callback
     */
    @Override
    public void getContainerInfo(String containerID, Callback callback) {

    }

    /**
     * @param containerID
     * @param callback
     */
    @Override
    public void getNodesForContainer(String containerID, Callback callback) {

    }

    /**
     * @param callback
     */
    @Override
    public void registerNode(Callback callback) {

    }

    /**
     * @param callback
     */
    @Override
    public void unregisterNode(Callback callback) {

    }

    /**
     * @param stakingAccount
     */
    @Override
    public void setStakingAccount(String stakingAccount) {

    }

    /**
     * @param signingAccount
     */
    @Override
    public void setSigningAccount(String signingAccount) {

    }

    /**
     * @param callback
     */
    @Override
    public void getAssignedDisputes(Callback callback) {

    }

    /**
     * @param disputeID
     * @param ruling
     * @param callback
     */
    @Override
    public void sendDisputeResolution(String disputeID, String ruling, Callback callback) {

    }

    // -------- Accessor Methods --------


    public void setEosRPCAdapter(EOSRPCAdapter eosRPCAdapter) {
        _eosRPCAdapter = eosRPCAdapter;
    }
}
