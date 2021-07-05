package com.hoomicorp.hoomi.mqtt;

import com.hoomicorp.hoomi.model.UserSession;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Objects;
import java.util.function.Consumer;

public final class MqttClient {
    private static final String CONNECTION_STRING_TEMPLATE = "tcp://%s:%s";
    private final org.eclipse.paho.client.mqttv3.MqttClient mqttClient;
    private final MqttMessageHandler handler;
    private final String topic;

    // used for message exchange with mqtt protocol. MQTT - IOT
    public MqttClient(final MqttMessageHandler handler, final String topic) throws MqttException {
        this.handler = handler;
        this.topic = topic;
        final String customerId = UserSession.getInstance().getId();
        final String connectionString = String.format(CONNECTION_STRING_TEMPLATE, "192.168.100.42", "1883");
        final MemoryPersistence persistence = new MemoryPersistence();
        final org.eclipse.paho.client.mqttv3.MqttClient mqttClient = new org.eclipse.paho.client.mqttv3.MqttClient(connectionString, customerId, persistence);
        final MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setUserName("guest");
        connOpts.setPassword("guest".toCharArray());
        connOpts.setAutomaticReconnect(true);
        connOpts.setKeepAliveInterval(60);
        connOpts.setConnectionTimeout(60);
        connOpts.setMaxInflight(10_000);
        mqttClient.connect(connOpts);
        mqttClient.setCallback(handler);
        mqttClient.subscribe(topic + "/#");

        final Consumer<MqttMessageHandler> reconnectCallback = reconnectCallback(mqttClient, topic);
        handler.setReconnectCallback(reconnectCallback);
        this.mqttClient = mqttClient;

        System.out.println("&&&&&&&&&&&&&&&&&&&&MQTT&&&&&&&&&&&&&&&&&&&&&");
    }

    public void release() throws MqttException {
        handler.setReconnectCallback(null);
        mqttClient.disconnect();
        mqttClient.close();
    }


    public void sendMessage(final byte[] payload) {
        if (Objects.nonNull(mqttClient)) {
            try {
                final MqttMessage mqttMessage = new MqttMessage(payload);
                mqttClient.getTopic(topic).publish(mqttMessage);
            } catch (MqttException e) {
                // TODO: log
            }
        }
    }



    private static Consumer<MqttMessageHandler> reconnectCallback(final org.eclipse.paho.client.mqttv3.MqttClient mqttClient, final String topic) {
        return mqttMessageHandler -> {
            try {
                mqttClient.subscribe(topic);

            } catch (MqttException e) {
                //todo log
                e.printStackTrace();
//                logger.error("[Mqtt Client] Can't subscribe to topic, Reason: {}", e.getMessage());
            }
        };
    }
}
