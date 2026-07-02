package com.zrlog.plugin.qiniu.controller;

public class StorageProvider {

    private String key;
    private String title;
    private String helpUrl;
    private String regionLabel;
    private boolean privateBucket;
    private boolean appId;
    private boolean syncHtml;
    private boolean supportHttps;

    public StorageProvider() {
    }

    public StorageProvider(String key, String title, String helpUrl, String regionLabel,
                           boolean privateBucket, boolean appId, boolean syncHtml, boolean supportHttps) {
        this.key = key;
        this.title = title;
        this.helpUrl = helpUrl;
        this.regionLabel = regionLabel;
        this.privateBucket = privateBucket;
        this.appId = appId;
        this.syncHtml = syncHtml;
        this.supportHttps = supportHttps;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getHelpUrl() {
        return helpUrl;
    }

    public String getRegionLabel() {
        return regionLabel;
    }

    public boolean isPrivateBucket() {
        return privateBucket;
    }

    public boolean isAppId() {
        return appId;
    }

    public boolean isSyncHtml() {
        return syncHtml;
    }

    public boolean isSupportHttps() {
        return supportHttps;
    }
}
