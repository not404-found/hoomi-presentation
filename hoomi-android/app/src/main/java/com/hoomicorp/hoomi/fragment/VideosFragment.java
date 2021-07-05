package com.hoomicorp.hoomi.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.adapter.LiveStreamPlayerAdapter;
import com.hoomicorp.hoomi.adapter.VideoPlayerAdapter;
import com.hoomicorp.hoomi.model.dto.PostDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import im.ene.toro.widget.Container;
import im.ene.toro.widget.PressablePlayerSelector;

/**
 * A simple {@link Fragment} subclass
 */
public class VideosFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference videosRef;
    private Container videosContainer;

    private final List<PostDto> postDtos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        //firestore setup
        firebaseFirestore = FirebaseFirestore.getInstance();
        //videos collection setup
        //TODO update to video - used live streams just for test
        videosRef = firebaseFirestore.collection("live-streams");

        //videos rv setup
        videosContainer = view.findViewById(R.id.videos_fragment_recycle_view);

        videosRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PostDto postDto = document.toObject(PostDto.class);
                            postDtos.add(postDto);
                        }
                        prepareGames();
                        initPlayerViews();
                    } else {
                        //todo handle exception task.getException()
                    }
                });

        return view;
    }

    private void initPlayerViews() {
        PressablePlayerSelector pressablePlayerSelector = new PressablePlayerSelector(videosContainer);
        videosContainer.setPlayerSelector(pressablePlayerSelector);
        VideoPlayerAdapter videoPlayerAdapter = new VideoPlayerAdapter(getContext(), postDtos, pressablePlayerSelector);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        videosContainer.setLayoutManager(layoutManager);
        videosContainer.setAdapter(videoPlayerAdapter);
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
            postDtos.add(postDto);

        }

    }
}