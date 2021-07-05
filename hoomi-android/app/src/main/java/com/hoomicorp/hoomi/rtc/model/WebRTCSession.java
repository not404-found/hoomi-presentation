package com.hoomicorp.hoomi.rtc.model;

import com.google.gson.GsonBuilder;

import java.util.List;

public class WebRTCSession {
    private String id;
    private Offer offer;
    private Answer answer;
    private List<Candidate> clientCandidates;
    private List<Candidate> serverCandidates;

    public WebRTCSession() {
    }

    public WebRTCSession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public List<Candidate> getClientCandidates() {
        return clientCandidates;
    }

    public void setClientCandidates(List<Candidate> clientCandidates) {
        this.clientCandidates = clientCandidates;
    }

    public List<Candidate> getServerCandidates() {
        return serverCandidates;
    }

    public void setServerCandidates(List<Candidate> serverCandidates) {
        this.serverCandidates = serverCandidates;
    }

    @Override
    public String toString() {
        return "WebRTCSession{" +
                "id='" + id + '\'' +
                ", clientCandidates=" + clientCandidates +
                ", serverCandidates=" + serverCandidates +
                '}';
    }

    //    @Override
//    public String toString() {
//        return new GsonBuilder().create().toJson(this, WebRTCSession.class);
//    }
}
