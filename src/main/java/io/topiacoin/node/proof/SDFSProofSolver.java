package io.topiacoin.node.proof;

import io.topiacoin.node.exceptions.InitializationException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.micronetwork.MicroNetworkManager;
import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ChallengeChunkInfo;
import io.topiacoin.node.model.ChallengeSolution;
import io.topiacoin.node.storage.provider.DataStorageProvider;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SDFSProofSolver implements ProofSolver {

    private Log _log = LogFactory.getLog(this.getClass());

    @Autowired
    private DataStorageProvider _dataStorageProvider;

    @Autowired
    private MicroNetworkManager _microNetworkManager;

    @PostConstruct
    public void initialize() {
        _log.info ("Initializing the SDFS Proof Solver" ) ;

        if ( _dataStorageProvider == null ) {
            throw new InitializationException("Failed to initialize SDFS Proof Solver.  No Data Storage Provider configured" ) ;
        }
        if ( _microNetworkManager == null ) {
            throw new InitializationException("Failed to initialize SDFS Proof Solver.  No Micro Network Manager configured" ) ;
        }
        _log.info ("Initialized the SDFS Proof Solver" ) ;
    }

    @PreDestroy
    public void shutdown() {
        _log.info ("Shutting Down the SDFS Proof Solver" ) ;
        _log.info ("Shut Down the SDFS Proof Solver" ) ;
    }

    @Override
    public ChallengeSolution generateSolution(Challenge proofChallenge) {

        String merkleRoot = generateReplicateSolution(proofChallenge);

        // TODO - Use the MicroNetwork Blockchain Manager to get the Verification Value, Transaction ID, and Blocknumber needed to validate the chain.
        String verificationValue = null;
        String transactionID = null;
        long blockNumber = -1 ;

        // Collapse the PreLeaves into the Merkle Root
        return new ChallengeSolution(verificationValue, transactionID, blockNumber,merkleRoot);
    }

    private String generateReplicateSolution(Challenge proofChallenge) {
        String merkleRoot = null;
        List<byte[]> leaves = new ArrayList<>(proofChallenge.getChallengeChunks().size());
        try {
            // Generate the Pre Image of all of the chunks
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            int leafIndex = 0 ;
            for (ChallengeChunkInfo info : proofChallenge.getChallengeChunks()) {
                digest.reset();
                ByteArrayOutputStream baos = new ByteArrayOutputStream(info.getLength());
                DigestOutputStream dos = new DigestOutputStream(baos, digest);
                _dataStorageProvider.fetchData(info.getChunkID(), info.getOffset(), info.getLength(), baos);
                dos.close();
                byte[] leafPreImageHash = digest.digest(baos.toByteArray());
                leaves.add(leafPreImageHash);
                _log.info ( "Merkle Pre Image " + (leafIndex++) + ": " + Hex.encodeHexString(leafPreImageHash));
            }

            // Combine adjacent nodes until there is only one left.
            int level = 0 ;
            while (leaves.size() > 1) {
                List<byte[]> curLeaves = leaves;
                leaves = new ArrayList<>();

                for (int i = 0; i < curLeaves.size(); ) {
                    byte[] newDigest;
                    if (i + 1 < curLeaves.size()) {
                        // There are more than two nodes left, so combine the next two.
                        digest.reset();
                        if ( level == 0 ) {
                            // Leaf nodes are prepended with 0x00.
                            digest.update((byte)0x00);
                        } else {
                            // Inner nodes are prepended with 0x01.
                            digest.update((byte) 0x01);
                        }
                        digest.update(curLeaves.get(i));
                        digest.update(curLeaves.get(i + 1));
                        newDigest = digest.digest();
                        i += 2;
                    } else {
                        // This is a single node at the end of the list.  Just push it forward.
                        newDigest = curLeaves.get(i);
                        i++;
                    }
                    _log.info ( "Merkle Node Level " + level + " : " + Hex.encodeHexString(newDigest));

                    leaves.add(newDigest);
                }
                level++;
            }

            merkleRoot = Hex.encodeHexString(leaves.get(0));
            _log.info ( "MerkleRoot : " + merkleRoot);
        } catch (IOException e) {
            _log.info ( "Exception retrieving data", e );
        } catch (NoSuchDataItemException e) {
            _log.info ( "A requested piece of data was not found.", e);
        } catch (NoSuchAlgorithmException e) {
            _log.fatal("OMG!!  Java doesn't support SHA-256 anymore!!", e);
            throw new RuntimeException("OMG!! Java doesn't support SHA-256 anymore!!", e);
        }
        return merkleRoot;
    }

    // -------- Accessor Methods --------


    public void setDataStorageProvider(DataStorageProvider dataStorageProvider) {
        _dataStorageProvider = dataStorageProvider;
    }

    public void setMicroNetworkManager(MicroNetworkManager microNetworkManager) {
        _microNetworkManager = microNetworkManager;
    }
}
