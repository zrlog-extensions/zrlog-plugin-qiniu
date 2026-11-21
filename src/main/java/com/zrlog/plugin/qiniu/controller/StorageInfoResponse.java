package com.zrlog.plugin.qiniu.controller;

import com.zrlog.plugin.data.codec.PluginAppearance;

import com.zrlog.plugin.message.Plugin;

public class StorageInfoResponse<T> {

    private PluginAppearance appearance;

    private boolean dark;
    private String colorPrimary;
    private Plugin plugin;
    private StorageProvider provider;
    private T config;

    public boolean isDark() {
        return dark;
    }

    public void setDark(boolean dark) {
        this.dark = dark;
    }

    public String getColorPrimary() {
        return colorPrimary;
    }

    public void setColorPrimary(String colorPrimary) {
        this.colorPrimary = colorPrimary;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public StorageProvider getProvider() {
        return provider;
    }

    public void setProvider(StorageProvider provider) {
        this.provider = provider;
    }

    public T getConfig() {
        return config;
    }

    public void setConfig(T config) {
        this.config = config;
    }
    public void setAppearance(PluginAppearance appearance) {
        this.appearance = appearance;
    }
}
