package com.hoomicorp.hoomi.model.dto;

import com.google.gson.GsonBuilder;

import java.util.Objects;

public class PollDto {
    private String question;
    private String leftVote;
    private int leftVotePrice;
    private String rightVote;
    private int rightVotePrice;

    public PollDto() {
    }

    public PollDto(String question, String leftVote, int leftVotePrice, String rightVote, int rightVotePrice) {
        this.question = question;
        this.leftVote = leftVote;
        this.leftVotePrice = leftVotePrice;
        this.rightVote = rightVote;
        this.rightVotePrice = rightVotePrice;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getLeftVote() {
        return leftVote;
    }

    public void setLeftVote(String leftVote) {
        this.leftVote = leftVote;
    }

    public int getLeftVotePrice() {
        return leftVotePrice;
    }

    public void setLeftVotePrice(int leftVotePrice) {
        this.leftVotePrice = leftVotePrice;
    }

    public String getRightVote() {
        return rightVote;
    }

    public void setRightVote(String rightVote) {
        this.rightVote = rightVote;
    }

    public int getRightVotePrice() {
        return rightVotePrice;
    }

    public void setRightVotePrice(int rightVotePrice) {
        this.rightVotePrice = rightVotePrice;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, PollDto.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PollDto pollDto = (PollDto) o;
        return leftVotePrice == pollDto.leftVotePrice &&
                rightVotePrice == pollDto.rightVotePrice &&
                Objects.equals(question, pollDto.question) &&
                Objects.equals(leftVote, pollDto.leftVote) &&
                Objects.equals(rightVote, pollDto.rightVote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, leftVote, leftVotePrice, rightVote, rightVotePrice);
    }
}
