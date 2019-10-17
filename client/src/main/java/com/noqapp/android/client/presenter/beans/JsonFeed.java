package com.noqapp.android.client.presenter.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;

/**
 * hitender
 * 2018-12-07 12:35
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder(alphabetic = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonFeed extends AbstractDomain implements Parcelable {

    @JsonProperty("ti")
    private String title;

    @JsonProperty("co")
    private String content;

    @JsonProperty("ct")
    private String contentType;

    @JsonProperty("ci")
    private String contentId;

    @JsonProperty("iu")
    private String imageUrl;

    @JsonProperty("au")
    private String author;

    @JsonProperty("ai")
    private String authorThumbnail;

    @JsonProperty("ap")
    private String profession;

    @JsonProperty("wp")
    private String webProfileId;

    protected JsonFeed(Parcel in) {
        title = in.readString();
        content = in.readString();
        contentType = in.readString();
        contentId = in.readString();
        imageUrl = in.readString();
        author = in.readString();
        authorThumbnail = in.readString();
        profession = in.readString();
        webProfileId = in.readString();
    }

    public static final Creator<JsonFeed> CREATOR = new Creator<JsonFeed>() {
        @Override
        public JsonFeed createFromParcel(Parcel in) {
            return new JsonFeed(in);
        }

        @Override
        public JsonFeed[] newArray(int size) {
            return new JsonFeed[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public JsonFeed setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public JsonFeed setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public JsonFeed setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getContentId() {
        return contentId;
    }

    public JsonFeed setContentId(String contentId) {
        this.contentId = contentId;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public JsonFeed setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public JsonFeed setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getAuthorThumbnail() {
        return authorThumbnail;
    }

    public JsonFeed setAuthorThumbnail(String authorThumbnail) {
        this.authorThumbnail = authorThumbnail;
        return this;
    }

    public String getProfession() {
        return profession;
    }

    public JsonFeed setProfession(String profession) {
        this.profession = profession;
        return this;
    }

    public String getWebProfileId() {
        return webProfileId;
    }

    public JsonFeed setWebProfileId(String webProfileId) {
        this.webProfileId = webProfileId;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(contentType);
        dest.writeString(contentId);
        dest.writeString(imageUrl);
        dest.writeString(author);
        dest.writeString(authorThumbnail);
        dest.writeString(profession);
        dest.writeString(webProfileId);
    }
}

