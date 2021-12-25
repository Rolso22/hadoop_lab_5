package ru.bmstu.hadoop.labs;

public class GetFromCache {
    private final String url;
    private final int count;

    public GetFromCache(String url, int count) {
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

