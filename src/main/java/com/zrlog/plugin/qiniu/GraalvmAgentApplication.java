package com.zrlog.plugin.qiniu;

import com.zrlog.plugin.RunConstants;
import com.zrlog.plugin.type.RunType;
import com.zrlog.plugin.common.PluginNativeImageUtils;
import com.zrlog.plugin.qiniu.controller.QiniuController;
import com.zrlog.plugin.qiniu.controller.StorageApiResponse;
import com.zrlog.plugin.qiniu.controller.StorageInfoResponse;
import com.zrlog.plugin.qiniu.controller.StorageProvider;
import com.zrlog.plugin.qiniu.controller.WebsiteKeyRequest;
import com.zrlog.plugin.qiniu.service.QiniuStorageConfig;
import com.zrlog.plugin.qiniu.service.QiniuStaticSyncService;
import com.zrlog.plugin.qiniu.service.UploadService;
import com.zrlog.plugin.qiniu.service.UploadServiceRequest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class GraalvmAgentApplication {


    public static void main(String[] args) throws IOException, ReflectiveOperationException {
        RunConstants.runType = RunType.AGENT;
        //upload need set content-type
        PluginNativeImageUtils.usedGsonObject();
        PluginNativeImageUtils.gsonNativeAgentByClazz(Arrays.asList(UploadServiceRequest.class, QiniuStorageConfig.class,
                StorageApiResponse.class, StorageInfoResponse.class, StorageProvider.class, WebsiteKeyRequest.class));
        UploadService.class.getDeclaredConstructor().newInstance();
        QiniuStaticSyncService.class.getDeclaredConstructor().newInstance();
        String basePath = System.getProperty("user.dir").replace("\\target", "").replace("/target", "");
        //PathKit.setRootPath(basePath);
        File file = new File(basePath + "/src/main/resources");
        PluginNativeImageUtils.doLoopResourceLoad(file.listFiles(), file.getPath() + "/", "/");
        //Application.nativeAgent = true;
        PluginNativeImageUtils.exposeController(Collections.singletonList(QiniuController.class));
        Application.main(args);

    }
}
