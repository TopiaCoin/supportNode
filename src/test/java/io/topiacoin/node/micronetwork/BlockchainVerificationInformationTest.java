package io.topiacoin.node.micronetwork;

import org.junit.Test;

import static junit.framework.TestCase.*;

public class BlockchainVerificationInformationTest {


    @Test
    public void testDefaultConstrutor() throws Exception {
        BlockchainVerificationInformation bfi = new BlockchainVerificationInformation();

        assertNull(bfi.getVerificationValue());
        assertEquals(0, bfi.getBlockNumber());
        assertNull(bfi.getTransactionID());
    }

    @Test
    public void testAccessors() throws Exception {


        String verificationValue = "foo";
        long blockNumber = 0xdeadbeefL;
        String transactionID = "0x1234567890abcdef";

        BlockchainVerificationInformation bvi = new BlockchainVerificationInformation(verificationValue, blockNumber, transactionID);

        assertEquals(verificationValue, bvi.getVerificationValue());
        assertEquals(blockNumber, bvi.getBlockNumber());
        assertEquals(transactionID, bvi.getTransactionID());
    }
}
