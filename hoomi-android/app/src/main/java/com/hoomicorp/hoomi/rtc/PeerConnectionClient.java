package com.hoomicorp.hoomi.rtc;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.hoomicorp.hoomi.rtc.PeerConnectionListener;
import com.hoomicorp.hoomi.rtc.model.Candidate;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RTCStats;
import org.webrtc.RTCStatsCollectorCallback;
import org.webrtc.RTCStatsReport;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PeerConnectionClient {

    private PeerConnectionListener peerConnectionListener;

    private EglBase.Context eglBaseContext;
    private Context applicationContext;
    private PeerConnectionFactory peerConnectionFactory;
    private org.webrtc.PeerConnection peerConnection;


    private VideoCapturer videoCapturer;
    private VideoEncoderFactory encoderFactory;
    private VideoDecoderFactory decoderFactory;


    public PeerConnectionClient(final PeerConnectionListener peerConnectionListener, final Context applicationContext, final boolean isScreenShare, final Intent mediaProjectionResultData) {
        this.peerConnectionListener = peerConnectionListener;
        this.applicationContext = applicationContext;
        initCodec();
        initPeerConnectionFactory();
        if (isScreenShare) {
            this.videoCapturer =  new CameraManager(applicationContext).createScreenCapturer(mediaProjectionResultData);
        } else {
            this.videoCapturer = new CameraManager(applicationContext).createCameraCapturer(true);
        }

    }


    private void initPeerConnectionFactory() {
        final AudioManager audioManager = new AudioManager(applicationContext);
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions
                .builder(applicationContext)
                .createInitializationOptions());
        this.peerConnectionFactory
                = PeerConnectionFactory.builder()
                .setAudioDeviceModule(audioManager.createAudioDeviceModule())
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
    }

    private void initCodec() {
        this.eglBaseContext = EglBaseUtil.getRootEglBaseContext();

        if (eglBaseContext != null) {
            encoderFactory
                    = new DefaultVideoEncoderFactory(
                    eglBaseContext,
                    /* enableIntelVp8Encoder */ true,
                    /* enableH264HighProfile */ false);
            decoderFactory = new DefaultVideoDecoderFactory(eglBaseContext);
        } else {
            encoderFactory = new SoftwareVideoEncoderFactory();
            decoderFactory = new SoftwareVideoDecoderFactory();
        }
    }


    //2
    public void initPeerConnection() {

        final List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(org.webrtc.PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
        iceServers.add(org.webrtc.PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer());
        iceServers.add(org.webrtc.PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer());
        iceServers.add(org.webrtc.PeerConnection.IceServer.builder("stun:stun3.l.google.com:19302").createIceServer());
        iceServers.add(org.webrtc.PeerConnection.IceServer.builder("stun:stun4.l.google.com:19302").createIceServer());
//        iceServers.add(org.webrtc.PeerConnection.IceServer.builder("turn:34.251.200.213:3478").setUsername("kurento")
//                .setPassword("kurento").createIceServer());

        this.peerConnection = peerConnectionFactory.createPeerConnection(iceServers, peerConnectionListener);

//        this.peerConnection.setBitrate(250, 400, 700);
        this.peerConnection.addStream(initLocalMediaStream());

        MediaConstraints constraints = new MediaConstraints();
        constraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        constraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        this.peerConnection.createOffer(peerConnectionListener, constraints);

    }

    public void getStats() {
        this.peerConnection.getStats(rtcStatsReport -> {
            System.out.println("Receiving stats " + rtcStatsReport.toString());
            Map<String, RTCStats> statsMap = rtcStatsReport.getStatsMap();
            statsMap.forEach((k, v) -> System.out.println("K: " + k + " V: " + v.toString()));
        });
    }

    public MediaStream initLocalMediaStream() {

        VideoTrack localVideoTrack = createLocalVideoTrack();
        AudioTrack localAudioTrack = createAudioTrack();

        MediaStream localMediaStream = peerConnectionFactory.createLocalMediaStream(UUID.randomUUID().toString() + "mediaStreamLocal");

        for (MediaStreamTrack track : new MediaStreamTrack[]{localAudioTrack, localVideoTrack}) {
            if (track == null) {
                continue;
            }

            if (track instanceof AudioTrack) {
                localMediaStream.addTrack((AudioTrack) track);
                Log.i("[PC]", "Local audio exists");
            } else {
                localMediaStream.addTrack((VideoTrack) track);
                Log.i("[PC]", "Local audio exists");
            }
        }


        return localMediaStream;
    }


    private VideoTrack createLocalVideoTrack() {
        if (videoCapturer == null) {
            Log.e("[PC]", "NO VIDEO CAPTURER");
            return null;
        }


        SurfaceTextureHelper surfaceTextureHelper =
                SurfaceTextureHelper.create("CaptureThread", eglBaseContext);

        if (surfaceTextureHelper == null) {
            Log.d("[PC]", "Error creating SurfaceTextureHelper");
            return null;
        }

        VideoSource videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, applicationContext, videoSource.getCapturerObserver());

        String id = UUID.randomUUID().toString();
        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack(id, videoSource);

        localVideoTrack.setEnabled(true);

        return localVideoTrack;
    }

    private AudioTrack createAudioTrack() {
        String id = UUID.randomUUID().toString();

        AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        return peerConnectionFactory.createAudioTrack(id, audioSource);
    }

    //int width, int height, int framerate
    public void startStream(int width, int high, int fps) {
        videoCapturer.startCapture(width, high, fps);
    }


    public void resumeStream() {
        videoCapturer.startCapture(480, 640, 30);
    }


    public void addLocalDescription(final SdpObserver observer, final SessionDescription sessionDescription) {
        peerConnection.setLocalDescription(observer, sessionDescription);
    }

    public void addRemoteDescription(final String answer) {
        final SessionDescription sdp = new SessionDescription(SessionDescription.Type.ANSWER, answer);
        this.peerConnection.setRemoteDescription(peerConnectionListener, sdp);
    }

    public void addIceCandidate(final Candidate candidate) {
        final IceCandidate ice = new IceCandidate("", candidate.getSdpMLineIndex(), candidate.getCandidate());
        this.peerConnection.addIceCandidate(ice);
    }
}
