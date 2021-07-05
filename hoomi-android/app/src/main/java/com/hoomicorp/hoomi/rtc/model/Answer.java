package com.hoomicorp.hoomi.rtc.model;

import com.google.gson.GsonBuilder;

public class Answer {
    private String sdp;
    private boolean used = false;

    public Answer() {
    }

    public Answer(String sdp) {
        this.sdp = sdp;
    }

    public Answer(String sdp, Boolean used) {
        this.sdp = sdp;
        this.used = used;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public Boolean istUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }


    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, Answer.class);
    }
}
