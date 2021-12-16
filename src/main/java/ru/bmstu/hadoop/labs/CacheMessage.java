package ru.bmstu.hadoop.labs;

public class CacheMessage {
    private final String url;
    private final int count;
    private final float result;

    public CacheMessage(String url, int count, float result) {
        this.url = url;
        this.count = count;
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public String getUrl() {
        return url;
    }

    public float getResult() {
        return result;
    }
}
