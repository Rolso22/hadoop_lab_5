package ru.bmstu.hadoop.labs;

public class CacheMessage {
    private String url;
    private int count;
    private float result;

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

    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }
}
