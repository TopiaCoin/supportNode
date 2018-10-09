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

    @Test
    public void testEqualsAndHashCode() throws Exception {
        String verificationValue1 = "23456789";
        String verificationValue2 = "98765432";
        String transactionID1 = "0xdeadbeef";
        String transactionID2 = "0xbeefdead";
        long blockNumber1 = 27849365;
        long blockNumber2 = 33845634;
        String chunkHash1 = "0xabcdef0123456789";
        String chunkHash2 = "0xfedcba9876543210";


        ChallengeSolution solution1 = new ChallengeSolution(verificationValue1, transactionID1, blockNumber1, chunkHash1);
        ChallengeSolution solution2 = new ChallengeSolution(verificationValue1, transactionID1, blockNumber1, chunkHash1);

        ChallengeSolution solution3 = new ChallengeSolution(verificationValue2, transactionID1, blockNumber1, chunkHash1);
        ChallengeSolution solution4 = new ChallengeSolution(verificationValue1, transactionID2, blockNumber1, chunkHash1);
        ChallengeSolution solution5 = new ChallengeSolution(verificationValue1, transactionID1, blockNumber2, chunkHash1);
        ChallengeSolution solution6 = new ChallengeSolution(verificationValue1, transactionID1, blockNumber1, chunkHash2);

        assertEquals(solution1, solution1);
        assertEquals(solution1, solution2);

        assertNotEquals(solution1, solution3);
        assertNotEquals(solution1, solution4);
        assertNotEquals(solution1, solution5);
        assertNotEquals(solution1, solution6);

        assertEquals(solution1.hashCode(), solution1.hashCode());
        assertEquals(solution1.hashCode(), solution2.hashCode());

        assertNotEquals(solution1.hashCode(), solution3.hashCode());
        assertNotEquals(solution1.hashCode(), solution4.hashCode());
        assertNotEquals(solution1.hashCode(), solution5.hashCode());
        assertNotEquals(solution1.hashCode(), solution6.hashCode());
    }
}
