package com.example.hoang.datingproject.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by hoang on 4/14/2016.
 */
public class InboxModel implements Serializable{
    private String time;
    private String content;
    private int id;
    private Bitmap image;
    private String sender;
    private String profileImage;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public InboxModel(){}

    public InboxModel(String mtime, String mcontent, String sender, String profileImage) {
        this.time = mtime;
        this.content = mcontent;
        this.sender = sender;
        this.profileImage = profileImage;
        this.image = null;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public InboxModel(String mtime,Bitmap bitmap, String sender, String profileImage) {
        this.time = mtime;
        this.image = bitmap;
        this.sender = sender;
        this.profileImage = profileImage;
        this.content = null;

    }

    public static class Builder {

        private Bitmap mImage;
        private String mMessage;
        private String time;
        private String sender;
        private String profileImage;

        public Builder() {}

        public Builder image(Bitmap image, String mtime, String msender, String mprofileImage) {
            mImage = image;
            time = mtime;
            sender = msender;
            profileImage = mprofileImage;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public InboxModel build() {
            InboxModel message = new InboxModel();
            message.setImage(mImage);
            message.setContent(mMessage);
            message.setTime(time);
            return message;
        }
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
