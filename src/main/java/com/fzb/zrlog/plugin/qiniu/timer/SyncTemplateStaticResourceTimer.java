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

public class SyncTemplateStaticResourceTimer extends TimerTask {

    private IOSession session;

    public SyncTemplateStaticResourceTimer(IOSession session) {
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
                    System.out.println(templateFilePath);
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
                                List<UploadFile> neeSyncFile = new ArrayList<>();
                                for (File file : fileList) {
                                    if (file.isDirectory()) {
                                        List<File> dirFiles = IOUtil.getAllFiles(file.toString());
                                        for (File f : dirFiles) {
                                            addToUploadFiles(neeSyncFile, f, blogRunTime.getPath());
                                        }
                                    } else {
                                        addToUploadFiles(neeSyncFile, file, blogRunTime.getPath());
                                    }
                                }
                                new UploadService().upload(session, neeSyncFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
        });

    }

    private void addToUploadFiles(List<UploadFile> uploadFiles, File f, String blogRootPath) {
        String key = f.toString().substring(blogRootPath.length() - 1);
        UploadFile uploadFile = new UploadFile();
        uploadFile.setFile(f);
        uploadFile.setFileKey(key);
        uploadFiles.add(uploadFile);
    }

}
