package com.hoomicorp.hoomi.mqtt;

import com.google.gson.Gson;
import com.hoomicorp.hoomi.listener.OnNewMessageArrived;
import com.hoomicorp.hoomi.model.dto.LiveChatMessageDto;
import com.hoomicorp.hoomi.model.dto.PollDto;
import com.hoomicorp.hoomi.model.dto.VoteEnum;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Objects;
import java.util.function.Consumer;

public class MqttMessageHandler implements MqttCallbackExtended {
    private final Gson jsonConverter = new Gson();

    private Consumer<MqttMessageHandler> reconnectCallback;
    private OnNewMessageArrived onNewMessageArrived;

    public MqttMessageHandler(OnNewMessageArrived onNewMessageArrived) {
        this.onNewMessageArrived = onNewMessageArrived;
    }

    public MqttMessageHandler() {
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {

        if (Objects.nonNull(this.reconnectCallback)) {
            reconnectCallback.accept(this);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        //todo log
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        byte[] payload = message.getPayload();

        if (topic.contains("live-chat")) {
            final String parsedPayload = bytesToString(payload);
            final LiveChatMessageDto messageDto = jsonConverter.fromJson(parsedPayload, LiveChatMessageDto.class);
            onNewMessageArrived.onMessage(messageDto);
            System.out.println("topic: " + topic + " payload: " + parsedPayload);

        } else if (topic.contains("poll")) {
            final String parsedPayload = bytesToString(payload);
            final PollDto pollDto = jsonConverter.fromJson(parsedPayload, PollDto.class);
            onNewMessageArrived.onPoll(pollDto);
            System.out.println("topic: " + topic + " payload: " + parsedPayload);

        } else if (topic.contains("vote")) {
            final int voteNum = bytesToInt(payload);
            final VoteEnum vote = VoteEnum.getByValue(voteNum);
            onNewMessageArrived.onVote(vote);
            System.out.println("topic: " + topic + " payload: " + voteNum);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public void setReconnectCallback(final Consumer<MqttMessageHandler> reconnectCallback) {
        this.reconnectCallback = reconnectCallback;
    }

    public String bytesToString(final byte[] val) {
        if (Objects.nonNull(val) && val.length > 0) {
            return new String(val);
        }
        return "";
    }

    public int toUnsignedBytes(byte symbol) {
        return symbol & 0xFF;
    }

    public int bytesToInt(byte... val) {
        int result = 0;
        for (int i = 0; i < val.length; i++) {
            result += toUnsignedBytes(val[i]) << (8 * (val.length - (i + 1)));
        }
        return result;
    }

    public int[] bytesToIntArray(byte... val) {
        final int[] result = new int[8 * val.length];


        for (int i = 0; i < val.length; i++) {
            int intValue = toUnsignedBytes(val[val.length - (i + 1)]);
            for (int j = 0; j < 8; j++) {
                result[i * 8 + j] = intValue & 0x01;

                intValue = intValue >> 1;

            }
        }
        return result;
    }
}
