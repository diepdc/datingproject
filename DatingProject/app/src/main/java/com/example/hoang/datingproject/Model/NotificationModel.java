package com.example.hoang.datingproject.Model;

/**
 * Created by hoang on 5/26/2016.
 */
public class NotificationModel {

    private String title;
    private String content;
    private String posttime;

    public NotificationModel(String title, String content, String posttime) {
        this.title = title;
        this.content = content;
        this.posttime = posttime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }
}
