package com.zrlog.plugin.qiniu.service;

import com.fzb.io.api.FileManageAPI;
import com.fzb.io.yunstore.BucketVO;
import com.fzb.io.yunstore.QiniuBucketManageImpl;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.api.Capability;
import com.zrlog.plugin.api.IPluginService;
import com.zrlog.plugin.api.Service;
import com.zrlog.plugin.common.LoggerUtil;
import com.zrlog.plugin.common.response.UploadFileResponse;
import com.zrlog.plugin.common.response.UploadFileResponseEntry;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.message.CapabilityInvokeResult;
import com.zrlog.plugin.qiniu.entry.UploadFile;
import com.google.gson.Gson;
import com.zrlog.plugin.type.ActionType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service("uploadService")
@Capability(
        key = "qiniu.upload",
        type = "service",
        label = "上传到七牛云存储",
        description = "上传文章附件和生成资源到七牛云存储。",
        exposure = {"internal"},
        riskLevel = "medium",
        timeoutSeconds = 120
)
public class UploadService implements IPluginService {

    private static final Logger LOGGER = LoggerUtil.getLogger(UploadService.class);

    @Override
    public void handle(final IOSession ioSession, final MsgPacket requestPacket) {
        UploadServiceRequest rawRequest = new Gson().fromJson(requestPacket.getDataStr(), UploadServiceRequest.class);
        UploadServiceRequest request = requestPayload(requestPacket, rawRequest);
        List<String> fileInfoList = request.fileInfoList();
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
        if (Objects.equals(ActionType.CAPABILITY_INVOKE.name(), requestPacket.getMethodStr())) {
            CapabilityInvokeResult result = new CapabilityInvokeResult();
            result.setSuccess(true);
            Map<String, Object> data = new HashMap<>();
            data.put("items", uploadFileResponse);
            result.setData(data);
            ioSession.sendJsonMsg(result, requestPacket.getMethodStr(), requestPacket.getMsgId(), MsgPacketStatus.RESPONSE_SUCCESS);
        } else {
            ioSession.sendMsg(ContentType.JSON, uploadFileResponse, requestPacket.getMethodStr(), requestPacket.getMsgId(), MsgPacketStatus.RESPONSE_SUCCESS);
        }
    }

    private UploadServiceRequest requestPayload(MsgPacket requestPacket, UploadServiceRequest rawRequest) {
        if (rawRequest == null) {
            return new UploadServiceRequest();
        }
        if (Objects.equals(ActionType.CAPABILITY_INVOKE.name(), requestPacket.getMethodStr())
                && rawRequest.getPayload() != null) {
            return rawRequest.getPayload();
        }
        return rawRequest;
    }

    public UploadFileResponse upload(IOSession session, final List<UploadFile> uploadFileList) {
        final UploadFileResponse response = new UploadFileResponse();
        if (uploadFileList != null && !uploadFileList.isEmpty()) {
            final Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("key", "bucket,access_key,secret_key,host");
            QiniuStorageConfig config = session.getResponseSync(ContentType.JSON, keyMap, ActionType.GET_WEBSITE, QiniuStorageConfig.class);
            for (UploadFile uploadFile : uploadFileList) {
                BucketVO bucket = new BucketVO(config.getBucket(), config.getAccessKey(), config.getSecretKey(), config.getHost());
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
            LOGGER.log(Level.SEVERE, "upload file " + uploadFile.getFile() + " error", e);
            entry.setUrl(uploadFile.getFileKey());
        }
        return entry;
    }
}
