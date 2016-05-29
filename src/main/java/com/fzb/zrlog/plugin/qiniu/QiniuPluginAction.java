package com.fzb.zrlog.plugin.qiniu;

import com.fzb.zrlog.plugin.IOSession;
import com.fzb.zrlog.plugin.api.IPluginAction;
import com.fzb.zrlog.plugin.data.codec.HttpRequestInfo;
import com.fzb.zrlog.plugin.data.codec.MsgPacket;
import com.fzb.zrlog.plugin.qiniu.controller.QiniuController;
import flexjson.JSONDeserializer;

public class QiniuPluginAction implements IPluginAction {
    @Override
    public void start(IOSession ioSession, MsgPacket msgPacket) {
        HttpRequestInfo httpRequestInfo = (HttpRequestInfo) (new JSONDeserializer()).deserialize(msgPacket.getDataStr());
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
