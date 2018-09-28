package io.topiacoin.node.micronetwork;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
}
