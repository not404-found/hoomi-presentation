package com.hoomicorp.payment.controller;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.hoomicorp.payment.dto.PaymentRequestDto;
import com.hoomicorp.payment.dto.PaymentResponseDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/charge")
@Api(value = "/api/v1/charge", description = "Payments API")
public class PaymentsController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentsController.class);

    @Value("${stripe.key.public}")
    private String stripePublicKey;
    @Value("${stripe.key.private}")
    private String stripePrivateKey;

    @PostMapping("/create-payment")
    public PaymentResponseDto createPayment(@RequestBody final PaymentRequestDto requestDto) throws StripeException {
        Stripe.apiKey = stripePrivateKey;

        final PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                .setCurrency("usd")
                .setAmount((long) requestDto.getAmount())
                .build();
        // Create a PaymentIntent with the order amount and currency
        final PaymentIntent intent = PaymentIntent.create(createParams);

        return new PaymentResponseDto(intent.getClientSecret());
    }

}
