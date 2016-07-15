package com.example.hoang.datingproject.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by hoang on 4/4/2016.
 */
public class PersonModel implements Serializable{
    private String image;
    private String id;
    private String nickname;
    private String uid;

    public PersonModel() {}

    public PersonModel(String image, String nickname, String id) {
        this.image = image;
        this.nickname = nickname;
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
