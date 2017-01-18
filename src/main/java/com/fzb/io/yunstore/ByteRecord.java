package com.fzb.io.yunstore;

import com.google.gson.Gson;
import com.qiniu.storage.Recorder;

import java.util.Map;

public class ByteRecord implements Recorder {

    private long totalByteSize;

    public ByteRecord(long totalByteSize) {
        this.totalByteSize = totalByteSize;
    }

    @Override
    public void set(String s, byte[] bytes) {
        Map map = new Gson().fromJson(new String(bytes), Map.class);
        Double uploaded = (Double) map.get("offset");
        System.out.println("uploading " + s.substring(0, s.indexOf("_._")) + " " + (int) Math.ceil(uploaded * 100 / totalByteSize * 1.0) + "%");
    }

    @Override
    public byte[] get(String s) {
        return "{}".getBytes();
    }

    @Override
    public void del(String s) {

    }
}
