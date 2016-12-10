package com.fzb.io.yunstore;

import com.fzb.common.util.IOUtil;
import com.fzb.io.api.FileManageAPI;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.Etag;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class QiniuBucketManageImpl implements FileManageAPI {

    private Map<String, Object> responseData = new HashMap<String, Object>();

    private BucketVO bucket;
    private Auth auth;
    private BucketManager bucketManager;

    public QiniuBucketManageImpl(BucketVO bucket) {
        this.bucket = bucket;
        auth = Auth.create(bucket.getAccessKey(), bucket.getSecretKey());
        bucketManager = new BucketManager(auth);
    }

    @Override
    public Map<String, Object> delFile(String file) {
        /*Mac mac = new Mac(bucket.getAccessKey(), bucket.getSecretKey());
        RSClient client = new RSClient(mac);
        CallRet cr = client.delete(bucket.getBucketName(), file);
        responseData.put("statusCode", cr.statusCode);
        responseData.put("resp", cr.getResponse());*/
        return responseData;
    }

    @Override
    @Deprecated
    public Map<String, Object> delFolder(String folder) {
        return null;
    }

    @Override
    public Map<String, Object> create(File file) {
        // 生成一个新的文件名称  。不是太方便
        //String key = ParseTools.getRandomFileNameByOld(file).getName();
        return create(file, null);
    }

    @Override
    public Map<String, Object> create(File file, String key) {
        return create(file, key, false);
    }

    @Override
    public Map<String, Object> create(InputStream in, String key) {
        try {
            File file = File.createTempFile("qiniu", ".tmp");
            IOUtil.writeBytesToFile(IOUtil.getByteByInputStream(in), file);
            create(file, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Object> create(File file, String key, boolean deleteRepeat) {
        UploadManager uploadManager = new UploadManager();
        byte[] bytes;
        try {
            bytes = IOUtil.getByteByInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return responseData;
        }
        if (deleteRepeat) {
            try {
                FileInfo fileInfo = bucketManager.stat(bucket.getBucketName(), key);
                if (fileInfo != null) {
                    if (!Etag.data(bytes).equals(fileInfo.hash)) {
                        bucketManager.delete(bucket.getBucketName(), key);
                    }
                }
            } catch (QiniuException e) {
                e.printStackTrace();
            }
        }
        try {
            Response response = uploadManager.put(bytes, key, auth.uploadToken(bucket.getBucketName()));
            responseData.put("statusCode", response.statusCode);
            String url = "http://" + bucket.getHost() + "/" + key;
            /*ImageInfoRet infoRet = ImageInfo.call(url);
            if (infoRet.width > 600) {
                url += "?imageView2/2/w/600";
            }*/
            responseData.put("url", url);
            return responseData;
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        return responseData;
    }

    @Override
    public Map<String, Object> moveOrCopy(String filer, String tag,
                                          boolean isMove) {
        return null;
    }

    @Override
    public Map<String, Object> moveOrCopyFile(String src, String tag,
                                              boolean isMove) {
        return null;
    }

    @Override
    public Map<String, Object> CopyFileByInStream(InputStream in, String tag) {
        return null;
    }

    @Override
    public Map<String, Object> modifyFile(String root, String code,
                                          String content) {
        return null;
    }

    @Override
    public Map<String, Object> getFileList(String folder) {
        return null;
    }

}
