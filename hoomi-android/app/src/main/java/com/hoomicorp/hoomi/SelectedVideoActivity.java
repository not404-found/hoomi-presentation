package com.hoomicorp.hoomi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PictureInPictureParams;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hoomicorp.hoomi.model.UserSession;
import com.hoomicorp.hoomi.model.dto.PostDto;
import com.hoomicorp.hoomi.model.dto.UserInfoDto;

import java.util.Objects;
import java.util.UUID;

public class SelectedVideoActivity extends AppCompatActivity {

    private ImageView notificationIV;
    private ImageView thumbDownIV;
    private ImageView thumbUPIV;
    private ImageView saveIV;
    private ImageView shareIV;

    private FrameLayout arrowDown;
    private FrameLayout pip;

    private PlayerView videoPlayer;

    private boolean liked = false;
    private boolean disliked = false;

    private DocumentReference userActionDocRef;
    private DocumentReference liveStreamDocRef;
    private DocumentReference userInfoDocRef;

    private final UserSession userSession = UserSession.getInstance();

    private PostDto postDto;
    private UserInfoDto userAction;

    private TextView saveTextView;

    private PictureInPictureParams.Builder pictureInPictureParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_video);

        // input args
        String streamUrl = getIntent().getStringExtra("streamUrl");
        String postId = getIntent().getStringExtra("postId");

        //firebase setup
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        final String uid = userSession.getId();
        //docs setup
        userInfoDocRef = firebaseFirestore.collection("user-info").document(uid);
        liveStreamDocRef = firebaseFirestore.collection("live-streams").document(postId); //TODO should be videos
        userActionDocRef = liveStreamDocRef.collection("user-action").document(uid);

        // views init
        videoPlayer = findViewById(R.id.selected_stream_activity_stream_video_player);
        ImageView donateIV = findViewById(R.id.selected_stream_activity_donate_icon);
        notificationIV = findViewById(R.id.selected_stream_notification_off_icon);
        thumbDownIV = findViewById(R.id.selected_stream_activity_thumb_down_icon);
        thumbUPIV = findViewById(R.id.selected_stream_activity_thumb_up_icon);
        saveIV = findViewById(R.id.selected_stream_activity_save_to_playlist_icon);
        saveTextView = findViewById(R.id.selected_stream_activity_save_text_view);
        Button followingBtn = findViewById(R.id.selected_stream_activity_follow_btn);
        EditText messageInputET = findViewById(R.id.selected_stream_activity_chat_input_msg);
        Button messageSendBtn = findViewById(R.id.selected_stream_activity_chat_send_msg);

        // check if video is liked
        userActionDocRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                userAction = document.toObject(UserInfoDto.class);

                                liked = userAction.isLiked();
                                disliked = userAction.isDisliked();

                                if (disliked) {
                                    thumbDownIV.setImageResource(R.drawable.ic_thumb_down_fill_yellow);
                                } else if (SelectedVideoActivity.this.liked) {
                                    thumbUPIV.setImageResource(R.drawable.ic_thumb_up_fill_yellow);
                                }

                                Log.d("[USER ACTION]", "DATA: " + document.getData());
                            } else {
                                Log.d("[USER ACTION]", "No such document " + document.getId());
                                initUserInfo();
                            }
                        } else {
                            Log.d("[USER ACTION]", "Failed to get user action " + uid + " due to", task.getException());
                        }
                    }
                });


        // exo player setup

        SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        videoPlayer.setPlayer(simpleExoPlayer);
        DefaultDataSourceFactory dataSourceFactory =
                new DefaultDataSourceFactory(this
                        , Util.getUserAgent(this, UUID.randomUUID().toString()));
        MediaSource mediaSource = buildMediaSource(Uri.parse(streamUrl), dataSourceFactory);
//        MediaSource mediaSource = buildMediaSource(Uri.parse("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"), dataSourceFactory);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);

        //init controller
        PlayerControlView controlView = videoPlayer.findViewById(R.id.exo_controller);
        arrowDown = controlView.findViewById(R.id.arrow_down);
        pip = controlView.findViewById(R.id.pip);

        arrowDown.setVisibility(View.VISIBLE);
        pip.setVisibility(View.VISIBLE);


        //pip init
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pictureInPictureParams = new PictureInPictureParams.Builder();
        }

        pip.setOnClickListener(v -> {
            pipMode();
        });

        arrowDown.setOnClickListener(v -> {
            pipMode();
        });

        // notification image view setup
        notificationIV.setOnClickListener(view -> {

            notificationIV.setImageResource(R.drawable.ic_notification_fill_yellow);
        });

        //save image view setup

        saveIV.setOnClickListener(view -> {

            saveIV.setImageResource(R.drawable.ic_add_box_fill_yellow);
            saveTextView.setText("Saved");
        });


        // thumb up btn setup
        thumbUPIV.setOnClickListener(view -> {

            if (this.liked) {

                thumbUPIV.setImageResource(R.drawable.ic_thumb_up_white);

                this.liked = false;

            } else {

                thumbDownIV.setImageResource(R.drawable.ic_thumb_down_white);
                thumbUPIV.setImageResource(R.drawable.ic_thumb_up_fill_yellow);
                this.liked = true;
                disliked = false;
            }

        });


        //thumb down btn setup
        thumbDownIV.setOnClickListener(view -> {

            if (disliked) {
                thumbDownIV.setImageResource(R.drawable.ic_thumb_down_white);
                disliked = false;

            } else {

                disliked = true;
                this.liked = false;


                thumbUPIV.setImageResource(R.drawable.ic_thumb_up_white);
                thumbDownIV.setImageResource(R.drawable.ic_thumb_down_fill_yellow);

            }
        });


    }


    private void initUserInfo() {
        userInfoDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userAction = document.toObject(UserInfoDto.class);

                        liked = userAction.isLiked();
                        disliked = userAction.isDisliked();

                        if (disliked) {
                            thumbDownIV.setBackgroundResource(R.drawable.ic_thumb_down_fill_yellow);
                        } else if (SelectedVideoActivity.this.liked) {
                            thumbUPIV.setBackgroundResource(R.drawable.ic_thumb_up_fill_yellow);
                        }

                        Log.d("[USER ACTION]", "DATA: " + document.getData());
                    } else {
                        Log.d("[USER ACTION]", "No such document " + document.getId());
                    }
                }
            }
        });
    }

    private MediaSource buildMediaSource(Uri uri, DefaultDataSourceFactory dataSourceFactory) {
        @C.ContentType int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private void pipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational rational = new Rational(videoPlayer.getWidth(), videoPlayer.getHeight());
            pictureInPictureParams.setAspectRatio(rational).build();
            enterPictureInPictureMode(pictureInPictureParams.build());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean isActionChanged = !(Objects.equals(liked, userAction.isLiked()) && Objects.equals(disliked, userAction.isDisliked()));

        if (isActionChanged) {
            liveStreamDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            PostDto postDto = document.toObject(PostDto.class);

                            Integer dislikedUsersCount = postDto.getDislikedUsersCount();
                            Integer likedUsersCount = postDto.getLikedUsersCount();


                            if (disliked) {
                                postDto.setDislikedUsersCount(dislikedUsersCount + 1);
                                if (userAction.isLiked()) {
                                    postDto.setLikedUsersCount(likedUsersCount - 1);
                                }
                            } else if (liked) {
                                postDto.setLikedUsersCount(likedUsersCount + 1);
                                if (userAction.isDisliked()) {
                                    postDto.setDislikedUsersCount(dislikedUsersCount - 1);
                                }
                            }

                            liveStreamDocRef.set(postDto)
                                    .addOnSuccessListener(avoid -> {
                                        userAction.setLiked(liked);
                                        userAction.setDisliked(disliked);
                                        userActionDocRef.set(postDto).addOnFailureListener(err -> {
                                            Log.d("[USER ACTION]", "Could not save user action " + userAction.getId() + " due to" + err.getLocalizedMessage());
                                        });
                                    })
                                    .addOnFailureListener(err -> {
                                        Log.d("[POST ACTION]", "Could not save pos action " + postDto.getPostId() + " due to" + err.getLocalizedMessage());
                                    });

                            Log.d("[LIKED VIDEOS]", "DATA: " + document.getData());
                        } else {
                            Log.d("[LIKED VIDEOS]", "No such document " + "id");
                        }
                    } else {
                        Log.d("[LIKED VIDEOS]", "Failed to check is video liked ", task.getException());
                    }
                }
            });
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isInPictureInPictureMode()) {
                pipMode();
            } else {

            }
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);


        if (isInPictureInPictureMode()) {
            arrowDown.setVisibility(View.GONE);
            pip.setVisibility(View.GONE);
        } else {
            arrowDown.setVisibility(View.VISIBLE);
            pip.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

    }
}