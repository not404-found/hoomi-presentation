package com.hoomicorp.hoomi.util;

import androidx.recyclerview.widget.DiffUtil;

import com.hoomicorp.hoomi.model.dto.LiveChatMessageDto;

import java.util.List;

public class LiveChatMessageDiffUtil extends DiffUtil.Callback {
    private final List<LiveChatMessageDto> oldMessages;
    private final List<LiveChatMessageDto> newMessages;

    public LiveChatMessageDiffUtil(List<LiveChatMessageDto> oldMessages, List<LiveChatMessageDto> newMessages) {
        this.oldMessages = oldMessages;
        this.newMessages = newMessages;
    }

    @Override
    public int getOldListSize() {
        return oldMessages.size();
    }

    @Override
    public int getNewListSize() {
        return newMessages.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        LiveChatMessageDto oldMessage = oldMessages.get(oldItemPosition);
        LiveChatMessageDto newMessage = newMessages.get(newItemPosition);
        return oldMessage.equals(newMessage);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        LiveChatMessageDto oldMessage = oldMessages.get(oldItemPosition);
        LiveChatMessageDto newMessage = newMessages.get(newItemPosition);
        return oldMessage.equals(newMessage);
    }
}
