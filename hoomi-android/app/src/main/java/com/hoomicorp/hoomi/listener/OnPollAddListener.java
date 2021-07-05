package com.hoomicorp.hoomi.listener;

import com.hoomicorp.hoomi.model.dto.PollDto;

public interface OnPollAddListener {
    void addPoll(final PollDto pollDto);
}
