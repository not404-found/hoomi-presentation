package com.hoomicorp.hoomi.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hoomicorp.hoomi.R;
import com.hoomicorp.hoomi.model.dto.LiveChatMessageDto;
import com.hoomicorp.hoomi.model.dto.PaymentCardDto;
import com.hoomicorp.hoomi.util.LiveChatMessageDiffUtil;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LiveChatRecyclerViewAdapter extends RecyclerView.Adapter<LiveChatRecyclerViewAdapter.LiveChatRecyclerViewHolder>{

    private List<LiveChatMessageDto> messages;
    private final Context context;

    private final Queue<Integer> backgrounds = new LinkedList<>();



    public LiveChatRecyclerViewAdapter(List<LiveChatMessageDto> messages, Context context) {
        this.messages = messages;
        this.context = context;

        backgrounds.add(context.getResources().getColor(R.color.red));
        backgrounds.add(context.getResources().getColor(R.color.blue));
        backgrounds.add(context.getResources().getColor(R.color.yellow));
        backgrounds.add(context.getResources().getColor(R.color.blink_purple));
        backgrounds.add(context.getResources().getColor(R.color.green));
        backgrounds.add(context.getResources().getColor(R.color.purple));
    }


    @NonNull
    @Override
    public LiveChatRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.message_item, parent, false);

        return new LiveChatRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveChatRecyclerViewHolder holder, int position) {
        LiveChatMessageDto messageDto = messages.get(position);
        holder.username.setText(messageDto.getUsername() + ":");
        holder.text.setText(messageDto.getMessage());

        Integer bg = backgrounds.poll();
        holder.username.setTextColor(bg);
        backgrounds.offer(bg);
    }

    public void addNewMessage(final LiveChatMessageDto message) {
        List<LiveChatMessageDto> newMessages = new ArrayList<>(messages);
        newMessages.add(message);

        LiveChatMessageDiffUtil liveChatMessageDiffUtil = new LiveChatMessageDiffUtil(messages, newMessages);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(liveChatMessageDiffUtil);

        messages.add(message);

        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    class LiveChatRecyclerViewHolder  extends RecyclerView.ViewHolder  {
        private TextView text;
        private TextView username;


        public LiveChatRecyclerViewHolder(@NonNull View view) {
            super(view);

            text = view.findViewById(R.id.live_chat_item_user_text);
            username = view.findViewById(R.id.live_chat_item_username);

        }
    }

}
