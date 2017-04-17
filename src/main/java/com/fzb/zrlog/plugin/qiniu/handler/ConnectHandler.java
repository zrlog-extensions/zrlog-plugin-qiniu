package com.fzb.zrlog.plugin.qiniu.handler;

import com.fzb.zrlog.plugin.IOSession;
import com.fzb.zrlog.plugin.api.IConnectHandler;
import com.fzb.zrlog.plugin.data.codec.MsgPacket;
import com.fzb.zrlog.plugin.qiniu.timer.SyncTemplateStaticResourceTimerTask;

import java.util.Date;
import java.util.Timer;

public class ConnectHandler implements IConnectHandler {

    private static Timer timer = new Timer();

    @Override
    public void handler(IOSession ioSession, MsgPacket msgPacket) {
        timer.scheduleAtFixedRate(new SyncTemplateStaticResourceTimerTask(ioSession), new Date(), 1000);
    }
}
