package io.topiacoin.node.rest;

import io.topiacoin.node.BusinessLogic;
import io.topiacoin.node.exceptions.BadRequestException;
import io.topiacoin.node.model.ContainerInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@RestController
public class APIController {

    private Log _log = LogFactory.getLog(this.getClass());

    @Autowired
    private BusinessLogic _businessLogic;

    // -------- Lifecycle Methods --------

    @PostConstruct
    public void initialize() {
        _log.info("Initializing Master Controller" ) ;
        _log.info("Initialized Master Controller" ) ;
    }

    @PreDestroy
    public void shutdown () {
        _log.info("Shutting Down Master Controller" ) ;
        _log.info("Shut Down Master Controller" ) ;
    }

    // -------- REST Methods --------

    @RequestMapping("/")
    public String test() {
        return "Hello, World";
    }

    @RequestMapping(value = "/container", method = RequestMethod.GET)
    public ContainerInfo getContainer(
            @RequestParam("containerID") String containerID) {

        if (TextUtils.isBlank(containerID)) {
            throw new BadRequestException("ContainerID not specified.") ;
        }
        return null;
    }

    @RequestMapping(value="/container", method = RequestMethod.POST)
    public ResponseEntity<Void> createContainer(
            @RequestBody ContainerCreationRequest creationRequest) {

        if (creationRequest == null ) {
            throw new BadRequestException("Invalid Body.") ;
        }

        _log.info ( "Creation request: " + creationRequest) ;

        if (TextUtils.isBlank(creationRequest.containerID)) {
            throw new BadRequestException("ContainerID not specified.") ;
        }
        return new ResponseEntity<>(HttpStatus.CREATED) ;
    }

    @RequestMapping(value="/container", method = RequestMethod.PUT)
    public ResponseEntity<Void> replicateContainer(
            @RequestBody ContainerReplicationRequest replicationRequest) {

        if (replicationRequest == null ) {
            throw new BadRequestException("Invalid Body.") ;
        }

        _log.info ( "Replication request: " + replicationRequest) ;

        if (TextUtils.isBlank(replicationRequest.containerID)) {
            throw new BadRequestException("ContainerID not specified.") ;
        }
        if (TextUtils.isBlank(replicationRequest.peerNodeID)) {
            throw new BadRequestException("Peer Node ID not specified.") ;
        }

        return new ResponseEntity<>(HttpStatus.CREATED) ;
    }


    // -------- Accessor Methods --------


    public void setBusinessLogic(BusinessLogic businessLogic) {
        _businessLogic = businessLogic;
    }
}
