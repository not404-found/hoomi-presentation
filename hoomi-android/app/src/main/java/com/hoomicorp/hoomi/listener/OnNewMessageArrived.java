package com.hoomicorp.hoomi.listener;

import com.hoomicorp.hoomi.model.dto.LiveChatMessageDto;
import com.hoomicorp.hoomi.model.dto.PollDto;
import com.hoomicorp.hoomi.model.dto.VoteEnum;

public interface OnNewMessageArrived {

    void onMessage(final LiveChatMessageDto messageDto);

    void onPoll(final PollDto pollDto);

    void onVote(final VoteEnum voteEnum);
}
