package com.zrlog.plugin.qiniu.timer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.common.FileUtils;
import com.zrlog.plugin.common.IdUtil;
import com.zrlog.plugin.common.LoggerUtil;
import com.zrlog.plugin.common.SecurityUtils;
import com.zrlog.plugin.common.model.BlogRunTime;
import com.zrlog.plugin.common.model.TemplatePath;
import com.zrlog.plugin.common.response.UploadFileResponse;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.qiniu.entry.UploadFile;
import com.zrlog.plugin.qiniu.service.QiniuStorageConfig;
import com.zrlog.plugin.qiniu.service.UploadService;
import com.zrlog.plugin.type.ActionType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncTemplateStaticResourceRunnable implements Runnable {

    private static final Logger LOGGER = LoggerUtil.getLogger(SyncTemplateStaticResourceRunnable.class);
    private static final String CACHE_KEY = "cacheMap";
    private static final ReentrantLock LOCK = new ReentrantLock();

    private final IOSession session;
    private boolean success = true;
    private int filesCount;
    private String message = "";

    public SyncTemplateStaticResourceRunnable(IOSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        LOCK.lock();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("key", "syncTemplate," + CACHE_KEY);
            QiniuStorageConfig syncConfig = session.getResponseSync(ContentType.JSON, map, ActionType.GET_WEBSITE, QiniuStorageConfig.class);
            if (syncConfig == null) {
                markResult(true, 0, "未读取到静态同步配置。");
                return;
            }
            if (!syncConfig.isSyncTemplateEnabled()) {
                markResult(true, 0, "静态资源同步未启用。");
                return;
            }
            Map<String, String> fileInfoCacheMap = preloadCache(syncConfig);
            Map<String, String> nextFileInfoCacheMap = new HashMap<>(fileInfoCacheMap);
            TemplatePath templatePath = session.getResponseSync(ContentType.JSON, new HashMap<>(), ActionType.CURRENT_TEMPLATE, TemplatePath.class);
            BlogRunTime blogRunTime = session.getResponseSync(ContentType.JSON, new HashMap<>(), ActionType.BLOG_RUN_TIME, BlogRunTime.class);
            List<UploadFile> uploadFiles = templateUploadFiles(blogRunTime, templatePath, nextFileInfoCacheMap);
            if (uploadFiles.isEmpty()) {
                markResult(true, 0, "同步已完成，无新变动资源需要推送。");
                return;
            }
            UploadFileResponse uploadResponse = new UploadService().upload(session, uploadFiles);
            filesCount = uploadResponse == null ? uploadFiles.size() : uploadResponse.size();
            saveCacheToDb(nextFileInfoCacheMap);
            markResult(true, filesCount, "成功同步了 " + filesCount + " 个新增/变更资源。");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Sync error " + e.getMessage(), e);
            markResult(false, 0, "同步失败: " + e.getMessage());
        } finally {
            LOCK.unlock();
        }
    }

    private Map<String, String> preloadCache(QiniuStorageConfig syncConfig) {
        String cacheMapStr = syncConfig.getCacheMap();
        if (Objects.nonNull(cacheMapStr) && !cacheMapStr.isEmpty()) {
            return parseCacheMap(cacheMapStr);
        }
        return new HashMap<>();
    }

    private Map<String, String> parseCacheMap(String cacheMapStr) {
        JsonObject jsonObject = new Gson().fromJson(cacheMapStr, JsonObject.class);
        Map<String, String> cacheMap = new HashMap<>();
        if (jsonObject == null) {
            return cacheMap;
        }
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            JsonElement value = entry.getValue();
            if (value == null || value.isJsonNull()) {
                continue;
            }
            cacheMap.put(entry.getKey(), value.isJsonPrimitive() ? value.getAsString() : value.toString());
        }
        return cacheMap;
    }

    private void saveCacheToDb(Map<String, String> fileInfoCacheMap) {
        QiniuStorageConfig syncConfig = new QiniuStorageConfig();
        syncConfig.setCacheMap(new Gson().toJson(fileInfoCacheMap));
        session.sendJsonMsg(syncConfig, ActionType.SET_WEBSITE.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST);
    }

    private List<UploadFile> templateUploadFiles(BlogRunTime blogRunTime,
                                                 TemplatePath templatePath,
                                                 Map<String, String> fileInfoCacheMap) throws IOException {
        List<UploadFile> uploadFiles = new ArrayList<>();
        if (blogRunTime == null || templatePath == null) {
            return uploadFiles;
        }
        File templateFilePath = new File(blogRunTime.getPath() + templatePath.getValue());
        if (!templateFilePath.isDirectory()) {
            return uploadFiles;
        }
        File propertiesFile = new File(templateFilePath + "/template.properties");
        if (!propertiesFile.exists()) {
            return uploadFiles;
        }
        Properties prop = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(propertiesFile)) {
            prop.load(fileInputStream);
        }
        String staticResource = (String) prop.get("staticResource");
        if (staticResource == null || staticResource.isEmpty()) {
            return uploadFiles;
        }
        List<File> fileList = new ArrayList<>();
        String[] staticFileArr = staticResource.split(",");
        for (String staticFile : staticFileArr) {
            fileList.add(new File(templateFilePath + "/" + staticFile));
        }
        fillToUploadFiles(fileList, blogRunTime.getPath(), uploadFiles, fileInfoCacheMap);
        return uploadFiles;
    }

    private void fillToUploadFiles(List<File> files,
                                   String blogRootPath,
                                   List<UploadFile> uploadFiles,
                                   Map<String, String> fileInfoCacheMap) {
        List<File> fullFileList = new ArrayList<>();
        for (File file : files) {
            FileUtils.getAllFiles(file.toString(), fullFileList);
        }
        if (!blogRootPath.endsWith("/")) {
            blogRootPath = blogRootPath + "/";
        }
        for (File file : fullFileList) {
            if (!file.isFile() || !file.exists()) {
                continue;
            }
            String md5 = SecurityUtils.md5ByFile(file);
            if (Objects.equals(fileInfoCacheMap.get(file.toString()), md5)) {
                continue;
            }
            UploadFile uploadFile = new UploadFile();
            uploadFile.setFile(file);
            uploadFile.setFileKey(file.toString().substring(blogRootPath.length()));
            uploadFiles.add(uploadFile);
            fileInfoCacheMap.put(file.toString(), md5);
        }
    }

    private void markResult(boolean success, int filesCount, String message) {
        this.success = success;
        this.filesCount = filesCount;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getFilesCount() {
        return filesCount;
    }

    public String getMessage() {
        return message;
    }
}
