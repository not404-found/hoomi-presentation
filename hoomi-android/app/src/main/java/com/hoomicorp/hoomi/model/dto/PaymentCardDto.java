package com.hoomicorp.hoomi.model.dto;

import com.google.gson.GsonBuilder;
import com.hoomicorp.hoomi.model.enums.PaymentCardType;

public class PaymentCardDto {
    private String cardNum;
    private PaymentCardType cardType;
    private boolean main;
    private String firstName;
    private String lastName;
    private String expDate;

    public PaymentCardDto(String cardNum, PaymentCardType cardType, boolean main, String firstName, String lastName, String expDate) {
        this.cardNum = cardNum;
        this.cardType = cardType;
        this.main = main;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expDate = expDate;
    }

    public PaymentCardDto() {
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public PaymentCardType getCardType() {
        return cardType;
    }

    public void setCardType(PaymentCardType cardType) {
        this.cardType = cardType;
    }

    public boolean isMain() {
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, PaymentCardDto.class);
    }
}
