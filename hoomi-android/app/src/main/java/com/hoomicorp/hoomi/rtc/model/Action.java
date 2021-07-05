package com.hoomicorp.hoomi.rtc.model;

import com.google.gson.GsonBuilder;

public class Action {

    private ActionType type;
    private ActionBody body;

    public Action() {
    }

    public Action(ActionType type) {
        this.type = type;
    }

    public Action(ActionType type, ActionBody body) {
        this.type = type;
        this.body = body;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public ActionBody getBody() {
        return body;
    }

    public void setBody(ActionBody body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, Action.class);
    }
}
