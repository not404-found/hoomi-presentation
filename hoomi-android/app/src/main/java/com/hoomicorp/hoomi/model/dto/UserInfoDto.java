package com.hoomicorp.hoomi.model.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserInfoDto implements Parcelable {

    private String id;
    private String username;
    private String searchName;
    private String profileImageLink;
    private String email;
    private String phoneNum;
    private long updatedDateTime;
    private boolean liked;
    private boolean disliked;
    private boolean saved;
    private Double donationSum;
    private List<String> tags = new ArrayList<>();

    public UserInfoDto() {
    }


    protected UserInfoDto(Parcel in) {
        id = in.readString();
        username = in.readString();
        searchName = in.readString();
        profileImageLink = in.readString();
        email = in.readString();
        phoneNum = in.readString();
        updatedDateTime = in.readLong();
        liked = in.readByte() != 0;
        disliked = in.readByte() != 0;
        saved = in.readByte() != 0;
        if (in.readByte() == 0) {
            donationSum = null;
        } else {
            donationSum = in.readDouble();
        }
        tags = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(searchName);
        dest.writeString(profileImageLink);
        dest.writeString(email);
        dest.writeString(phoneNum);
        dest.writeLong(updatedDateTime);
        dest.writeByte((byte) (liked ? 1 : 0));
        dest.writeByte((byte) (disliked ? 1 : 0));
        dest.writeByte((byte) (saved ? 1 : 0));
        if (donationSum == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(donationSum);
        }
        dest.writeStringList(tags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserInfoDto> CREATOR = new Creator<UserInfoDto>() {
        @Override
        public UserInfoDto createFromParcel(Parcel in) {
            return new UserInfoDto(in);
        }

        @Override
        public UserInfoDto[] newArray(int size) {
            return new UserInfoDto[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getProfileImageLink() {
        return profileImageLink;
    }

    public void setProfileImageLink(String profileImageLink) {
        this.profileImageLink = profileImageLink;
    }

    public long getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(long updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isDisliked() {
        return disliked;
    }

    public void setDisliked(boolean disliked) {
        this.disliked = disliked;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public Double getDonationSum() {
        return donationSum;
    }

    public void setDonationSum(Double donationSum) {
        this.donationSum = donationSum;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, UserInfoDto.class);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfoDto that = (UserInfoDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(username, that.username) &&
                Objects.equals(profileImageLink, that.profileImageLink) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phoneNum, that.phoneNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, profileImageLink, email, phoneNum);
    }
}
