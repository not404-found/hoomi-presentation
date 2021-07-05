package com.hoomicorp.service.impl;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.Firestore;
import com.hoomicorp.model.dto.StreamDto;

import com.hoomicorp.service.ChannelService;
import com.hoomicorp.service.CategoryService;
import com.hoomicorp.service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class StreamServiceImpl implements StreamService {
    private final ChannelService channelService;
    private final CategoryService categoryService;
    private final Firestore firestore;

    @Autowired
    public StreamServiceImpl(ChannelService channelService, CategoryService categoryService, Firestore firestore) {
        this.channelService = channelService;
        this.categoryService = categoryService;
        this.firestore = firestore;
    }

    @PostConstruct
    private void init() {
        CollectionReference streams = firestore.collection("live-streams");
        streams.addSnapshotListener((snapshots, err) -> {
            if (err != null) {
                System.err.println("Listen failed: " + err);
                return;
            }

            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        StreamDto streamDto = dc.getDocument().toObject(StreamDto.class);
                        System.out.println("New Live Stream: " + streamDto);
                        break;
                    case MODIFIED:
                        System.out.println("Modified Live Stream: " + dc.getDocument().getData());
                        break;
                    case REMOVED:
                        System.out.println("Removed Live Stream: " + dc.getDocument().getData());
                        break;
                    default:
                        break;
                }
            }
        });


    }


}
