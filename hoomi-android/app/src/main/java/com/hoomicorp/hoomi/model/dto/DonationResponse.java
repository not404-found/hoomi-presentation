package com.hoomicorp.hoomi.model.dto;

public class DonationResponse {
    private String paymentIntentCS;

    public DonationResponse(String paymentIntentCS) {
        this.paymentIntentCS = paymentIntentCS;
    }

    public DonationResponse() {
    }

    public String getPaymentIntentCS() {
        return paymentIntentCS;
    }

    public void setPaymentIntentCS(String paymentIntentCS) {
        this.paymentIntentCS = paymentIntentCS;
    }
}
