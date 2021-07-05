package com.hoomicorp.hoomi.model.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;

import java.util.List;


public class PostDto implements Parcelable {
    private String postId;
    private String postName;
    private String postImageLink;
    private String postPreviewLink;
    private String livesStreamLink;
    private String recordingLink;

    private String userId;
    private String username;
    private String userProfileImageLink;

    private long scheduledDateTime;
    private long streamedDateTime;
    private long updatedDateTime;

    private String categoryName;
    private String categoryId;
    private List<String> tags;

    private Double cost;

    private Integer likedUsersCount;
    private Integer dislikedUsersCount;

    private boolean removed;
    private boolean live;

    public PostDto() {
    }


    protected PostDto(Parcel in) {
        postId = in.readString();
        postName = in.readString();
        postImageLink = in.readString();
        postPreviewLink = in.readString();
        livesStreamLink = in.readString();
        recordingLink = in.readString();
        userId = in.readString();
        username = in.readString();
        userProfileImageLink = in.readString();
        scheduledDateTime = in.readLong();
        streamedDateTime = in.readLong();
        updatedDateTime = in.readLong();
        categoryName = in.readString();
        categoryId = in.readString();
        tags = in.createStringArrayList();
        if (in.readByte() == 0) {
            cost = null;
        } else {
            cost = in.readDouble();
        }
        if (in.readByte() == 0) {
            likedUsersCount = null;
        } else {
            likedUsersCount = in.readInt();
        }
        if (in.readByte() == 0) {
            dislikedUsersCount = null;
        } else {
            dislikedUsersCount = in.readInt();
        }
        removed = in.readByte() != 0;
        live = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postId);
        dest.writeString(postName);
        dest.writeString(postImageLink);
        dest.writeString(postPreviewLink);
        dest.writeString(livesStreamLink);
        dest.writeString(recordingLink);
        dest.writeString(userId);
        dest.writeString(username);
        dest.writeString(userProfileImageLink);
        dest.writeLong(scheduledDateTime);
        dest.writeLong(streamedDateTime);
        dest.writeLong(updatedDateTime);
        dest.writeString(categoryName);
        dest.writeString(categoryId);
        dest.writeStringList(tags);
        if (cost == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(cost);
        }
        if (likedUsersCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(likedUsersCount);
        }
        if (dislikedUsersCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(dislikedUsersCount);
        }
        dest.writeByte((byte) (removed ? 1 : 0));
        dest.writeByte((byte) (live ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostDto> CREATOR = new Creator<PostDto>() {
        @Override
        public PostDto createFromParcel(Parcel in) {
            return new PostDto(in);
        }

        @Override
        public PostDto[] newArray(int size) {
            return new PostDto[size];
        }
    };

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getPostImageLink() {
        return postImageLink;
    }

    public void setPostImageLink(String postImageLink) {
        this.postImageLink = postImageLink;
    }

    public String getPostPreviewLink() {
        return postPreviewLink;
    }

    public void setPostPreviewLink(String postPreviewLink) {
        this.postPreviewLink = postPreviewLink;
    }

    public String getLivesStreamLink() {
        return livesStreamLink;
    }

    public void setLivesStreamLink(String livesStreamLink) {
        this.livesStreamLink = livesStreamLink;
    }

    public String getRecordingLink() {
        return recordingLink;
    }

    public void setRecordingLink(String recordingLink) {
        this.recordingLink = recordingLink;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserProfileImageLink() {
        return userProfileImageLink;
    }

    public void setUserProfileImageLink(String userProfileImageLink) {
        this.userProfileImageLink = userProfileImageLink;
    }

    public long getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(long scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public long getStreamedDateTime() {
        return streamedDateTime;
    }

    public void setStreamedDateTime(long streamedDateTime) {
        this.streamedDateTime = streamedDateTime;
    }

    public long getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(long updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getLikedUsersCount() {
        return likedUsersCount;
    }

    public void setLikedUsersCount(Integer likedUsersCount) {
        this.likedUsersCount = likedUsersCount;
    }

    public Integer getDislikedUsersCount() {
        return dislikedUsersCount;
    }

    public void setDislikedUsersCount(Integer dislikedUsersCount) {
        this.dislikedUsersCount = dislikedUsersCount;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public static Creator<PostDto> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, PostDto.class);
    }
}
