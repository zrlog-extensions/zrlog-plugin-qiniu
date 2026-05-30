package com.zrlog.plugin.qiniu.controller;

import com.google.gson.Gson;
import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.common.IdUtil;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.data.codec.HttpRequestInfo;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.type.ActionType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by xiaochun on 2016/2/13.
 */
public class QiniuController {

    private static final String CONFIG_KEYS = "access_key,host,secret_key,bucket,syncTemplate";

    private final IOSession session;
    private final MsgPacket requestPacket;
    private final HttpRequestInfo requestInfo;
    private final Gson gson = new Gson();

    public QiniuController(IOSession session, MsgPacket requestPacket, HttpRequestInfo requestInfo) {
        this.session = session;
        this.requestPacket = requestPacket;
        this.requestInfo = requestInfo;
    }

    public void update() {
        session.sendMsg(new MsgPacket(requestInfo.simpleParam(), ContentType.JSON, MsgPacketStatus.SEND_REQUEST, IdUtil.getInt(), ActionType.SET_WEBSITE.name()), msgPacket -> {
            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            session.sendMsg(new MsgPacket(map, ContentType.JSON, MsgPacketStatus.RESPONSE_SUCCESS, requestPacket.getMsgId(), requestPacket.getMethodStr()));
        });
    }

    public void info() {
        response(loadConfig());
    }

    public void index() {
        Map<String, Object> data = new HashMap<>();
        data.put("theme", isDarkMode() ? "dark" : "light");
        data.put("data", gson.toJson(pageData()));
        session.responseHtml("/templates/index", data, requestPacket.getMethodStr(), requestPacket.getMsgId());
    }

    public void json() {
        response(pageData());
    }

    private Map<String, Object> pageData() {
        Map<String, Object> data = new HashMap<>();
        data.put("dark", isDarkMode());
        data.put("colorPrimary", getAdminColorPrimary());
        data.put("plugin", session.getPlugin());
        data.put("provider", provider());
        data.put("config", loadConfig());
        return successMap(data);
    }

    private Map<String, Object> provider() {
        Map<String, Object> provider = new HashMap<>();
        provider.put("key", "qiniu");
        provider.put("title", "七牛云对象存储设置");
        provider.put("helpUrl", "https://blog.zrlog.com/qiniu-install.html");
        provider.put("regionLabel", "");
        provider.put("privateBucket", false);
        provider.put("appId", false);
        provider.put("syncHtml", false);
        provider.put("supportHttps", false);
        return provider;
    }

    private Map<String, Object> loadConfig() {
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", CONFIG_KEYS);
        Map response = session.getResponseSync(ContentType.JSON, keyMap, ActionType.GET_WEBSITE, Map.class);
        Map<String, Object> config = response == null ? new HashMap<>() : new HashMap<>(response);
        normalizeSwitch(config, "syncTemplate");
        config.put("version", session.getPlugin().getVersion());
        return config;
    }

    private void normalizeSwitch(Map<String, Object> config, String key) {
        if (!Objects.equals(config.get(key), "on")) {
            config.put(key, "off");
        }
    }

    private Map<String, Object> successMap(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("data", data);
        return map;
    }

    private void response(Map<String, Object> map) {
        session.sendMsg(ContentType.JSON, map, requestPacket.getMethodStr(), requestPacket.getMsgId(), MsgPacketStatus.RESPONSE_SUCCESS);
    }

    private boolean isDarkMode() {
        return requestInfo.isDarkMode();
    }

    private String getAdminColorPrimary() {
        return requestInfo.getAdminColorPrimary();
    }
}
