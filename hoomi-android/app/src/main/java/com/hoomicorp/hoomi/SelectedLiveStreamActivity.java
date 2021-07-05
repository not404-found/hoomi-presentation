package com.hoomicorp.hoomi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Rational;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hoomicorp.hoomi.adapter.LiveChatRecyclerViewAdapter;
import com.hoomicorp.hoomi.adapter.PaymentCardRecyclerViewAdapter;
import com.hoomicorp.hoomi.listener.OnItemClickListener;
import com.hoomicorp.hoomi.listener.OnNewMessageArrived;
import com.hoomicorp.hoomi.model.UserSession;
import com.hoomicorp.hoomi.model.dto.DonationRequest;
import com.hoomicorp.hoomi.model.dto.DonationResponse;
import com.hoomicorp.hoomi.model.dto.LiveChatMessageDto;
import com.hoomicorp.hoomi.model.dto.PaymentCardDto;
import com.hoomicorp.hoomi.model.dto.PollDto;
import com.hoomicorp.hoomi.model.dto.PostDto;
import com.hoomicorp.hoomi.model.dto.UserInfoDto;
import com.hoomicorp.hoomi.model.dto.VoteEnum;
import com.hoomicorp.hoomi.model.enums.Currency;
import com.hoomicorp.hoomi.model.enums.PaymentCardType;
import com.hoomicorp.hoomi.model.request.HttpRequest;
import com.hoomicorp.hoomi.mqtt.MqttClientInstance;
import com.hoomicorp.hoomi.mqtt.MqttMessageHandler;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.Address;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectedLiveStreamActivity extends AppCompatActivity implements OnNewMessageArrived {

    private final Gson jsonConverter = new Gson();


    private RequestQueue requestQueue;

    private Stripe stripe;
    private String paymentIntentClientSecret;


    private DocumentReference userActionDocRef;
    private DocumentReference liveStreamDocRef;
    private DocumentReference userInfoDocRef;

    private PostDto postDto;
    private UserInfoDto userAction;

    private boolean liked = false;
    private boolean disliked = false;

    private ImageView notificationIV;
    private ImageView thumbDownIV;
    private ImageView thumbUPIV;
    private ImageView saveIV;
    private ImageView shareIV;

    private View donationSuccessImage;
    private View donationFailedImage;
    private View donationProcessingImage;
    private View liveChatContainer;
    private RecyclerView liveChatRV;

    private View bottomSheetView;
    private View chooseDonateMethodContainer;
    private View chooseDonateContainer;
    private View chooseDonateCardContainer;
    private View donationResultContainer;
    private Button donationResultButton;

    private Spinner donationCurrencySpinner;
    private EditText donationAmountET;

    private View pollContainer;
    private TextView pollText;
    private View leftPoll;
    private TextView leftPollText;
    private View rightPoll;
    private TextView rightPollText;
    private Button skipPollBtn;

    private LiveChatRecyclerViewAdapter liveChatRecyclerViewAdapter;

    private FrameLayout arrowDown;
    private FrameLayout pip;

    private PlayerView videoPlayer;
    private TextView saveTextView;

    private PictureInPictureParams.Builder pictureInPictureParams;

    private BottomSheetDialog bottomSheetDialog;

    private MqttClientInstance mqttClient;

    private String postId;
    private String streamerId;

    private String topic;

    private List<LiveChatMessageDto> liveChatMessages = new ArrayList<>();

    private PollDto pollDto;
    private VoteEnum selectedVote;
    private final AtomicInteger pollPrice = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_live_stream);
        //http/s init
        requestQueue = Volley.newRequestQueue(this);

        // stripe init
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51IJQDDKJAzwSmmCg5JFDh7THNn5L9DuFF4HH5oaLq7vPkNJuSGEFwOy5BK2iDLDUyd5QBZz0NfUg14AddTN20Vrv00ELQqWBov")
        );

        // input args
        String streamUrl = getIntent().getStringExtra("streamUrl");
        this.postId = getIntent().getStringExtra("postId");
        this.streamerId = getIntent().getStringExtra("streamerId");
        this.topic = streamerId + "/live-stream/" + postId;

        System.out.println("LISTENING: " + topic);
        this.mqttClient = MqttClientInstance.getInstance();

        //firebase init
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        //db init
        String uid = UserSession.getInstance().getId();
        ;

        userInfoDocRef = firebaseFirestore.collection("user-info").document(uid);
        liveStreamDocRef = firebaseFirestore.collection("live-streams").document(postId);
        userActionDocRef = liveStreamDocRef.collection("user-action").document(uid);

        // views init
        videoPlayer = findViewById(R.id.selected_stream_activity_stream_video_player);
        ImageView donateIV = findViewById(R.id.selected_stream_activity_donate_icon);
        notificationIV = findViewById(R.id.selected_stream_notification_off_icon);
        thumbDownIV = findViewById(R.id.selected_stream_activity_thumb_down_icon);
        thumbUPIV = findViewById(R.id.selected_stream_activity_thumb_up_icon);
        saveIV = findViewById(R.id.selected_stream_activity_save_to_playlist_icon);
        saveTextView = findViewById(R.id.selected_stream_activity_save_text_view);
        liveChatRV = findViewById(R.id.selected_stream_activity_chat_rv);
        liveChatContainer = findViewById(R.id.selected_stream_activity_chat);
        Button followingBtn = findViewById(R.id.selected_stream_activity_follow_btn);
        EditText messageInputET = findViewById(R.id.selected_stream_activity_chat_input_msg);
        Button messageSendBtn = findViewById(R.id.selected_stream_activity_chat_send_msg);

        //poll view
        pollContainer = findViewById(R.id.selected_stream_activity_poll);
        pollText = findViewById(R.id.selected_stream_activity_poll_question_et);
        leftPoll = findViewById(R.id.selected_stream_activity_poll_left_vote_container);
        leftPollText = findViewById(R.id.selected_stream_activity_poll_left_vote_et);
        rightPoll = findViewById(R.id.selected_stream_activity_poll_right_vote_container);
        rightPollText = findViewById(R.id.selected_stream_activity_poll_right_vote_et);
        skipPollBtn = findViewById(R.id.selected_stream_activity_poll_skip_poll_btn);

        //bottom sheet dialog setup
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetTheme);
        bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout, findViewById(R.id.bottom_sheet));
        bottomSheetDialog.setContentView(bottomSheetView);

        View donate = bottomSheetView.findViewById(R.id.bottom_sheet_donate_btn);
        donationAmountET = bottomSheetView.findViewById(R.id.bottom_sheet_amount);
        bottomSheetView.findViewById(R.id.bottom_sheet_spinner);

        chooseDonateMethodContainer = bottomSheetView.findViewById(R.id.bottom_sheet_choose_donate_method_container);
        chooseDonateContainer = bottomSheetView.findViewById(R.id.bottom_sheet_choose_donate_container);
        chooseDonateCardContainer = bottomSheetView.findViewById(R.id.bottom_sheet_select_card_container);
        donationResultContainer = bottomSheetView.findViewById(R.id.bottom_sheet_donation_result_container);

        View donateWithMainCard = bottomSheetView.findViewById(R.id.bottom_sheet_pay_with_main_card);
        View donateWithPaPayPal = bottomSheetView.findViewById(R.id.bottom_sheet_pay_with_main_paypal);
        View selectCardToDonate = bottomSheetView.findViewById(R.id.bottom_sheet_pay_select_card);

        donationSuccessImage = bottomSheetView.findViewById(R.id.bottom_sheet_donation_success_img);
        donationFailedImage = bottomSheetView.findViewById(R.id.bottom_sheet_donation_failed_img);
        donationProcessingImage = bottomSheetView.findViewById(R.id.bottom_sheet_donation_processing_img);
        donationResultButton = bottomSheetView.findViewById(R.id.bottom_sheet_donation_result_btn);

        //spinner init
        donationCurrencySpinner = bottomSheetView.findViewById(R.id.bottom_sheet_spinner);
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.currency));
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        donationCurrencySpinner.setAdapter(currencyAdapter);

        // impl spinner popup height
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(donationCurrencySpinner);

            float resultPix = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
            int pixInInt = Math.round(resultPix);
            // Set popupWindow height to 500px
            popupWindow.setHeight(pixInInt);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }

        //donate with main card init
        donateWithMainCard.setOnClickListener(v -> {
            showDonateContainer();
        });


        //select card  to donate init
        selectCardToDonate.setOnClickListener(v -> {
            selectCardToDonate();
        });

        //donate btn setup
        donateIV.setOnClickListener(v -> {
            showBottomSheetDialog();

//            Intent applicationStartActivity = new Intent(SelectedLiveStreamActivity.this, CheckoutActivity.class);
//            startActivity(applicationStartActivity);
        });

        //donation setup
        donate.setOnClickListener(v -> {
            donate();
        });

        //poll setup
        skipPollBtn.setOnClickListener(v -> {
            liveChatContainer.setVisibility(View.VISIBLE);
            pollContainer.setVisibility(View.GONE);
        });

        leftPoll.setOnClickListener(v -> {
            if (Objects.nonNull(pollDto)) {
                int leftVotePrice = pollDto.getLeftVotePrice();
                if (leftVotePrice == 0) {
                    byte[] payload = new byte[] {0};
                    mqttClient.sendMessage(topic + "/vote", payload);
                } else {
                    this.pollPrice.set(leftVotePrice);
                    this.selectedVote = VoteEnum.LEFT;
                    showBottomSheetDialog();
                }
            }
        });

        rightPoll.setOnClickListener(v -> {
            if (Objects.nonNull(pollDto)) {
                final int rightVotePrice = pollDto.getRightVotePrice();
                if (rightVotePrice == 0) {
                    byte[] payload = new byte[] {1};
                    mqttClient.sendMessage(topic + "/vote", payload);
                } else {
                    this.pollPrice.set(rightVotePrice);
                    this.selectedVote = VoteEnum.RIGHT;
                    showBottomSheetDialog();
                }
            }
        });


        // setup post info
        liveStreamDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        postDto = document.toObject(PostDto.class);
                        // todo liked count & disliked
                        Log.d("[LIKED VIDEOS]", "DATA: " + document.getData());
                    } else {
                        Log.d("[LIKED VIDEOS]", "No such document " + "id");
                    }
                } else {
                    Log.d("[LIKED VIDEOS]", "Failed to check is video liked ", task.getException());
                }
            }
        });

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
                                } else if (SelectedLiveStreamActivity.this.liked) {
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


        //message send btn setup
        messageSendBtn.setOnClickListener(v -> {
            String message = messageInputET.getText().toString();
            if (!TextUtils.isEmpty(message)) {
                final LiveChatMessageDto messageDto = new LiveChatMessageDto(message, UserSession.getInstance().getUsername());
                final String liveChatTopic = this.topic + "/live-chat/";
                mqttClient.sendMessage(liveChatTopic, messageDto.toString().getBytes());
                messageInputET.setText("");
                messageInputET.clearFocus();
            }
        });

        // live chat setup
        liveChatRecyclerViewAdapter = new LiveChatRecyclerViewAdapter(liveChatMessages, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        liveChatRV.setLayoutManager(linearLayoutManager);
        liveChatRV.setAdapter(liveChatRecyclerViewAdapter);
    }

    //2 -- select card
    private void selectCardToDonate() {
        chooseDonateCardContainer.setVisibility(View.VISIBLE);
        chooseDonateMethodContainer.setVisibility(View.GONE);
        chooseDonateContainer.setVisibility(View.GONE);
        //rv setup
        RecyclerView cardsRV = bottomSheetView.findViewById(R.id.bottom_sheet_select_card_rv);
        //TODO use card from saved cards
        List<PaymentCardDto> paymentCardDtos = List.of(new PaymentCardDto("1234 5678 9012 3456", PaymentCardType.VISA, false, "AMAL", "Kabulov", "19/19"),
                new PaymentCardDto("9012 3456 7890 1324", PaymentCardType.PAYPAL, false, "AMAL", "Kabulov", "19/19"),
                new PaymentCardDto("8495 9504 1230 8123", PaymentCardType.BTC, false, "AMAL", "Kabulov", "19/19"),
                new PaymentCardDto("9021 0932 9321 1652", PaymentCardType.MASTERCARD, false, "AMAL", "Kabulov", "19/19"));

        PaymentCardRecyclerViewAdapter paymentCardRecyclerViewAdapter = new PaymentCardRecyclerViewAdapter(this, paymentCardDtos,
                new OnItemClickListener<PaymentCardDto>() {
                    @Override
                    public void onItemClick(PaymentCardDto item) {
                        //todo save donation card
                       showDonateContainer();
                    }
                });
        cardsRV.setAdapter(paymentCardRecyclerViewAdapter);
        cardsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    //2 --main card
    private void showDonateContainer() {
        int pollPrice = this.pollPrice.get();
        if (pollPrice != 0) {
            this.donationAmountET.setText(String.valueOf(pollPrice));

            this.donationAmountET.setFocusable(false);
            this.donationAmountET.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            this.donationAmountET.setClickable(false);
            this.donationAmountET.setFocusable(false);

            // 0 - usd
            this.donationCurrencySpinner.setSelection(0);

            this.donationCurrencySpinner.setFocusable(false);
            this.donationCurrencySpinner.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            this.donationCurrencySpinner.setClickable(false);
            this.donationCurrencySpinner.setFocusable(false);
        }
        this.chooseDonateContainer.setVisibility(View.VISIBLE);
        this.chooseDonateMethodContainer.setVisibility(View.GONE);
    }

    // 1
    private void showBottomSheetDialog() {
        chooseDonateMethodContainer.setVisibility(View.VISIBLE);
        chooseDonateCardContainer.setVisibility(View.GONE);
        chooseDonateContainer.setVisibility(View.GONE);
        donationResultContainer.setVisibility(View.GONE);
        bottomSheetDialog.show();
    }


    public void donate() {
        chooseDonateContainer.setVisibility(View.GONE);
        donationResultContainer.setVisibility(View.VISIBLE);
        donationProcessingImage.setVisibility(View.VISIBLE);

        DonationRequest donationRequest = new DonationRequest(Currency.USD, 5000);
        final HttpRequest registerReq = new HttpRequest(Request.Method.POST,
                "http://192.168.100.42:8090/payment/api/v1/charge/create-payment",
                resp -> {

                    DonationResponse donationResponse = jsonConverter.fromJson(resp, DonationResponse.class);

                    paymentIntentClientSecret = donationResponse.getPaymentIntentCS();
                    PaymentMethodCreateParams params = PaymentMethodCreateParams.create(new PaymentMethodCreateParams.Card("4242424242424242", 11, 22, "999", null, Set.of()),
                            new PaymentMethod.BillingDetails(new Address(null, null, null, null, "222222", null)));
                    if (params != null) {
                        ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                        stripe.confirmPayment(this, confirmParams);
                    }
                },
                error -> {
                    donationFailedImage.setVisibility(View.VISIBLE);
                    donationProcessingImage.setVisibility(View.GONE);
                    error.printStackTrace();
                }, donationRequest);

        requestQueue.add(registerReq);
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
                        } else if (SelectedLiveStreamActivity.this.liked) {
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
    protected void onStart() {
        System.out.println("================================ON START=========================================");
        super.onStart();
        try {
            mqttClient.initMqttClient(new MqttMessageHandler(this), topic);
        } catch (MqttException e) {
            //todo log
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            mqttClient.release();
        } catch (MqttException e) {
            //todo log
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    @Override
    public void onPoll(PollDto pollDto) {
        System.out.println("RECEIVED POLL");
        rightPollText.setText(pollDto.getRightVote());
        leftPollText.setText(pollDto.getLeftVote());
        pollText.setText(pollDto.getQuestion());

        this.pollDto = pollDto;


        runOnUiThread(() -> {
            pollContainer.setVisibility(View.VISIBLE);
            liveChatContainer.setVisibility(View.INVISIBLE);
        });

//        showBottomSheetDialog();
    }

    @Override
    public void onVote(VoteEnum voteEnum) {

    }

    @Override
    public void onMessage(LiveChatMessageDto message) {
        runOnUiThread(() -> {
            liveChatRecyclerViewAdapter.addNewMessage(message);
            liveChatRV.smoothScrollToPosition(liveChatMessages.size() - 1);
        });
    }


    private static final class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {

        @NonNull
        private final WeakReference<SelectedLiveStreamActivity> activityRef;

        PaymentResultCallback(@NonNull SelectedLiveStreamActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final SelectedLiveStreamActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                activity.donationSuccessImage.setVisibility(View.VISIBLE);
                activity.donationProcessingImage.setVisibility(View.GONE);
                activity.pollContainer.setVisibility(View.GONE);
                activity.liveChatContainer.setVisibility(View.VISIBLE);
                activity.donationResultButton.setVisibility(View.VISIBLE);



                activity.donationAmountET.setFocusable(true);
                activity.donationAmountET.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
                activity.donationAmountET.setClickable(true);
                activity.donationAmountET.setFocusable(true);

                activity.donationCurrencySpinner.setFocusable(true);
                activity.donationCurrencySpinner.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
                activity.donationCurrencySpinner.setClickable(true);
                activity.donationCurrencySpinner.setFocusable(true);



                if (!Objects.equals(activity.pollPrice.get(), 0)) {

                    byte[] payload = new byte[]{activity.selectedVote.getVoteNum()};
                    activity.mqttClient.sendMessage(activity.topic + "/vote", payload);
                }

                activity.pollPrice.set(0);


                activity.donationResultButton.setOnClickListener(v -> {
                    activity.bottomSheetDialog.dismiss();
                });
//                activity.displayAlert(
//                        "Payment completed",
//                        gson.toJson(paymentIntent)
//                );
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {

                activity.donationResultButton.setText("Retry");

                activity.donationFailedImage.setVisibility(View.VISIBLE);
                activity.donationProcessingImage.setVisibility(View.GONE);
                activity.donationResultButton.setVisibility(View.VISIBLE);


                // Payment failed – allow retrying using a different payment method
//                activity.displayAlert(
//                        "Payment failed",
//                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
//                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final SelectedLiveStreamActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            activity.donationFailedImage.setVisibility(View.VISIBLE);
            activity.donationProcessingImage.setVisibility(View.GONE);

            // Payment request failed – allow retrying using the same payment method
//            activity.displayAlert("Error", e.toString());
        }
    }
}