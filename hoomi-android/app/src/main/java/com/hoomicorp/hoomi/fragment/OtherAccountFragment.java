package com.hoomicorp.hoomi.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.adapter.AccountFragmentTabAdapter;
import com.hoomicorp.hoomi.model.UserSession;
import com.hoomicorp.hoomi.model.dto.UserInfoDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherAccountFragment extends Fragment {

    private UserInfoDto ownerInfo;
    private UserInfoDto streamerInfo;

    private String ownerId;
    private String streamerId;

    private boolean followed;
    private boolean unfollowed;


    private CollectionReference streamerFollowersReference;
    private CollectionReference ownerFollowingReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_other_account, container, false);


        //firebase setup

        ownerId = UserSession.getInstance().getId();

        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final CollectionReference userInfoCollection = firestore.collection("user-info");
        final DocumentReference ownerInfoDocument = userInfoCollection.document(ownerId);

        ownerFollowingReference = ownerInfoDocument.collection("following");

        //init views
        TextView usernameTV = view.findViewById(R.id.other_account_fragment_username);
        ImageView profileImage = view.findViewById(R.id.other_account_fragment_user_image);
        Button followButton = view.findViewById(R.id.other_account_fragment_follow_button);
        Button messageButton = view.findViewById(R.id.other_account_fragment_message_button);

        //init tabs
        TabLayout tabLayout = view.findViewById(R.id.other_account_fragment_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.other_account_fragment_view_pager);

        if (Objects.nonNull(getArguments())) {
            OtherAccountFragmentArgs accountFragmentArgs = OtherAccountFragmentArgs.fromBundle(getArguments());

            ownerInfoDocument.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ownerInfo = document.toObject(UserInfoDto.class);

                    } else {
                        System.out.println("Document not exist");

                        //TODO log err
                    }
                } else {
                    //TODO log err

                    System.out.println("Error while getting user info");

                }
            });

            //init view as streamer account if not owner account
            streamerInfo = accountFragmentArgs.getUserInfoDto();
            streamerId = streamerInfo.getId();
            final String streamerImageLink = streamerInfo.getProfileImageLink();

            streamerFollowersReference = userInfoCollection.document(streamerId).collection("followers");

            AccountFragmentTabAdapter tabsAdapter = new AccountFragmentTabAdapter(getChildFragmentManager(), tabLayout.getTabCount(), streamerId, getContext(), null);
            viewPager.setAdapter(tabsAdapter);
            tabLayout.setupWithViewPager(viewPager);

            Glide.with(this).load(streamerImageLink).into(profileImage);
            usernameTV.setText(streamerInfo.getUsername());


            //check if user already follows streamer
            DocumentReference streamerDocRef = ownerFollowingReference.document(streamerId);
            streamerDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("[Account Fragment]", "User already follows streamer: " + streamerId);

                            followButton.setBackgroundResource(R.drawable.custom_button_transparent);
                            followButton.setText(R.string.following);
                            followButton.setTextColor(getResources().getColor(R.color.colorAccent, null));

                            followed = true;
                            unfollowed = false;

                        } else {

                            followed = false;
                            unfollowed = false;
                            Log.d("[Account Fragment]", "User don't follows streamer: " + streamerId);


                        }
                    } else {
                        Log.d("[Account Fragment]", "Failed to get streamer from user subsriptions: " + streamerId);
                    }
                }
            });

            messageButton.setOnClickListener(v -> {
                System.out.println("Message");
            });


            followButton.setOnClickListener(v -> {
                if (followed) {
                    System.out.println("UNFOLLOW");
                    followButton.setBackgroundResource(R.drawable.custom_button_accent);
                    followButton.setText(R.string.follow);
                    followButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));

                    //delete user from streamer subscribers
                    unfollowed = true;
                    followed = false;
                } else  {
                    System.out.println("FOLLOW");
                    followButton.setBackgroundResource(R.drawable.custom_button_transparent);
                    followButton.setText(R.string.following);
                    followButton.setTextColor(getResources().getColor(R.color.colorAccent, null));


                    unfollowed = false;
                    followed = true;
                }
            });


        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPause() {
        super.onPause();

        if (Objects.nonNull(ownerInfo) && Objects.nonNull(streamerInfo)) {

            if (followed) {
                long updatedDateTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                ownerInfo.setUpdatedDateTime(updatedDateTime);
                ownerInfo.setUpdatedDateTime(updatedDateTime);

                streamerFollowersReference.document(ownerId).set(ownerInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("[Account Fragment]", "User " + ownerId + " added to followers " + streamerId);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("[Account Fragment]", "Cant add user " + ownerId + " to followers " + streamerId, e);
                            }
                        });

                ownerFollowingReference.document(streamerId).set(streamerInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("[Account Fragment]", "Streamer " + streamerId + " added to following " + ownerId);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("[Account Fragment]", "Cant add streamer " + streamerId + " to following " + ownerId, e);
                            }
                        });


            } else if (unfollowed) {
                streamerFollowersReference.document(ownerId).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("[Account Fragment]", "User " + ownerId + " removed from followers " + streamerId);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("[Account Fragment]", "Cant remove user " + ownerId + " from followers " + streamerId, e);
                            }
                        });

                //delete streamer from user subscriptions
                ownerFollowingReference.document(streamerId).delete().
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("[Account Fragment]", "Streamer " + streamerId + " removed from following " + ownerId);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("[Account Fragment]", "Cant remove streamer " + streamerId + " from following " + ownerId, e);
                            }
                        });
            }


        }
    }
}