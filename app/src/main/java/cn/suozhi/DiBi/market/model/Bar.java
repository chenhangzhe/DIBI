package cn.suozhi.DiBi.market.model;

import androidx.annotation.NonNull;

/**
 * 可比较的Bar
 */
public class Bar implements Comparable<Bar> {

    private long time;
    private float open;
    private float high;
    private float low;
    private float close;
    private float volume;

    public Bar(long time, double open, double high, double low, double close, double volume) {
        this.time = time;
        this.open = (float) open;
        this.high = (float) high;
        this.low = (float) low;
        this.close = (float) close;
        this.volume = (float) volume;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public int compareTo(@NonNull Bar o) {
        return Long.compare(time, o.getTime());
    }
}
