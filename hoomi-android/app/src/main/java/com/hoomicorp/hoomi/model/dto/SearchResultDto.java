package com.hoomicorp.hoomi.model.dto;

import com.google.gson.GsonBuilder;
import com.hoomicorp.hoomi.model.enums.Tag;

import java.util.List;

public class SearchResultDto {
    private String id;
    private String name;
    private String imageLink;
    private boolean isStreamer;
    private List<Tag> tags;

    public SearchResultDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public boolean isStreamer() {
        return isStreamer;
    }

    public void setStreamer(boolean streamer) {
        isStreamer = streamer;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, SearchResultDto.class);
    }
}
