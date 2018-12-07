package com.noqapp.android.client.utils;

import java.io.Serializable;

public class FeedObj implements Serializable {

    private String title;
    private String content;
    private String contentType = "Health";
    private String imageUrl;
    private String author;
    private String authorPic;
    private String authorProfession;
    private String authorWebProfileId;


    public String getTitle() {
        return title;
    }

    public FeedObj setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public FeedObj setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public FeedObj setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public FeedObj setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public FeedObj setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getAuthorPic() {
        return authorPic;
    }

    public FeedObj setAuthorPic(String authorPic) {
        this.authorPic = authorPic;
        return this;
    }

    public String getAuthorProfession() {
        return authorProfession;
    }

    public FeedObj setAuthorProfession(String authorProfession) {
        this.authorProfession = authorProfession;
        return this;
    }

    public String getAuthorWebProfileId() {
        return authorWebProfileId;
    }

    public FeedObj setAuthorWebProfileId(String authorWebProfileId) {
        this.authorWebProfileId = authorWebProfileId;
        return this;
    }

    @Override
    public String toString() {
        return "FeedObj{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", contentType='" + contentType + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", author='" + author + '\'' +
                ", authorPic='" + authorPic + '\'' +
                ", authorProfession='" + authorProfession + '\'' +
                ", authorWebProfileId='" + authorWebProfileId + '\'' +
                '}';
    }
}
