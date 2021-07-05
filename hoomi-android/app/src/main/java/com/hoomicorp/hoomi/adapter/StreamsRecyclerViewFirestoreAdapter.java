package com.hoomicorp.hoomi.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.listener.OnItemClickListener;
import com.hoomicorp.hoomi.model.dto.PostDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class StreamsRecyclerViewFirestoreAdapter extends FirestoreRecyclerAdapter<PostDto, StreamsRecyclerViewFirestoreAdapter.RecyclerStreamViewHolder> {

    private final Context mContext;
    private final RequestOptions requestOptions;
    private final OnItemClickListener<PostDto> onItemClickListener;
    private final FirestoreRecyclerOptions<PostDto> firestoreOptions;

    public StreamsRecyclerViewFirestoreAdapter(Context mContext, FirestoreRecyclerOptions<PostDto> firestoreOptions, OnItemClickListener<PostDto> onItemClickListener) {
        super(firestoreOptions);
        this.mContext = mContext;
        this.firestoreOptions = firestoreOptions;
        this.onItemClickListener = onItemClickListener;
        this.requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onBindViewHolder(@NonNull StreamsRecyclerViewFirestoreAdapter.RecyclerStreamViewHolder holder, int position, @NonNull PostDto model) {
        holder.streamName.setText(model.getPostName());
//        holder.waitingViewersCount.setText(model.getWaitingViewersId().size());
        holder.waitingViewersCount.setText("Waiting: 100");
        LocalDateTime dateTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(model.getScheduledDateTime()), ZoneId.systemDefault());
        holder.scheduledDate.setText("Scheduled at: " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        Glide.with(mContext).load(model.getPostImageLink()).apply(requestOptions).into(holder.ivGameThumbnail);


    }

    @NonNull
    @Override
    public RecyclerStreamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.scheduled_streams_item, parent, false);

        return new RecyclerStreamViewHolder(view);
    }


    class RecyclerStreamViewHolder extends RecyclerView.ViewHolder {

        private TextView streamName;
        private TextView waitingViewersCount;
        private TextView scheduledDate;
        private ImageView ivGameThumbnail;

        public RecyclerStreamViewHolder(@NonNull View itemView) {
            super(itemView);
            streamName = itemView.findViewById(R.id.scheduled_streams_game_title);
            waitingViewersCount = itemView.findViewById(R.id.scheduled_streams_game_viewers_count);
            scheduledDate = itemView.findViewById(R.id.scheduled_streams_scheduled_date);

            ivGameThumbnail = itemView.findViewById(R.id.scheduled_streams_game_thumbnail);

            itemView.setOnClickListener(v -> {
                PostDto item = getItem(getAdapterPosition());
                onItemClickListener.onItemClick(item);
            });
        }


    }
}
