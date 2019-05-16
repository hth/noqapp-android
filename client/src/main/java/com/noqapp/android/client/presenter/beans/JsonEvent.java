package com.noqapp.android.client.presenter.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.noqapp.android.common.beans.AbstractDomain;

import java.io.Serializable;
import java.util.List;

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
public class JsonEvent extends AbstractDomain implements Serializable {

    @JsonProperty("ti")
    private String title;

    @JsonProperty("co")
    private String content;

    @JsonProperty("tac")
    private List<String> termAndConditions;

    @JsonProperty("ci")
    private String contentId;

    @JsonProperty("iu")
    private String imageUrl;


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

    public List<String> getTermAndConditions() {
        return termAndConditions;
    }

    public void setTermAndConditions(List<String> termAndConditions) {
        this.termAndConditions = termAndConditions;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JsonEvent{");
        sb.append("title='").append(title).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", termAndConditions=").append(termAndConditions);
        sb.append(", contentId='").append(contentId).append('\'');
        sb.append(", imageUrl='").append(imageUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

