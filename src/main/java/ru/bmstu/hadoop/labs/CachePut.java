package ru.bmstu.hadoop.labs;

public class CachePut {
    private final String url;
    private final float result;
    private final int count;

    public CachePut(String url, float result, int count) {
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
