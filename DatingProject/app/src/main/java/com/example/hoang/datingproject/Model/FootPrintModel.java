package com.example.hoang.datingproject.Model;

/**
 * Created by hoang on 6/6/2016.
 */
public class FootPrintModel {

    private String nickname;
    private String userID;
    private String address;
    private String profileImage;
    private String posttime;

    public FootPrintModel() {}

    public FootPrintModel(String nickname, String userID, String address, String profileImage, String posttime) {
        this.nickname = nickname;
        this.userID = userID;
        this.address = address;
        this.profileImage = profileImage;
        this.posttime = posttime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }
}
