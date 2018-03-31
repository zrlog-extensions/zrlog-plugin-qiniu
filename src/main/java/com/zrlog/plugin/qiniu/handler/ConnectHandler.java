package com.zrlog.plugin.qiniu.handler;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.api.IConnectHandler;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.qiniu.timer.SyncTemplateStaticResourceTimerTask;

import java.util.Date;
import java.util.Timer;

public class ConnectHandler implements IConnectHandler {

    private static Timer timer = new Timer();

    @Override
    public void handler(IOSession ioSession, MsgPacket msgPacket) {
        timer.scheduleAtFixedRate(new SyncTemplateStaticResourceTimerTask(ioSession), new Date(), 1000);
    }
}
