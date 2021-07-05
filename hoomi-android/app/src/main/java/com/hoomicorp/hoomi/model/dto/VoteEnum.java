package com.hoomicorp.hoomi.model.dto;

public enum VoteEnum {
    LEFT(0), RIGHT(1);

    private final int voteNum;

    VoteEnum(int voteNum) {
        this.voteNum = voteNum;
    }

    public static VoteEnum getByValue(final int voteNum) {
        byte a = 1;
        if (voteNum > 1 || voteNum < 0) {
            throw new IllegalArgumentException("Incorrect vote number. Can be 1 or 2");
        }
        if (voteNum == 0) {
            return LEFT;
        } else {
            return RIGHT;
        }
    }

    public byte getVoteNum() {
        return (byte) voteNum;
    }
}
