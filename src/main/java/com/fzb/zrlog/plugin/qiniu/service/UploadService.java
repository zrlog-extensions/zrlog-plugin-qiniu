package com.fzb.zrlog.plugin.qiniu.service;

import com.fzb.io.api.FileManageAPI;
import com.fzb.io.yunstore.BucketVO;
import com.fzb.io.yunstore.QiniuBucketManageImpl;
import com.fzb.zrlog.plugin.IOSession;
import com.fzb.zrlog.plugin.api.IPluginService;
import com.fzb.zrlog.plugin.api.Service;
import com.fzb.zrlog.plugin.common.IdUtil;
import com.fzb.zrlog.plugin.common.response.UploadFileResponse;
import com.fzb.zrlog.plugin.common.response.UploadFileResponseEntry;
import com.fzb.zrlog.plugin.data.codec.ContentType;
import com.fzb.zrlog.plugin.data.codec.MsgPacket;
import com.fzb.zrlog.plugin.data.codec.MsgPacketStatus;
import com.fzb.zrlog.plugin.qiniu.entry.UploadFile;
import com.fzb.zrlog.plugin.type.ActionType;
import flexjson.JSONDeserializer;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("uploadService")
public class UploadService implements IPluginService {

    private static Logger LOGGER = Logger.getLogger(UploadService.class);

    @Override
    public void handle(final IOSession ioSession, final MsgPacket requestPacket) {
        Map<String, Object> request = new JSONDeserializer<Map<String, Object>>().deserialize(requestPacket.getDataStr());
        List<String> fileInfoList = (List<String>) request.get("fileInfo");
        List<UploadFile> uploadFileList = new ArrayList<>();
        for (String fileInfo : fileInfoList) {
            UploadFile uploadFile = new UploadFile();
            uploadFile.setFile(new File(fileInfo.split(",")[0]));
            String fileKey = fileInfo.split(",")[1];
            if (fileKey.startsWith("/")) {
                uploadFile.setFileKey(fileKey.substring(1, fileKey.length()));
            } else {
                uploadFile.setFileKey(fileKey);
            }
            uploadFileList.add(uploadFile);
        }
        UploadFileResponse uploadFileResponse = upload(ioSession, uploadFileList);
        List<Map<String, Object>> responseList = new ArrayList<>();
        for (UploadFileResponseEntry entry : uploadFileResponse) {
            Map<String, Object> map = new HashMap<>();
            map.put("url", entry.getUrl());
            responseList.add(map);
        }
        ioSession.sendMsg(ContentType.JSON, responseList, requestPacket.getMethodStr(), requestPacket.getMsgId(), MsgPacketStatus.RESPONSE_SUCCESS);
    }

    public UploadFileResponse upload(IOSession session, final List<UploadFile> uploadFileList) {
        final UploadFileResponse response = new UploadFileResponse();
        if (uploadFileList != null && !uploadFileList.isEmpty()) {
            final Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("key", "bucket,access_key,secret_key,host");
            int msgId = IdUtil.getInt();
            session.sendJsonMsg(keyMap, ActionType.GET_WEBSITE.name(), msgId, MsgPacketStatus.SEND_REQUEST, null);
            MsgPacket packet = session.getResponseMsgPacketByMsgId(msgId);
            Map<String, String> responseMap = new JSONDeserializer<Map<String, String>>().deserialize(packet.getDataStr());
            BucketVO bucket = new BucketVO(responseMap.get("bucket"), responseMap.get("access_key"),
                    responseMap.get("secret_key"), responseMap.get("host"));
            FileManageAPI man = new QiniuBucketManageImpl(bucket);
            for (UploadFile uploadFile : uploadFileList) {
                LOGGER.info("upload file " + uploadFile.getFile());
                UploadFileResponseEntry entry = new UploadFileResponseEntry();
                try {
                    entry.setUrl(man.create(uploadFile.getFile(), uploadFile.getFileKey(), true).get("url").toString());
                } catch (Exception e) {
                    LOGGER.error("upload error", e);
                    entry.setUrl(uploadFile.getFileKey());
                }
                response.add(entry);
            }
            LOGGER.info("upload file finish");
        }
        return response;
    }
}
