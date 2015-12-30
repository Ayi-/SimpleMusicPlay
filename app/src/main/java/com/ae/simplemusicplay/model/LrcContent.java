package com.ae.simplemusicplay.model;

/**
 * Created by chen on 2015/12/26.
 */
public class LrcContent implements Comparable<LrcContent> {

    /**
     * 一行歌词
     */
    private String lrcStr;

    /**
     * 当前歌词的时间
     */
    private int lrcTime;

    public String getLrcStr() {
        return lrcStr;
    }

    public void setLrcStr(String lrcStr) {
        this.lrcStr = lrcStr;
    }

    public int getLrcTime() {
        return lrcTime;
    }

    public void setLrcTime(int lrcTime) {
        this.lrcTime = lrcTime;
    }

    @Override
    public int compareTo(LrcContent another) {
        return this.lrcTime - another.lrcTime;
    }
}
