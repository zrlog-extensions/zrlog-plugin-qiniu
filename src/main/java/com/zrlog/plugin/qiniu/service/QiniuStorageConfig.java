package com.zrlog.plugin.qiniu.service;

import com.google.gson.annotations.SerializedName;

public class QiniuStorageConfig {

    private String bucket;
    @SerializedName("access_key")
    private String accessKey;
    @SerializedName("secret_key")
    private String secretKey;
    private String host;
    private String syncTemplate;
    private String cacheMap;
    private String version;

    public boolean isSyncTemplateEnabled() {
        return "on".equals(syncTemplate);
    }

    public void normalizeForPage(String version) {
        this.syncTemplate = switchValue(syncTemplate);
        this.version = version;
    }

    private String switchValue(String value) {
        if ("on".equals(value)) {
            return "on";
        }
        return "off";
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSyncTemplate() {
        return syncTemplate;
    }

    public void setSyncTemplate(String syncTemplate) {
        this.syncTemplate = syncTemplate;
    }

    public String getCacheMap() {
        return cacheMap;
    }

    public void setCacheMap(String cacheMap) {
        this.cacheMap = cacheMap;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
