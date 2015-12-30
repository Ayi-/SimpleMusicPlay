package com.ae.simplemusicplay.model;

import org.litepal.crud.DataSupport;

public class SongInfo extends DataSupport implements Comparable<SongInfo>{

    private int id;

    private int songId;

    private String songName;

    private String albumName;

    private int albumId;

    private String artistName;

    private String path;

    private String lyrcUrl;

    private String pinyin;

    private int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getLyrcUrl() {
        return lyrcUrl;
    }

    public void setLyrcUrl(String lyrcUrl) {
        this.lyrcUrl = lyrcUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    @Override
    public int compareTo(SongInfo another) {
        return this.getPinyin().compareTo(another.getPinyin());
    }
}