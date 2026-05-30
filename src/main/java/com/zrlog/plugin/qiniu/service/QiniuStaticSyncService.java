package com.zrlog.plugin.qiniu.service;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.api.IPluginService;
import com.zrlog.plugin.api.ScheduledCapability;
import com.zrlog.plugin.api.Service;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.message.CapabilityInvokeResult;
import com.zrlog.plugin.qiniu.timer.SyncTemplateStaticResourceTimerTask;

import java.util.HashMap;
import java.util.Map;

@Service("qiniu.syncStaticResources")
@ScheduledCapability(
        key = "qiniu.syncStaticResources",
        label = "同步七牛云静态资源",
        description = "同步模板静态资源到七牛云存储",
        defaultCron = "*/5 * * * *",
        timeoutSeconds = 300
)
public class QiniuStaticSyncService implements IPluginService {

    @Override
    public void handle(IOSession session, MsgPacket msgPacket) {
        CapabilityInvokeResult result = new CapabilityInvokeResult();
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            new SyncTemplateStaticResourceTimerTask(session).run();
            result.setSuccess(true);
            data.put("message", "Qiniu static resources sync completed");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            data.put("message", e.getMessage());
        }
        result.setData(data);
        session.sendJsonMsg(result, msgPacket.getMethodStr(), msgPacket.getMsgId(),
                result.isSuccess() ? MsgPacketStatus.RESPONSE_SUCCESS : MsgPacketStatus.RESPONSE_ERROR);
    }
}
