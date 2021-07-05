package com.hoomicorp.hoomi.model.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.hoomicorp.hoomi.model.enums.Tag;

import java.util.List;

public class CategoryDto implements Parcelable {
    private String id;
    private String name;
    private String displayName;
    private List<Tag> tags;
    private String viewersCount;
    private String imageLink;

    public CategoryDto(String id, String name, String displayName, List<Tag> tags, String viewersCount, String imageLink) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.tags = tags;
        this.viewersCount = viewersCount;
        this.imageLink = imageLink;
    }

    public CategoryDto(String displayName, List<Tag> tags, String viewersCount, String imageLink) {
        this.displayName = displayName;
        this.name = displayName.toLowerCase();
        this.tags = tags;
        this.viewersCount = viewersCount;
        this.imageLink = imageLink;
    }

    public CategoryDto() {
    }


    protected CategoryDto(Parcel in) {
        id = in.readString();
        name = in.readString();
        displayName = in.readString();
        viewersCount = in.readString();
        imageLink = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(displayName);
        dest.writeString(viewersCount);
        dest.writeString(imageLink);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CategoryDto> CREATOR = new Creator<CategoryDto>() {
        @Override
        public CategoryDto createFromParcel(Parcel in) {
            return new CategoryDto(in);
        }

        @Override
        public CategoryDto[] newArray(int size) {
            return new CategoryDto[size];
        }
    };

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getViewersCount() {
        return viewersCount;
    }

    public void setViewersCount(String viewersCount) {
        this.viewersCount = viewersCount;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, CategoryDto.class);
    }
}
