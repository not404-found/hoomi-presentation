package com.hoomicorp.hoomi.rtc;

import android.util.Log;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class PeerConnectionListener implements org.webrtc.PeerConnection.Observer, SdpObserver {

    private void log(String s) {
        Log.i("[PEER CONNECTION LISTENER] ", s);
    }

    @Override
    public void onSignalingChange(org.webrtc.PeerConnection.SignalingState signalingState) {
        log("[Signaling state change] " + signalingState);
    }

    @Override
    public void onIceConnectionChange(org.webrtc.PeerConnection.IceConnectionState iceConnectionState) {
        log("[Ice connection state change] " + iceConnectionState);
    }

    @Override
    public void onStandardizedIceConnectionChange(org.webrtc.PeerConnection.IceConnectionState newState) {
        log("[Standardized Ice connection state change] " + newState);
    }

    @Override
    public void onConnectionChange(org.webrtc.PeerConnection.PeerConnectionState newState) {
        log("[Connection state change] " + newState);
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        log("[Ice connection receiving change] " + b);

    }

    @Override
    public void onIceGatheringChange(org.webrtc.PeerConnection.IceGatheringState iceGatheringState) {
        log("[Ice gathering state change] " + iceGatheringState);
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        log("[Ice candidate] " + iceCandidate);
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        log("[Ice candidates removed ]" + iceCandidates.length);
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        log("[Received remote initLocalMediaStream]");
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        log("[Remove media initLocalMediaStream]");
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        log("[On data channel]");
    }

    @Override
    public void onRenegotiationNeeded() {
        log("[On negotiation neede]");
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        log("[On add track] " + mediaStreams.length);
    }

    @Override
    public void onTrack(RtpTransceiver transceiver) {
        log("[On track]");
    }

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        final String description = sessionDescription.description;

        log("[Offer created successfully] " + description);
    }

    @Override
    public void onSetSuccess() {
        log("[Offer set successfully] ");
    }

    @Override
    public void onCreateFailure(String s) {
        log("[Offer could not created] " + s);
    }

    @Override
    public void onSetFailure(String s) {
        log("[Offer could not set] " + s);
    }
}

