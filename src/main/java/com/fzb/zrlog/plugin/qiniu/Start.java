package com.fzb.zrlog.plugin.qiniu;


import com.fzb.zrlog.plugin.client.NioClient;
import com.fzb.zrlog.plugin.qiniu.controller.QiniuController;
import com.fzb.zrlog.plugin.qiniu.handler.ConnectHandler;
import com.fzb.zrlog.plugin.qiniu.service.UploadService;
import com.fzb.zrlog.plugin.render.FreeMarkerRenderHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Start {
    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        List<Class> classList = new ArrayList<>();
        classList.add(QiniuController.class);
        new NioClient(new ConnectHandler(), new FreeMarkerRenderHandler()).connectServerByProperties(args, classList, "/plugin.properties", QiniuPluginAction.class, UploadService.class);
    }
}

