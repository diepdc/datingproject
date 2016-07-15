package com.example.hoang.datingproject.Model;

import java.io.Serializable;

/**
 * Created by hoang on 4/5/2016.
 */
public class MessageModel implements Serializable{

    private String message_icon;
    private String message_pottime;
    private String message_content;
    private String nickname;
    private String friend_id;

    public MessageModel(String message_icon, String message_pottime, String message_content, String nickname, String friend_id) {
        this.message_icon = message_icon;
        this.message_pottime = message_pottime;
        this.message_content = message_content;
        this.nickname = nickname;
        this.friend_id = friend_id;
    }

    public String getMessage_icon() {
        return message_icon;
    }

    public void setMessage_icon(String message_icon) {
        this.message_icon = message_icon;
    }

    public String getMessage_pottime() {
        return message_pottime;
    }

    public void setMessage_pottime(String message_pottime) {
        this.message_pottime = message_pottime;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }
}
