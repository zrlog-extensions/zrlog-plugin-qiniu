package com.fzb.zrlog.plugin.qiniu.timer;

import com.fzb.common.util.IOUtil;
import com.fzb.zrlog.plugin.IMsgPacketCallBack;
import com.fzb.zrlog.plugin.IOSession;
import com.fzb.zrlog.plugin.common.IdUtil;
import com.fzb.zrlog.plugin.common.modle.BlogRunTime;
import com.fzb.zrlog.plugin.common.modle.TemplatePath;
import com.fzb.zrlog.plugin.data.codec.ContentType;
import com.fzb.zrlog.plugin.data.codec.MsgPacket;
import com.fzb.zrlog.plugin.data.codec.MsgPacketStatus;
import com.fzb.zrlog.plugin.qiniu.entry.UploadFile;
import com.fzb.zrlog.plugin.qiniu.service.UploadService;
import com.fzb.zrlog.plugin.type.ActionType;
import flexjson.JSONDeserializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class SyncTemplateStaticResourceTimerTask extends TimerTask {

    private IOSession session;

    private Map<String, Long> fileWatcherMap = new HashMap<>();

    public SyncTemplateStaticResourceTimerTask(IOSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", "syncTemplate");
        session.sendJsonMsg(map, ActionType.GET_WEBSITE.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST, new IMsgPacketCallBack() {
            @Override
            public void handler(MsgPacket msgPacket) {
                Map<String, String> responseMap = new JSONDeserializer<Map<String, String>>().deserialize(msgPacket.getDataStr());
                if ("on".equals(responseMap.get("syncTemplate"))) {
                    TemplatePath templatePath = session.getResponseSync(ContentType.JSON, new HashMap(), ActionType.CURRENT_TEMPLATE, TemplatePath.class);
                    BlogRunTime blogRunTime = session.getResponseSync(ContentType.JSON, new HashMap(), ActionType.BLOG_RUN_TIME, BlogRunTime.class);
                    File templateFilePath = new File(blogRunTime.getPath() + templatePath.getValue());
                    System.out.println(blogRunTime.getPath());
                    if (templateFilePath.isDirectory()) {
                        File propertiesFile = new File(templateFilePath + "/template.properties");
                        System.out.println(propertiesFile);
                        if (propertiesFile.exists()) {
                            Properties prop = new Properties();
                            try {
                                prop.load(new FileInputStream(propertiesFile));
                                String staticResource = (String) prop.get("staticResource");
                                List<File> fileList = new ArrayList<>();
                                if (staticResource != null && !"".equals(staticResource)) {
                                    String[] staticFileArr = staticResource.split(",");
                                    for (String sFile : staticFileArr) {
                                        fileList.add(new File(templateFilePath + "/" + sFile));
                                    }
                                }
                                new UploadService().upload(session, convertToUploadFiles(fileList, blogRunTime.getPath()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

    }

    private List<UploadFile> convertToUploadFiles(List<File> files, String blogRootPath) {
        List<UploadFile> uploadFiles = new ArrayList<>();
        List<File> fullFileList = new ArrayList<>();
        for (File file : files) {
            fullFileList.addAll(IOUtil.getAllFiles(file.toString()));

        }
        if (!blogRootPath.endsWith("/")) {
            blogRootPath = blogRootPath + "/";
        }
        for (File file : fullFileList) {
            if (!file.isDirectory() && file.exists()) {
                if (fileWatcherMap.get(file.toString()) == null || fileWatcherMap.get(file.toString()) != file.lastModified()) {
                    UploadFile uploadFile = new UploadFile();
                    uploadFile.setFile(file);

                    String key = file.toString().substring(blogRootPath.length());
                    uploadFile.setFileKey(key);
                    System.out.println(key);
                    uploadFiles.add(uploadFile);
                    fileWatcherMap.put(file.toString(), file.lastModified());
                }
            }
        }
        return uploadFiles;
    }

}
