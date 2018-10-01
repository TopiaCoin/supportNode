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
}
