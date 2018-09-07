package io.topiacoin.node.utility;

import io.topiacoin.node.utilities.HashUtilities;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Random;

import static junit.framework.TestCase.*;

public class HashUtilitiesTest {

//    @Test
//    public void sanityTest() {
//        fail ( "I'm the sanest test ever!");
//    }

    @Test
    public void testHashGenerationAndVerificationOfByteArray() throws Exception {
        String algorithm = "SHA-256";
        byte[] data = new byte[1024] ;
        Random random = new Random();
        random.nextBytes(data);

        String dataHash = HashUtilities.generateHash(algorithm, data);
        assertNotNull ( "No Hash String was Returned", dataHash);

        boolean matches = HashUtilities.verifyHash(dataHash, data);
        assertTrue ( "Data did not match the Hash", matches);
    }

    @Test
    public void testHashGenerationAndVerificationOfDataStream() throws Exception {
        String algorithm = "SHA-256";
        byte[] data = new byte[1024] ;
        Random random = new Random();
        random.nextBytes(data);

        ByteArrayInputStream dataStream = new ByteArrayInputStream(data) ;

        String dataHash = HashUtilities.generateHash(algorithm, dataStream);
        assertNotNull ( "No Hash String was Returned", dataHash);

        dataStream.reset();
        boolean matches = HashUtilities.verifyHash(dataHash, dataStream);
        assertTrue ( "Data did not match the Hash", matches);
    }
}
