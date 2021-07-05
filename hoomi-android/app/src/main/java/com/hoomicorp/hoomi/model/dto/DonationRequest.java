package com.hoomicorp.hoomi.model.dto;

import com.google.gson.GsonBuilder;
import com.hoomicorp.hoomi.model.enums.Currency;
import com.hoomicorp.hoomi.model.request.Request;

public class DonationRequest implements Request {
    private final Currency currency;
    private final int amount;

    public DonationRequest(Currency currency, int amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, DonationRequest.class);
    }
}
