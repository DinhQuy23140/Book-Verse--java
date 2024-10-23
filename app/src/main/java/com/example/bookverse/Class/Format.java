package com.example.bookverse.Class;

import java.util.Map;

public class Format {

    private Map<String, String> mimeUrlMap;

    public Format(Map<String, String> mimeUrlMap) {
        this.mimeUrlMap = mimeUrlMap;
    }

    public Map<String, String> getMimeUrlMap() {
        return mimeUrlMap;
    }

    public void setMimeUrlMap(Map<String, String> mimeUrlMap) {
        this.mimeUrlMap = mimeUrlMap;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (mimeUrlMap != null) {
            for (Map.Entry<String, String> entry : mimeUrlMap.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        } else {
            sb.append("No formats available");
        }
        return sb.toString();
    }
}
