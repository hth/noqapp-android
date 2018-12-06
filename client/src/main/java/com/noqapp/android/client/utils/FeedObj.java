package com.noqapp.android.client.utils;

import java.io.Serializable;

public class FeedObj implements Serializable {

    private String title;
    private String details;
    private String content;
    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public FeedObj setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public FeedObj setDetails(String details) {
        this.details = details;
        return this;
    }

    public String getContent() {
        return content;
    }

    public FeedObj setContent(String content) {
        this.content = content;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public FeedObj setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    @Override
    public String toString() {
        return "FeedObj{" +
                "title='" + title + '\'' +
                ", details='" + details + '\'' +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
