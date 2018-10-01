package io.topiacoin.node.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Challenge {
    private String containerID;
    private List<ChallengeChunkInfo> chunkRanges;

    public Challenge() {
    }

    public Challenge(String containerID, List<ChallengeChunkInfo> challengeChunks) {
        this.containerID = containerID;
        this.chunkRanges = new ArrayList<>(challengeChunks);
    }

    public String getContainerID() {
        return containerID;
    }

    public List<ChallengeChunkInfo> getChunkRanges() {
        return chunkRanges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Challenge challenge = (Challenge) o;
        return Objects.equals(containerID, challenge.containerID) &&
                Objects.equals(chunkRanges, challenge.chunkRanges);
    }

    @Override
    public int hashCode() {

        return Objects.hash(containerID, chunkRanges);
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "containerID='" + containerID + '\'' +
                ", chunkRanges=" + chunkRanges +
                '}';
    }
}
