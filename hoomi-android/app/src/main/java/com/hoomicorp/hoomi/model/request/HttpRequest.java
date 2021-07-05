package com.hoomicorp.hoomi.model.request;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequest extends StringRequest {
    private Request request;
    private String token;


    public HttpRequest(int method, String url,
                       Response.Listener<String> listener,
                       @Nullable Response.ErrorListener errorListener,
                       final Request request) {
        super(method, url, listener, errorListener);
        this.request = request;
    }

    public HttpRequest(int method, String url,
                       Response.Listener<String> listener,
                       @Nullable Response.ErrorListener errorListener,
                       final Request request, String token) {
        super(method, url, listener, errorListener);
        this.request = request;
        this.token = token;
    }


    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if(Objects.isNull(request)) {
            return new byte[0];
        }

        final String body = request.toString();
        return body.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "application/json");
        if (Objects.nonNull(token)) {
            params.put("Authorization", token);
        }

        return params;
    }
}
