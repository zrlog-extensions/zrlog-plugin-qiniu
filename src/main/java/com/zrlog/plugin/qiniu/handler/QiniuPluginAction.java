package com.zrlog.plugin.qiniu.handler;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.api.IPluginAction;
import com.zrlog.plugin.data.codec.HttpRequestInfo;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.qiniu.controller.QiniuController;
import com.google.gson.Gson;

public class QiniuPluginAction implements IPluginAction {
    @Override
    public void start(IOSession ioSession, MsgPacket msgPacket) {
        HttpRequestInfo httpRequestInfo = new Gson().fromJson(msgPacket.getDataStr(),HttpRequestInfo.class);
        new QiniuController(ioSession, msgPacket, httpRequestInfo).index();
    }

    @Override
    public void stop(IOSession ioSession, MsgPacket msgPacket) {

    }

    @Override
    public void install(IOSession ioSession, MsgPacket msgPacket, HttpRequestInfo httpRequestInfo) {
        new QiniuController(ioSession, msgPacket, httpRequestInfo).index();
    }

    @Override
    public void uninstall(IOSession ioSession, MsgPacket msgPacket) {

    }
}
