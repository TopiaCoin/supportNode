package io.topiacoin.node.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ChallengeTest {

    @Test
    public void testAccessors() throws Exception {

        String containerID = "whizzbang";
        List<ChallengeChunkInfo> challengeChunks = new ArrayList<>();

        Challenge challenge = new Challenge(containerID, challengeChunks) ;

        assertEquals(containerID, challenge.getContainerID());
        assertEquals(challengeChunks, challenge.getChunkRanges());
    }

    @Test
    public void testEqualsAndHashcode() throws Exception {
        String containerID1 = "foo";
        String containerID2 = "bar";


        String chunkID1 = "chunk1";
        ChallengeChunkInfo challengeChunkInfo1 = new ChallengeChunkInfo(chunkID1, 0, 100);
        List<ChallengeChunkInfo> challengeChunks1 = new ArrayList<>();
        challengeChunks1.add(challengeChunkInfo1);

        String chunkID2 = "chunk2";
        ChallengeChunkInfo challengeChunkInfo2 = new ChallengeChunkInfo(chunkID2, 0, 100);
        List<ChallengeChunkInfo> challengeChunks2 = new ArrayList<>();
        challengeChunks2.add(challengeChunkInfo2);

        Challenge challenge1 = new Challenge(containerID1, challengeChunks1);
        Challenge challenge2 = new Challenge(containerID1, challengeChunks1);
        Challenge challenge3 = new Challenge(containerID2, challengeChunks1);
        Challenge challenge4 = new Challenge(containerID1, challengeChunks2);
        Challenge challenge5 = new Challenge(containerID2, challengeChunks2);

        assertEquals ( challenge1, challenge1) ;
        assertEquals(challenge1, challenge2);

        assertNotEquals(challenge1, challenge3);
        assertNotEquals(challenge1, challenge4);
        assertNotEquals(challenge1, challenge5);

        assertEquals( challenge1.hashCode(), challenge1.hashCode());
        assertEquals( challenge1.hashCode(), challenge2.hashCode());

        assertNotEquals(challenge1.hashCode(), challenge3.hashCode());
        assertNotEquals(challenge1.hashCode(), challenge4.hashCode());
        assertNotEquals(challenge1.hashCode(), challenge5.hashCode());
    }

    @Test
    public void testDefaultConstructor() throws Exception {
        Challenge challenge = new Challenge();

        assertNull(challenge.getContainerID());
        assertNull(challenge.getChunkRanges());
    }
}
