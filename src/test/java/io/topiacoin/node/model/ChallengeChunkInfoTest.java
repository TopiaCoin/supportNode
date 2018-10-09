package io.topiacoin.node.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChallengeChunkInfoTest {

    @Test
    public void testAccessors() {
        String chunkID = "foo-bar-baz";
        int offset = 664735;
        int length = 94723;

        ChallengeChunkInfo info = new ChallengeChunkInfo(chunkID, offset, length);

        assertEquals(chunkID, info.getChunkID());
        assertEquals(offset, info.getOffset());
        assertEquals(length, info.getLength());
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {
        String chunkID1 = "chunk1";
        String chunkID2 = "chunk2";

        int offset1 = 1234;
        int offset2 = 2345;

        int length1 = 100;
        int length2 = 200;

        ChallengeChunkInfo info1 = new ChallengeChunkInfo(chunkID1, offset1, length1);
        ChallengeChunkInfo info2 = new ChallengeChunkInfo(chunkID1, offset1, length1);

        ChallengeChunkInfo info3 = new ChallengeChunkInfo(chunkID2, offset1, length1);
        ChallengeChunkInfo info4 = new ChallengeChunkInfo(chunkID1, offset2, length1);
        ChallengeChunkInfo info5 = new ChallengeChunkInfo(chunkID1, offset1, length2);

        assertEquals(info1, info1);
        assertEquals(info1, info2);

        assertNotEquals(info1, info3);
        assertNotEquals(info1, info4);
        assertNotEquals(info1, info5);

        assertEquals(info1.hashCode(), info1.hashCode());
        assertEquals(info1.hashCode(), info2.hashCode());

        assertNotEquals(info1.hashCode(), info3.hashCode());
        assertNotEquals(info1.hashCode(), info4.hashCode());
        assertNotEquals(info1.hashCode(), info5.hashCode());
    }

    @Test
    public void testDefaultConstructor() throws Exception {
        ChallengeChunkInfo info = new ChallengeChunkInfo();

        assertNull(info.getChunkID());
        assertEquals(0, info.getOffset());
        assertEquals(0, info.getLength());
    }
}
