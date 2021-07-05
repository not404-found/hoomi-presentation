package com.hoomicorp.hoomi.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.model.dto.PostDto;
import com.hoomicorp.hoomi.model.request.HttpRequest;
import com.hoomicorp.hoomi.rtc.PeerConnectionClient;
import com.hoomicorp.hoomi.rtc.PeerConnectionListener;
import com.hoomicorp.hoomi.rtc.model.Answer;
import com.hoomicorp.hoomi.rtc.model.Candidate;
import com.hoomicorp.hoomi.rtc.model.Offer;
import com.hoomicorp.hoomi.rtc.model.WebRTCSession;

import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * * create an instance of this fragment.
 */
public class InLiveFragment extends Fragment {
    public static final int SCREEN_CAPTURE_PERMISSION_REQUEST = 1;
    private Intent mMediaProjectionPermissionResultData;
    private int mMediaProjectionPermissionResultCode;


    private FirebaseFirestore firebaseFirestore;
    private DocumentReference lifeStreamsDocRef;
    private DocumentReference userInfoDocRef;
    private DocumentReference rtcDocRef;

    private PeerConnectionClient peerConnectionClient;
    private WebRTCSession webRTCSession;
    private Answer answer;
    private final Set<Candidate> serverCandidates = new HashSet<>();
    private RequestQueue requestQueue;

    private PostDto postDto;
    private CountDownTimer timer;
    private boolean isTimerStarted;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_in_live, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        requestQueue = Volley.newRequestQueue(getContext());

        View fragmentConstraintLayout = view.findViewById(R.id.in_live_fragment_container);
        TextView timeTextView = view.findViewById(R.id.in_live_fragment_time);
        ProgressBar progressBar = view.findViewById(R.id.in_live_fragment_progress_bar);

        timer = new CountDownTimer(10000, 100) {
            int i = 0;

            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(i);
                long sec = millisUntilFinished / 1000;
                timeTextView.setText( String.valueOf(sec));

                if (i == 70) {
                    startStream(webRTCSession.getId());
                }
                i++;
            }

            public void onFinish() {
                timeTextView.setText( "In Live");
                fragmentConstraintLayout.setBackgroundResource(R.drawable.green_color_corners);
                progressBar.setVisibility(View.INVISIBLE);

            }
        };

        if (Objects.nonNull(currentUser)) {

            if (Objects.nonNull(getArguments())) {
                InLiveFragmentArgs goLiveFragmentArgs = InLiveFragmentArgs.fromBundle(getArguments());
                postDto = goLiveFragmentArgs.getPostDto();
            }

            String userId = currentUser.getUid();
            webRTCSession = new WebRTCSession(userId);


            firebaseFirestore = FirebaseFirestore.getInstance();
            userInfoDocRef = firebaseFirestore.collection("user-info").document(userId);
            lifeStreamsDocRef = firebaseFirestore.collection("live-streams").document(userId);
            rtcDocRef = firebaseFirestore.collection("rtc").document(webRTCSession.getId());

            rtcDocRef.set(webRTCSession).addOnSuccessListener(aVoid -> {
                rtcDocRef.addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w("RTC LISTENER", "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        final WebRTCSession webRTCSession = snapshot.toObject(WebRTCSession.class);
                        final Answer answer = webRTCSession.getAnswer();
                        final List<Candidate> serverCandidates = webRTCSession.getServerCandidates();

                        if (Objects.nonNull(answer) && Objects.isNull(InLiveFragment.this.answer)) {
                            Log.d("RTC LISTENER", "Answer " + answer);
                            InLiveFragment.this.answer = answer;
                            peerConnectionClient.addRemoteDescription(answer.getSdp());
//                        answer.setUsed(true);
//                        rtcRefference.update("answer", answer);
                        }

                        if (Objects.nonNull(serverCandidates) && !serverCandidates.isEmpty()) {
                            serverCandidates.forEach(c -> {

                                if (!InLiveFragment.this.serverCandidates.contains(c)) {
                                    Log.d("RTC LISTENER", "Ice " + c);
                                    peerConnectionClient.addIceCandidate(c);
                                    c.setUsed(true);
                                    InLiveFragment.this.serverCandidates.add(c);
                                }
                            });
                        }
                    } else {
                        Log.d("RTC LISTENER", "Current data: null");
                    }
                });
            });


            startWebRTC();

        }


        return view;
    }

    public void startWebRTC() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), SCREEN_CAPTURE_PERMISSION_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != SCREEN_CAPTURE_PERMISSION_REQUEST) {
            return;
        }
        mMediaProjectionPermissionResultCode = resultCode;
        mMediaProjectionPermissionResultData = data;


        InLiveFragment.WebRtcListener webRtcListener = new InLiveFragment.WebRtcListener();
        peerConnectionClient = new PeerConnectionClient(webRtcListener, getContext(), true, mMediaProjectionPermissionResultData);
        peerConnectionClient.initPeerConnection();
        peerConnectionClient.startStream(720, 480, 30);

        startListenStream(webRTCSession.getId());


    }

    private void startListenStream(String userId) {
//        http://54.93.93.114:8080/hoomi/streaming/controller/api/v1/stream/start/
        final HttpRequest registerReq = new HttpRequest(Request.Method.GET,
                "http://54.93.93.114:8080/hoomi/streaming/controller/api/v1/stream/listen/" + userId,
                token -> {
                    System.out.println("success");
                },
                error -> {
                    error.printStackTrace();
                }, null, null);

        requestQueue.add(registerReq);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startStream(String userId) {
        final HttpRequest registerReq = new HttpRequest(Request.Method.GET,
                "http://54.93.93.114:8080/hoomi/streaming/controller/api/v1/stream/start/" + userId,
                streamLink -> {
                    System.out.println("success: " + streamLink);
                    postDto.setLivesStreamLink(streamLink);
                    postDto.setUpdatedDateTime(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

                    lifeStreamsDocRef.set(postDto).addOnSuccessListener(aVoid -> {
                        //todo handle when we save to livestreams
                        System.out.println("Successfully saved to live streams");
                    }).addOnFailureListener(err -> {
                        //todo handle err when we cant save to livestreams
                        err.printStackTrace();
                    });
                },
                error -> {
                    //todo handle err when we have err response
                    error.printStackTrace();
                }, null, null);

        requestQueue.add(registerReq);
    }


    /**
     * WebRTC listener
     */
    public final class WebRtcListener extends PeerConnectionListener {

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            final Candidate candidate = new Candidate(iceCandidate.sdp, iceCandidate.sdpMid, iceCandidate.sdpMLineIndex);

            rtcDocRef.update("clientCandidates", FieldValue.arrayUnion(candidate));
        }

        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            peerConnectionClient.addLocalDescription(this, sessionDescription);

            final Offer offer = new Offer(sessionDescription.description);
            rtcDocRef.update("offer", offer);
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            System.out.println("onIceGatheringChange************************************" + iceGatheringState);
            if (Objects.equals(iceGatheringState, PeerConnection.IceGatheringState.COMPLETE)) {


//                try {
//                    Thread.sleep(5000);
//                    startStream(webRTCSession.getId());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                System.out.println("COMPLETE************************************");
            }
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            System.out.println("onIceConnectionChange************************************" + iceConnectionState);
            if (Objects.equals(iceConnectionState, PeerConnection.IceConnectionState.CONNECTED)) {

                if (!isTimerStarted) {
                    timer.start();
                    isTimerStarted = true;
                }

            }
        }

        @Override
        public void onStandardizedIceConnectionChange(PeerConnection.IceConnectionState newState) {
            System.out.println("onStandardizedIceConnectionChange************************************" + newState);
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            System.out.println("onIceConnectionReceivingChange************************************" + b);
        }
    }
}