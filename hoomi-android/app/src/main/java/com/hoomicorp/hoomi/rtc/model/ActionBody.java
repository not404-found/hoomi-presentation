package com.hoomicorp.hoomi.rtc.model;

import com.google.gson.GsonBuilder;

public class ActionBody {
    //ws id
    private String id;
    private String offer;
    private String answer;
    private String rtmpLink;
    private Candidate candidate;
    private StreamType connectionType;

    /*default*/ ActionBody(final Builder builder) {
        this.id = builder.id;
        this.offer = builder.offer;
        this.answer = builder.answer;
        this.candidate = builder.candidate;
        this.connectionType = builder.connectionType;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getRtmpLink() {
        return rtmpLink;
    }

    public void setRtmpLink(String rtmpLink) {
        this.rtmpLink = rtmpLink;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public StreamType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(StreamType connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, ActionBody.class);
    }

    public static final class Builder {

        private String id;
        private String offer;
        private String answer;
        private Candidate candidate;
        private StreamType connectionType;

        /* default */ Builder() {
        }

        public Builder withId(final String id) {
            this.id = id;
            return this;
        }


        public Builder withOffer(final String offer) {
            this.offer = offer;
            return this;
        }

        public Builder withAnswer(final String answer) {
            this.answer = answer;
            return this;
        }

        public Builder withConnectionType(final StreamType connectionType) {
            this.connectionType = connectionType;
            return this;
        }

        public Builder withIceCandidate(final Candidate candidate) {
            this.candidate = candidate;
            return this;
        }

        public ActionBody build() {
            return new ActionBody(this);
        }

    }
}
