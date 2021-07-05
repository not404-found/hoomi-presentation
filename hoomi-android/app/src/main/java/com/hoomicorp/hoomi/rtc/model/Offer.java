package com.hoomicorp.hoomi.rtc.model;

import com.google.gson.GsonBuilder;

public class Offer {
    private String sdp;
    private boolean used = false;

    public Offer() {
    }

    public Offer(String sdp) {
        this.sdp = sdp;
    }

    public Offer(String sdp, boolean used) {
        this.sdp = sdp;
        this.used = used;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public Boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }


    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, Offer.class);
    }
}
