package com.zrlog.plugin.qiniu.service;

import com.fzb.io.api.FileManageAPI;
import com.fzb.io.yunstore.BucketVO;
import com.fzb.io.yunstore.QiniuBucketManageImpl;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.api.IPluginService;
import com.zrlog.plugin.api.Service;
import com.zrlog.plugin.common.IdUtil;
import com.zrlog.plugin.common.LoggerUtil;
import com.zrlog.plugin.common.response.UploadFileResponse;
import com.zrlog.plugin.common.response.UploadFileResponseEntry;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.qiniu.entry.UploadFile;
import com.google.gson.Gson;
import com.zrlog.plugin.type.ActionType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service("uploadService")
public class UploadService implements IPluginService {

    private static final Logger LOGGER = LoggerUtil.getLogger(UploadService.class);

    @Override
    public void handle(final IOSession ioSession, final MsgPacket requestPacket) {
        Map<String, Object> request = new Gson().fromJson(requestPacket.getDataStr(), Map.class);
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
            Map<String, String> responseMap = new Gson().fromJson(packet.getDataStr(), Map.class);
            for (UploadFile uploadFile : uploadFileList) {
                BucketVO bucket = new BucketVO(responseMap.get("bucket"), responseMap.get("access_key"), responseMap.get("secret_key"), responseMap.get("host"));
                response.add(doUpload(bucket, uploadFile));
            }
        }
        return response;
    }

    private UploadFileResponseEntry doUpload(BucketVO bucketVO, UploadFile uploadFile) {
        UploadFileResponseEntry entry = new UploadFileResponseEntry();
        try {
            FileManageAPI man = new QiniuBucketManageImpl(bucketVO);
            entry.setUrl(man.create(uploadFile.getFile(), uploadFile.getFileKey(), true).get("url").toString());
            LOGGER.info("upload file " + uploadFile.getFile() + " success");
        } catch (Exception e) {
            LOGGER.severe("upload file " + uploadFile.getFile() + " error, " + e.getMessage());
            entry.setUrl(uploadFile.getFileKey());
        }
        return entry;
    }
}
