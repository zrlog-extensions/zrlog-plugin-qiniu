package com.zrlog.plugin.qiniu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qiniu.util.Json;
import com.zrlog.plugin.common.PluginNativeImageUtils;
import com.zrlog.plugin.qiniu.controller.QiniuController;
import com.zrlog.plugin.qiniu.service.UploadService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class GraalvmAgentApplication {


    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException {
        //upload need set content-type
        PluginNativeImageUtils.usedGsonObject();
        //Class<?> aClass = Class.forName("com.qiniu.util.UC$UCRet");
        //Object decode = Json.decode("{}", aClass);
        //System.out.println("decode = " + decode);
        //RefreshObjectCachesResponse refreshObjectCachesResponse = new RefreshObjectCachesResponse();
        //refreshObjectCachesResponse.setRefreshTaskId("");
        //refreshObjectCachesResponse.setRequestId("");
        //new Gson().toJson(refreshObjectCachesResponse);
        GsonBuilder builder = new GsonBuilder();
        //RefreshObjectCachesRequest refreshObjectCachesRequest = new RefreshObjectCachesRequest();
        //refreshObjectCachesRequest.setObjectPath("Test");
        //builder.create().toJson(refreshObjectCachesRequest);

        new Gson().toJson(new TreeMap<>());
        new Gson().fromJson("{}", Map.class);
        //new RefreshCdnWorker("test", "test", "oss-cn-chengdu.aliyuncs.com").start(Arrays.asList("https://blog.zrlog.com/?"));
        UploadService.class.newInstance();
        //UploadToPrivateService.class.newInstance();
        String basePath = System.getProperty("user.dir").replace("\\target", "").replace("/target", "");
        //PathKit.setRootPath(basePath);
        File file = new File(basePath + "/src/main/resources");
        PluginNativeImageUtils.doLoopResourceLoad(file.listFiles(), file.getPath() + "/", "/");
        //Application.nativeAgent = true;
        PluginNativeImageUtils.exposeController(Collections.singletonList(QiniuController.class));
        Application.main(args);

    }
}