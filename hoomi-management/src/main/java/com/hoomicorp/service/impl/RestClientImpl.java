package com.hoomicorp.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hoomicorp.service.RestClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestClientImpl implements RestClient {
    private final RestTemplate restTemplate;
    private final Gson jsonBuilder = new GsonBuilder().create();

    public RestClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public <T> T post(final String url, final Object value, final Class<T> clazz) {
        final String json = serializeJson(value);

        final HttpEntity<String> entity = new HttpEntity<>(json);

        final ResponseEntity<T> responseEntity = restTemplate.postForEntity(url, entity, clazz);
        return responseEntity.getBody();
    }

    @Override
    public <T> T put(final String url, final Object value, final Class<T> clazz) {
        final String json = serializeJson(value);

        final HttpEntity<String> entity = new HttpEntity<>(json);

        final ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, entity, clazz);
        return responseEntity.getBody();

    }

    @Override
    public <T> T get(final String url, final Class<T> clazz) {

        return restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, clazz).getBody();
    }

    private String serializeJson(Object val) {
        return  jsonBuilder.toJson(val);
    }



}
