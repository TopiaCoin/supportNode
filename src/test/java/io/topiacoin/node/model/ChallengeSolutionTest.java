package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChallengeSolutionTest {

    @Test
    public void testAccessors() throws Exception {

        String verificationValue = "23456789";
        String transactionID = "0xdeadbeef";
        long blockNumber = 27849365;
        String chunkHash = "0xabcdef0123456789";

        ChallengeSolution solution = new ChallengeSolution(verificationValue, transactionID, blockNumber, chunkHash);

        assertEquals ( verificationValue, solution.getVerificationValue());
        assertEquals(transactionID, solution.getTransactionID());
        assertEquals(blockNumber, solution.getBlockNumber());
        assertEquals(chunkHash, solution.getChunkHash());
    }
}
