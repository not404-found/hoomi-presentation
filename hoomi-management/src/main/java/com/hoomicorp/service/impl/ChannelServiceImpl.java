package com.hoomicorp.service.impl;

import com.hoomicorp.exception.ChannelNotFoundException;
import com.hoomicorp.model.entity.Channel;
import com.hoomicorp.model.request.ChannelRequest;
import com.hoomicorp.repository.ChannelRepository;
import com.hoomicorp.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;

    @Autowired
    public ChannelServiceImpl(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public Channel getById(final Long id) {
        return channelRepository.findById(id).orElseThrow(() -> new ChannelNotFoundException(id));
    }


    public Channel createNewChannel(final ChannelRequest channelRequest) {
        return null;
    }

    public void removeChannel(final Long channelId) {

    }
}
