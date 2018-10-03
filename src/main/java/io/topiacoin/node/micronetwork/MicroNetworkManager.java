package io.topiacoin.node.micronetwork;

import io.topiacoin.node.model.MicroNetworkInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Future;

@Component
public class MicroNetworkManager {

    private Log _log = LogFactory.getLog(this.getClass());

    @PostConstruct
    public void initialize() {
        _log.info ( "Initializing Micro Network Manager" ) ;
        _log.info ( "Initialized Micro Network Manager" ) ;
    }

    @PreDestroy
    public void shutdown() {
        _log.info ( "Shutting Down Micro Network Manager" ) ;
        _log.info ( "Shut Down Micro Network Manager" ) ;
    }

    public void createBlockchain(String blockchainID) {

    }

    public Future syncBlockchain(String blockchainConnectionString, String blockchainID) {
        return null;
    }

    public void startBlockchain(String blockchainID) {

    }

    public void stopBlockchain (String blockchainID) {

    }

    public void destroyBlockchain(String blockchainID) {

    }

    public BlockchainVerificationInformation getVerificationValue(String blockchainID) {
        return null;
    }

    public List<String> listRunningBlockchains() {
        return null;
    }

    public List<String> listStoppedBlockchains() {
        return null;
    }

    public MicroNetworkInfo getBlockchainInfo(String blockchainID) {
        return null;
    }

}
