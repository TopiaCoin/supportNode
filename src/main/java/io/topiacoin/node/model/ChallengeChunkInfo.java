package io.topiacoin.node.model;

public class ChallengeChunkInfo {

    private String chunkID;
    private int offset;
    private int length;

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
}
