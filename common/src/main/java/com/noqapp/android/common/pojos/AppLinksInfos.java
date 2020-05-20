package com.noqapp.android.common.pojos;

public class AppLinksInfos {

    private String appPackageId;
    private String appBannerUrl;
    private String title;
    private String description;


    public String getAppPackageId() {
        return appPackageId;
    }

    public AppLinksInfos setAppPackageId(String appPackageId) {
        this.appPackageId = appPackageId;
        return this;
    }

    public String getAppBannerUrl() {
        return appBannerUrl;
    }

    public AppLinksInfos setAppBannerUrl(String appBannerUrl) {
        this.appBannerUrl = appBannerUrl;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AppLinksInfos setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AppLinksInfos setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AppLinksInfos{");
        sb.append("appPackageId='").append(appPackageId).append('\'');
        sb.append(", appBannerUrl='").append(appBannerUrl).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
