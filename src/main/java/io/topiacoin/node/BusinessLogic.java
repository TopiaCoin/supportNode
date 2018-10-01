package io.topiacoin.node;

import io.topiacoin.node.micronetwork.MicroNetworkManager;
import io.topiacoin.node.proof.ProofSolver;
import io.topiacoin.node.smsc.SMSCManager;
import io.topiacoin.node.storage.DataStorageManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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

    // -------- Lifecycle Methods --------

    @PostConstruct
    public void initialize() {
        _log.info("Initializing Business Logic" ) ;
        _log.info("Initialized Business Logic" ) ;
    }

    @PreDestroy
    public void shutdown () {
        _log.info("Shutting Down Business Logic" ) ;
        _log.info("Shut Down Business Logic" ) ;
    }
}
