package com.hoomicorp.hoomi.rtc;

import android.util.Log;


import com.hoomicorp.hoomi.rtc.model.Action;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.util.Objects;

public class WebsocketClient {
    private static WebsocketClient instance;

    private final WebSocketFactory factory = new WebSocketFactory();
    private WebSocketAdapter listener;

    private WebSocket ws;

//    private WebsocketClient() {
//
//        try {
//            ws = factory.createSocket("ws://localhost:8080/hoomi/streaming/controller/stream");
//            ws = ws.connect();
//        } catch (Exception e) {
//            Log.e("[WS]", "Could not establish web socket connection: " + e.getMessage());
//        }    }

    public WebsocketClient(final WebSocketAdapter listener) {
        this.listener = listener;
        try {
            ws = factory.createSocket("ws://192.168.100.42:8080/hoomi/streaming/controller/stream");
            ws.addListener(this.listener);
            ws = ws.connect();
        } catch (Exception e) {
           Log.e("[WS]", "Could not establish web socket connection: " + e.getMessage());
        }
    }

//    public static synchronized WebsocketClient getInstance() {
//        if (instance == null) {
//            instance = new WebsocketClient();
//        }
//
//        return instance;
//    }

    public void setListener(WebSocketAdapter listener) {
        this.listener = listener;
        ws.addListener(listener);
    }

    public void stop() {
        if (ws != null) {
            ws.disconnect();
            ws = null;
        }
    }


    public boolean isClosed () {
        if (Objects.isNull(ws) || !ws.isOpen()) {
            return true;
        }

        return false;
    }

    public void sendMessage(final Action action) {
//        if ( ((LiveStreamActivity.WebsocketListener)listener).isConnectionFailed() || Objects.isNull(ws)) {
//            Log.i("[WS]", "Could not sent message cause of ws is null or closed");
//            return;
//        }
        Log.i("[WS]", "Sending message: " + action);

        ws.sendText(action.toString());
    }

}
