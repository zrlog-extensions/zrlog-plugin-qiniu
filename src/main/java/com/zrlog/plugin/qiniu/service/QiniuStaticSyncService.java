package com.zrlog.plugin.qiniu.service;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.api.Capability;
import com.zrlog.plugin.api.IPluginService;
import com.zrlog.plugin.api.ScheduledCapability;
import com.zrlog.plugin.api.Service;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.message.CapabilityInvokeResult;
import com.zrlog.plugin.qiniu.timer.SyncTemplateStaticResourceRunnable;

import java.util.HashMap;
import java.util.Map;

@Service("qiniu.syncStaticResources")
@Capability(key = "qiniu.syncStaticResources", riskLevel = "medium")
@ScheduledCapability(
        key = "qiniu.syncStaticResources",
        label = "同步七牛云静态资源",
        description = "同步模板静态资源到七牛云存储。",
        defaultCron = "*/5 * * * *",
        timeoutSeconds = 300
)
public class QiniuStaticSyncService implements IPluginService {

    @Override
    public void handle(IOSession session, MsgPacket msgPacket) {
        SyncTemplateStaticResourceRunnable runnable = new SyncTemplateStaticResourceRunnable(session);
        runnable.run();

        CapabilityInvokeResult result = new CapabilityInvokeResult();
        result.setSuccess(runnable.isSuccess());
        result.setErrorMessage(runnable.isSuccess() ? "" : runnable.getMessage());
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("filesCount", runnable.getFilesCount());
        data.put("message", runnable.getMessage());
        result.setData(data);
        session.sendJsonMsg(result, msgPacket.getMethodStr(), msgPacket.getMsgId(),
                result.isSuccess() ? MsgPacketStatus.RESPONSE_SUCCESS : MsgPacketStatus.RESPONSE_ERROR);
    }
}
