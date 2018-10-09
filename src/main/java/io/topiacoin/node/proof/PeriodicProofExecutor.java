package io.topiacoin.node.proof;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * This class will periodically execute the Proof Solver for each container hosted by
 * this node.  Once a solution has been generated, it will be submitted to the SMSC.
 */
public class PeriodicProofExecutor {

    private Log _log = LogFactory.getLog(this.getClass());

    @PostConstruct
    public void initialize() {
        _log.info ( "Initializing Periodic Proof Executor" ) ;

        throw new NotImplementedException();

        //_log.info ( "Initialized Periodic Proof Executor" ) ;
    }

    @PreDestroy
    public void shutDown() {
        _log.info ( "Shutting Down Periodic Proof Executor" ) ;
        _log.info ( "Shut Down Periodic Proof Executor" ) ;
    }
}
