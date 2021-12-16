package ru.bmstu.hadoop.labs;

public class CacheMessage {
    private final String url;
    private final int count;

    public CacheMessage(String url, int count) {
        this.url = url;
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public int getCount() {
        return count;
    }
}
