package com.hoomicorp.payment.dto;


public class PaymentRequestDto {
    private Currency currency;
    private int amount;

    public PaymentRequestDto() {
    }

    public PaymentRequestDto(Currency currency, int amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
