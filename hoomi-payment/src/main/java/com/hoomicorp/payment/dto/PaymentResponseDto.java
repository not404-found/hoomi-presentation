package com.hoomicorp.payment.dto;

public class PaymentResponseDto {
    private String paymentIntentCS;

    public PaymentResponseDto() {
    }

    public PaymentResponseDto(String paymentIntentCS) {
        this.paymentIntentCS = paymentIntentCS;
    }

    public String getPaymentIntentCS() {
        return paymentIntentCS;
    }
}
