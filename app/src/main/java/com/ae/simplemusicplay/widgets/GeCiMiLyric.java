package com.ae.simplemusicplay.widgets;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by chen on 2015/12/16.
 */
public class GeCiMiLyric extends DataSupport {


    /**
     * 返回数量
     */
    private int count;
    /**
     * 返回状态
     */
    private int code;
    /**
     * aid : 1563419
     * artist_id : 9208
     * sid : 1668536
     * lrc : http://s.geci.me/lrc/166/16685/1668536.lrc
     * song : 海阔天空
     */

    private List<ResultEntity> result;

    public void setCount(int count) {
        this.count = count;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public int getCode() {
        return code;
    }

    public List<ResultEntity> getResult() {
        return result;
    }

    public static class ResultEntity {
        //aid 和 lrc 有用
        private int aid;
        private int artist_id;
        private int sid;
        private String lrc;
        private String song;

        public void setAid(int aid) {
            this.aid = aid;
        }

        public void setArtist_id(int artist_id) {
            this.artist_id = artist_id;
        }

        public void setSid(int sid) {
            this.sid = sid;
        }

        public void setLrc(String lrc) {
            this.lrc = lrc;
        }

        public void setSong(String song) {
            this.song = song;
        }

        public int getAid() {
            return aid;
        }

        public int getArtist_id() {
            return artist_id;
        }

        public int getSid() {
            return sid;
        }

        public String getLrc() {
            return lrc;
        }

        public String getSong() {
            return song;
        }
    }
}
