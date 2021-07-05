package com.hoomicorp.hoomi.rtc.model;

import java.util.Objects;

public class Candidate {
    private String candidate;
    private String sdpMid;
    private Integer sdpMLineIndex;
    private boolean used = false;

    public Candidate() {
    }

    public Candidate(String candidate, String sdpMid, Integer sdpMLineIndex) {
        this.candidate = candidate;
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
    }

    public Candidate(String candidate, String sdpMid, Integer sdpMLineIndex, boolean used) {
        this.candidate = candidate;
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
        this.used = used;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public void setSdpMid(String sdpMid) {
        this.sdpMid = sdpMid;
    }

    public Integer getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public void setSdpMLineIndex(Integer sdpMLineIndex) {
        this.sdpMLineIndex = sdpMLineIndex;
    }

    public Boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate1 = (Candidate) o;
        return Objects.equals(candidate, candidate1.candidate) &&
                Objects.equals(sdpMid, candidate1.sdpMid) &&
                Objects.equals(sdpMLineIndex, candidate1.sdpMLineIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(candidate, sdpMid, sdpMLineIndex);
    }
}
