package com.hoomicorp.hoomi.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.adapter.LiveStreamPlayerAdapter;
import com.hoomicorp.hoomi.listener.OnFullScreenClickListener;
import com.hoomicorp.hoomi.model.dto.CategoryDto;
import com.hoomicorp.hoomi.model.dto.PostDto;
import com.hoomicorp.hoomi.model.enums.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import im.ene.toro.widget.Container;
import im.ene.toro.widget.PressablePlayerSelector;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryStreamsFragment extends Fragment implements OnFullScreenClickListener {

    private ImageView gameDetailImgView;
    private ImageView backImageView;
    private TextView gameTitle;
    private TextView viewers;
    private List<TextView> gameCategoryTvs = new ArrayList<>();
    private Container gamePlayesRV;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference lifeStreamsColRef;
    private List<PostDto> liveStreamDtos = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_streams, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        lifeStreamsColRef = firebaseFirestore.collection("live-streams");


        backImageView = view.findViewById(R.id.event_detail_fragment_back_img_view);
        gameDetailImgView = view.findViewById(R.id.event_detail_fragment_game_img);
        gameTitle = view.findViewById(R.id.event_detail_fragment_game_title);
        viewers = view.findViewById(R.id.event_detail_fragment_game_viewers);
        gameCategoryTvs.add(view.findViewById(R.id.event_detail_fragment_game_category));
        gameCategoryTvs.add(view.findViewById(R.id.event_detail_fragment_game_category1));
        gameCategoryTvs.add(view.findViewById(R.id.event_detail_fragment_game_category2));

        gamePlayesRV = view.findViewById(R.id.event_detail_fragment_game_video_players_recycle_view);

        backImageView.setOnClickListener(v -> {
            // return back
        });

        if (Objects.nonNull(getArguments())) {
            CategoryStreamsFragmentArgs args = CategoryStreamsFragmentArgs.fromBundle(getArguments());
            CategoryDto entertainment = args.getCategory();

            List<Tag> categories = entertainment.getTags();
            int min = Math.min(categories.size(), gameCategoryTvs.size());
            for (int i = 0; i < min; i++) {
                TextView textView = gameCategoryTvs.get(i);
                String category = String.valueOf(categories.get(i));
                textView.setText(category);
                textView.setVisibility(View.VISIBLE);
            }

            gameTitle.setText(entertainment.getDisplayName());
            viewers.setText("100.1");
            Glide.with(this).load(entertainment.getImageLink()).into(gameDetailImgView);

        }

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


//        PressablePlayerSelector pressablePlayerSelector = new PressablePlayerSelector(gamePlayesRV);
//        gamePlayesRV.setPlayerSelector(pressablePlayerSelector);
//        VideoPlayerAdapter videoPlayerAdapter = new VideoPlayerAdapter(getContext(), new ArrayList<>(), pressablePlayerSelector, R.layout.live_streams_player_item);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        gamePlayesRV.setLayoutManager(layoutManager);
//        gamePlayesRV.setAdapter(videoPlayerAdapter);

        return view;
    }

    private void initPlayerViews() {
        PressablePlayerSelector pressablePlayerSelector = new PressablePlayerSelector(gamePlayesRV);
        gamePlayesRV.setPlayerSelector(pressablePlayerSelector);
        LiveStreamPlayerAdapter liveStreamPlayerAdapter = new LiveStreamPlayerAdapter(getContext(), liveStreamDtos, pressablePlayerSelector);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        gamePlayesRV.setLayoutManager(layoutManager);
        gamePlayesRV.setAdapter(liveStreamPlayerAdapter);
    }

    private void prepareGames() {
        for (int i = 0; i < 10; i++) {
            PostDto postDto = new PostDto();
            postDto.setPostId(UUID.randomUUID().toString());
            postDto.setPostName(UUID.randomUUID().toString());
            postDto.setUsername(UUID.randomUUID().toString());
            postDto.setUserProfileImageLink("https://hoomi-images.s3.eu-central-1.amazonaws.com/events-images/vainglory.jpg");
            postDto.setLivesStreamLink("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8");
            postDto.setCategoryName("Vainglory");
            liveStreamDtos.add(postDto);

        }

    }


    @Override
    public void fullScreen() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void exitFullScreen() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}