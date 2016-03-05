package com.fzb.zrlog.plugin.qiniu.service;

import com.fzb.io.api.FileManageAPI;
import com.fzb.io.yunstore.BucketVO;
import com.fzb.io.yunstore.QiniuBucketManageImpl;
import com.fzb.zrlog.plugin.IMsgPacketCallBack;
import com.fzb.zrlog.plugin.IOSession;
import com.fzb.zrlog.plugin.api.IPluginService;
import com.fzb.zrlog.plugin.api.Service;
import com.fzb.zrlog.plugin.common.IdUtil;
import com.fzb.zrlog.plugin.data.codec.ContentType;
import com.fzb.zrlog.plugin.data.codec.MsgPacket;
import com.fzb.zrlog.plugin.data.codec.MsgPacketStatus;
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
        final Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", "bucket,access_key,secret_key,host");
        ioSession.sendJsonMsg(keyMap, ActionType.GET_WEBSITE.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST, new IMsgPacketCallBack() {
            @Override
            public void handler(MsgPacket responseMsgPacket) {
                Map<String, String> responseMap = new JSONDeserializer<Map<String, String>>().deserialize(responseMsgPacket.getDataStr());
                Map<String,Object> request = new JSONDeserializer<Map<String, Object>>().deserialize(requestPacket.getDataStr());
                BucketVO bucket = new BucketVO(responseMap.get("bucket"), responseMap.get("access_key"),
                        responseMap.get("secret_key"), responseMap.get("host"));
                FileManageAPI man = new QiniuBucketManageImpl(bucket);
                List<Map<String, Object>> responseList = new ArrayList<>();
                List<String> fileInfoList = (List<String>) request.get("fileInfo");
                for (String fileInfo : fileInfoList) {
                    Map<String, Object> tempMap = new HashMap<>();
                    String newUrl = man.create(new File(fileInfo.split(",")[0]), fileInfo.split(",")[1]).get("url").toString();
                    tempMap.put("url", newUrl);
                    responseList.add(tempMap);
                }
                ioSession.sendMsg(ContentType.JSON, responseList, requestPacket.getMethodStr(), requestPacket.getMsgId(), MsgPacketStatus.RESPONSE_SUCCESS);
            }
        });
    }
}
