package io.topiacoin.node.smsc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class SMSCManager {

    private Log _log = LogFactory.getLog(this.getClass());

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

}
