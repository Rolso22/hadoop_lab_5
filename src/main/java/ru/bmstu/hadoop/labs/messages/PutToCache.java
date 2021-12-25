package ru.bmstu.hadoop.labs.messages;

public class PutToCache {
    private final String url;
    private final float result;
    private final int count;

    public PutToCache(String url, float result, int count) {
        this.url = url;
        this.result = result;
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public float getResult() {
        return result;
    }

    public int getCount() {
        return count;
    }
}
