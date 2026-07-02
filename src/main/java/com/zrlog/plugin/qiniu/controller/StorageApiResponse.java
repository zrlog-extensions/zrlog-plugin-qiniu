package com.zrlog.plugin.qiniu.controller;

public class StorageApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> StorageApiResponse<T> success(T data) {
        StorageApiResponse<T> response = new StorageApiResponse<T>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static StorageApiResponse<Object> success() {
        return success(null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
