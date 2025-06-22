package com.zrlog.plugin.qiniu;


import com.zrlog.plugin.client.NioClient;
import com.zrlog.plugin.qiniu.controller.QiniuController;
import com.zrlog.plugin.qiniu.handler.ConnectHandler;
import com.zrlog.plugin.qiniu.handler.QiniuPluginAction;
import com.zrlog.plugin.qiniu.service.UploadService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application {
    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        List<Class> classList = new ArrayList<>();
        classList.add(QiniuController.class);
        new NioClient(new ConnectHandler(), null).connectServer(args, classList, QiniuPluginAction.class, UploadService.class);
    }
}

