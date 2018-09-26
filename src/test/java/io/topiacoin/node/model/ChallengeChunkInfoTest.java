package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChallengeChunkInfoTest {

    @Test
    public void testAccessors() {
        String chunkID = "foo-bar-baz";
        int offset = 664735;
        int length= 94723;

        ChallengeChunkInfo info = new ChallengeChunkInfo(chunkID, offset, length);

        assertEquals ( chunkID, info.getChunkID()) ;
        assertEquals( offset, info.getOffset());
        assertEquals(length, info.getLength());
    }
}
