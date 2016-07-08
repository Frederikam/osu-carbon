package com.frederikam.osucarbon.io;

public class Line {

    public final String path;
    public final String value;
    public final long timestamp;

    public Line(String path, String value) {
        this.path = path;
        this.value = value;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public Line(String path, String value, long timestamp) {
        this.path = path;
        this.value = value;
        this.timestamp = timestamp;
    }
    
    public Line(String path, long value) {
        this.path = path;
        this.value = String.valueOf(value);
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public Line(String path, long value, long timestamp) {
        this.path = path;
        this.value = String.valueOf(value);
        this.timestamp = timestamp;
    }
    
    public Line(String path, float value) {
        this.path = path;
        this.value = String.valueOf(value);
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public Line(String path, float value, long timestamp) {
        this.path = path;
        this.value = String.valueOf(value);
        this.timestamp = timestamp;
    }
    
    public Line(String path, double value) {
        this.path = path;
        this.value = String.valueOf(value);
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public Line(String path, double value, long timestamp) {
        this.path = path;
        this.value = String.valueOf(value);
        this.timestamp = timestamp;
    }

    public String getData() {
        return path + " " + value + " " + timestamp;
    }
    
    public String getDataAsLine() {
        return getData() + "\n";
    }

}
