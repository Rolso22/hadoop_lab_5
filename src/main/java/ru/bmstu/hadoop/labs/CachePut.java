package ru.bmstu.hadoop.labs;

public class CacheMessage {
    private String url;
    private float result;

    public CacheMessage(String url, float result) {
        this.url = url;
        this.result = result;
    }

    public String getUrl() {
        return url;
    }

    public float getResult() {
        return result;
    }
}
