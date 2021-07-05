package com.hoomicorp.hoomi.mqtt;

import com.hoomicorp.hoomi.model.UserSession;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.nio.channels.AlreadyConnectedException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class MqttClientInstance {
    private static final String CONNECTION_STRING_TEMPLATE = "tcp://%s:%s";
    private static final MqttClientInstance instance = new MqttClientInstance();
    private final AtomicBoolean isMqttInit = new AtomicBoolean(Boolean.FALSE);

    private MqttClient mqttClient;
    private MqttMessageHandler handler;
    private String topic;

    private MqttClientInstance() {
    }

    public void initMqttClient(final MqttMessageHandler handler, final String topic) throws MqttException {
        if (isMqttInit.get()) {
            throw new AlreadyConnectedException();
        }
        this.handler = handler;
        this.topic = topic;
        final String customerId = UserSession.getInstance().getId();
        final String connectionString = String.format(CONNECTION_STRING_TEMPLATE, "192.168.100.42", "1883");
        final MemoryPersistence persistence = new MemoryPersistence();
        final MqttClient mqttClient = new MqttClient(connectionString, customerId, persistence);
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

        isMqttInit.set(Boolean.TRUE);

        System.out.println("MQTT Connected");
    }


    public void sendMessage(final String topic, final byte[] payload) {
        if (isMqttInit.get()) {

            System.out.println("Sending message to topic "  + topic);
            try {
                final MqttMessage mqttMessage = new MqttMessage(payload);
                mqttClient.getTopic(topic).publish(mqttMessage);
            } catch (MqttException e) {
                e.printStackTrace();
                // TODO: log
            }
        }
    }

    public static MqttClientInstance getInstance() {
        return instance;
    }

    private Consumer<MqttMessageHandler> reconnectCallback(final org.eclipse.paho.client.mqttv3.MqttClient mqttClient, final String topic) {
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

    public void release() throws MqttException {
        handler.setReconnectCallback(null);
        mqttClient.disconnect();
        mqttClient.close();
        isMqttInit.set(Boolean.FALSE);
    }
}
