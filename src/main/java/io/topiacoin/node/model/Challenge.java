package io.topiacoin.node.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return Objects.equals(containerID, challenge.containerID) &&
                Objects.equals(challengeChunks, challenge.challengeChunks);
    }

    @Override
    public int hashCode() {

        return Objects.hash(containerID, challengeChunks);
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "containerID='" + containerID + '\'' +
                ", challengeChunks=" + challengeChunks +
                '}';
    }
}
