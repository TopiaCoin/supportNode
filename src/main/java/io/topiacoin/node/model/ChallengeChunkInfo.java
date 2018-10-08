package io.topiacoin.node.model;

import java.util.Objects;

public class ChallengeChunkInfo {

    private String chunkID;
    private int offset;
    private int length;

    public ChallengeChunkInfo() {
    }

    public ChallengeChunkInfo(String chunkID, int offset, int length) {
        this.chunkID = chunkID;
        this.offset = offset;
        this.length = length;
    }

    public String getChunkID() {
        return chunkID;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeChunkInfo that = (ChallengeChunkInfo) o;
        return offset == that.offset &&
                length == that.length &&
                Objects.equals(chunkID, that.chunkID);
    }

    @Override
    public int hashCode() {

        return Objects.hash(chunkID, offset, length);
    }

    @Override
    public String toString() {
        return "ChallengeChunkInfo{" +
                "chunkID='" + chunkID + '\'' +
                ", offset=" + offset +
                ", length=" + length +
                '}';
    }
}
