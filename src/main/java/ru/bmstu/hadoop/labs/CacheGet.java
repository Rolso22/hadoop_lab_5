package ru.bmstu.hadoop.labs;

public class CacheGet {
    private final String url;
    private final int count;

    public CacheGet(String url, int count) {
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

