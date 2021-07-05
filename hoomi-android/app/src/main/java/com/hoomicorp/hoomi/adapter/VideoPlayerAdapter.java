package com.hoomicorp.hoomi.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ui.PlayerView;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.SelectedLiveStreamActivity;
import com.hoomicorp.hoomi.model.dto.PostDto;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import im.ene.toro.CacheManager;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoPlayerDispatcher;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;
import im.ene.toro.widget.PressablePlayerSelector;
import pl.droidsonroids.gif.GifImageView;


public class VideoPlayerAdapter extends RecyclerView.Adapter<VideoPlayerAdapter.RecyclerVideoPlayerHolder> implements CacheManager {
    private final Context context;
    private final List<PostDto> postDtos;
    private final PressablePlayerSelector selector;

    private final List<String> previews = new ArrayList<>();

    public VideoPlayerAdapter(Context context, List<PostDto> postDtos, PressablePlayerSelector selector) {
        this.context = context;
        this.postDtos = postDtos;
        this.selector = selector;

        previews.add("https://hoomi-images.s3.eu-central-1.amazonaws.com/gifs/am.gif");
        previews.add("https://hoomi-images.s3.eu-central-1.amazonaws.com/gifs/moonrider.gif");
    }

    @NonNull
    @Override
    public RecyclerVideoPlayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.video_player_item, parent, false);

        RecyclerVideoPlayerHolder recyclerVideoPlayerHolder = new RecyclerVideoPlayerHolder(view, selector);
        if (this.selector != null)
            recyclerVideoPlayerHolder.itemView.setOnLongClickListener(this.selector);
        return recyclerVideoPlayerHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerVideoPlayerHolder holder, int position) {

        PostDto postDto = postDtos.get(position);

        holder.bind(Uri.parse(postDto.getLivesStreamLink()));
        holder.streamerName.setText(postDto.getUsername());
        holder.streamName.setText(postDto.getPostName());
        holder.gameName.setText(postDto.getCategoryName());
        Glide.with(context).load(postDto.getUserProfileImageLink()).into(holder.streamerImage);
        String string = previews.get((int) (Math.random() * 2));
        Glide.with(context).load(string).into(holder.gifImageView);
        holder.postId = postDto.getPostId();


    }


    @Override
    public int getItemCount() {
        return postDtos.size();
    }


    @Nullable
    @Override
    public Object getKeyForOrder(int order) {
        return null;
    }

    @Nullable
    @Override
    public Integer getOrderForKey(@NonNull Object key) {
        return null;
    }


    public class RecyclerVideoPlayerHolder extends RecyclerView.ViewHolder implements ToroPlayer {
        private String postId;
        private ConstraintLayout playerViewContainer;
        private PlayerView playerView;
        private GifImageView gifImageView;
        private TextView streamerName;
        private TextView streamName;
        private TextView gameName;
        private CircleImageView streamerImage;
        private ExoPlayerViewHelper helper;
        private Uri uri;

        public RecyclerVideoPlayerHolder(@NonNull View itemView, PressablePlayerSelector selector) {
            super(itemView);


            playerView = itemView.findViewById(R.id.stream_video_player);
            playerViewContainer = itemView.findViewById(R.id.video_player_rl);
            gifImageView = itemView.findViewById(R.id.video_preview_gif);
            streamerName = itemView.findViewById(R.id.video_player_item_streamer_name);
            streamName = itemView.findViewById(R.id.video_player_item_stream_title);
            gameName = itemView.findViewById(R.id.video_player_item_game_name);
            streamerImage = itemView.findViewById(R.id.video_player_item_streamer_image);

            if (selector != null) {
                playerView.setControlDispatcher(new ExoPlayerDispatcher(selector, this));

            }

            View fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_button);
            fullscreenButton.setVisibility(View.GONE);


            playerView.setUseController(true);
            playerViewContainer.setOnClickListener(v -> {

                openSelectedStreamActivity();
            });

        }


        private void openSelectedStreamActivity() {

            Intent selectedLiveStreamActivity = new Intent(context, SelectedLiveStreamActivity.class);
            selectedLiveStreamActivity.putExtra("streamUrl", uri.toString());
            selectedLiveStreamActivity.putExtra("postId", postId);

            context.startActivity(selectedLiveStreamActivity);

        }


        @NonNull
        @Override
        public View getPlayerView() {
            return playerView;
        }

        @NonNull
        @Override
        public PlaybackInfo getCurrentPlaybackInfo() {
            return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
        }

        @Override
        public void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo) {
            if (helper == null) {
                helper = new ExoPlayerViewHelper(this, uri);
            }
            helper.initialize(container, playbackInfo);
//            helper.addPlayerEventListener(new EventListener() {
//                @Override
//                public void onFirstFrameRendered() {
//
//                }
//
//                @Override
//                public void onBuffering() {
//
//                }
//
//                @Override
//                public void onPlaying() {
//                    playerView.setVisibility(View.VISIBLE);
//                    gifImageView.setVisibility(View.INVISIBLE);
//                }
//
//                @Override
//                public void onPaused() {
//                    playerView.setVisibility(View.INVISIBLE);
//                    gifImageView.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onCompleted() {
//                    playerView.setVisibility(View.INVISIBLE);
//                    gifImageView.setVisibility(View.VISIBLE);
//                }
//            });

        }

        @Override
        public void play() {
            if (helper != null) {
                helper.play();
                playerView.setVisibility(View.VISIBLE);
                gifImageView.setVisibility(View.GONE);
            }

        }

        @Override
        public void pause() {
            if (helper != null) {
                helper.pause();
                playerView.setVisibility(View.GONE);
                gifImageView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public boolean isPlaying() {
            return helper != null && helper.isPlaying();
        }

        @Override
        public void release() {
            if (helper != null) {
                helper.release();
                helper = null;
            }
        }

        @Override
        public boolean wantsToPlay() {
            return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.85;
        }

        @Override
        public int getPlayerOrder() {
            return getAdapterPosition();
        }

        @Override
        public String toString() {
            return "ExoPlayer{" + hashCode() + " " + getAdapterPosition() + "}";
        }

        public void bind(Uri uri) {
            this.uri = uri;
        }
    }
}
