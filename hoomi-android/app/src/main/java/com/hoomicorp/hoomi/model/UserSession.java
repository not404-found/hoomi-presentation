package com.hoomicorp.hoomi.model;

import com.hoomicorp.hoomi.model.dto.UserInfoDto;

import java.util.Objects;

public class UserSession {
    private UserInfoDto userInfo;
    private boolean inLive;
//    private String id;
//    private String username;
//    private String email;
//    private String phoneNumber;
//    private String pwd;
//    private String profileImageLink;

    private static UserSession instance = new UserSession();

    private UserSession() {

    }

    public static UserSession getInstance() {
        return instance;
    }

    public void setUserInfo(final UserInfoDto userInfo) {
        this.userInfo = userInfo;
//        this.id = userInfo.getId();
//        this.username = userInfo.getUsername();
//        this.profileImageLink = userInfo.getProfileImageLink();
//        this.email = userInfo.getEmail();
//        this.phoneNumber = userInfo.getPhoneNum();

    }

    public UserInfoDto getUserInfo() {
        return userInfo;
    }

    public String getId() {
        return userInfo.getId();
    }

    public String getUsername() {
        return userInfo.getUsername();
    }


    public String getProfileImageLink() {
        return userInfo.getProfileImageLink();
    }


    public String getEmail() {
        return userInfo.getEmail();
    }

    public String getPhoneNumber() {
        return userInfo.getPhoneNum();
    }

    public boolean isInLive() {
        return inLive;
    }

    public void setInLive(boolean inLive) {
        this.inLive = inLive;
    }
}
