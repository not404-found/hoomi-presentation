package com.hoomicorp.service;

public interface RestClient {

    <T> T post(final String url, final Object value, final Class<T> clazz);

    <T> T put(final String url, final Object value, final Class<T> clazz);

    <T> T get(final String url, final Class<T> clazz);

}
