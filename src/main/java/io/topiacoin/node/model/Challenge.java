package io.topiacoin.node.model;

import java.util.ArrayList;
import java.util.List;

public class Challenge {
    private String containerID;
    private List<ChallengeChunkInfo> challengeChunks;

    public Challenge(String containerID, List<ChallengeChunkInfo> challengeChunks) {
        this.containerID = containerID;
        this.challengeChunks = new ArrayList<>(challengeChunks);
    }

    public String getContainerID() {
        return containerID;
    }

    public List<ChallengeChunkInfo> getChallengeChunks() {
        return challengeChunks;
    }
}
