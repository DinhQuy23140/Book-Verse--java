package com.example.bookverse.models;

import java.util.Map;

public class Format {
    private Map<String, String> formats;

    public Format(Map<String, String> formats) {
        this.formats = formats;
    }

    public Map<String, String> getFormats() {
        return formats;
    }

    public void setFormats(Map<String, String> formats) {
        this.formats = formats;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (formats != null && !formats.isEmpty()) {
            for (Map.Entry<String, String> entry : formats.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        } else {
            sb.append("No formats available");
        }
        return sb.toString();
    }
}
