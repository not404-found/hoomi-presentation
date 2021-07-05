package com.hoomicorp.hoomi.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.adapter.LiveStreamPlayerAdapter;
import com.hoomicorp.hoomi.model.dto.PostDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import im.ene.toro.widget.Container;
import im.ene.toro.widget.PressablePlayerSelector;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LiveStreamsFragment extends Fragment {

    private Container liveStreamsRV;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference lifeStreamsColRef;

    private List<PostDto> liveStreamDtos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("[LiveStreamsFragment]", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_live_streams, container, false);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference userInfoCollectionRef = firebaseFirestore.collection("user-info");
        lifeStreamsColRef = firebaseFirestore.collection("live-streams");
        liveStreamsRV = view.findViewById(R.id.live_streams_fragment_recycle_view);

//        CollectionReference subscriptions = userInfoCollectionRef.document(currentUser.getUid()).collection("following");

        lifeStreamsColRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PostDto postDto = document.toObject(PostDto.class);
                                liveStreamDtos.add(postDto);
                            }
                            prepareGames();
                            initPlayerViews();
                        } else {
                            //todo handle exception task.getException()
                        }
                    }
                });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("[LiveStreamsFragment]", "onViewCreated");
    }

    private void initPlayerViews() {
        PressablePlayerSelector pressablePlayerSelector = new PressablePlayerSelector(liveStreamsRV);
        liveStreamsRV.setPlayerSelector(pressablePlayerSelector);
        LiveStreamPlayerAdapter liveStreamPlayerAdapter = new LiveStreamPlayerAdapter(getContext(), liveStreamDtos, pressablePlayerSelector);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        liveStreamsRV.setLayoutManager(layoutManager);
        liveStreamsRV.setAdapter(liveStreamPlayerAdapter);
    }

    private void prepareGames() {
        for (int i = 0; i < 10; i++) {
            PostDto postDto = new PostDto();
            postDto.setPostId(UUID.randomUUID().toString());
            postDto.setPostName(UUID.randomUUID().toString());
            postDto.setUsername(UUID.randomUUID().toString());
            postDto.setUserProfileImageLink(((int) (Math.random() * 2)) == 1 ? "https://hoomi-images.s3.eu-central-1.amazonaws.com/events-images/call-of-duty.jpg" :
                    "https://hoomi-images.s3.eu-central-1.amazonaws.com/events-images/wot.png");
            postDto.setLivesStreamLink("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8");
            postDto.setCategoryName(((int) (Math.random() * 2)) == 1 ? "PUBG" : "Vainglory");
            liveStreamDtos.add(postDto);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("[LiveStreamsFragment]", "onResume");
    }
}