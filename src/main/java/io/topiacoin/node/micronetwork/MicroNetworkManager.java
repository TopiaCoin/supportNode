package io.topiacoin.node.micronetwork;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
}
