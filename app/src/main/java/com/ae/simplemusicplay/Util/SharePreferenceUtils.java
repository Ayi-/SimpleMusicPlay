package com.ae.simplemusicplay.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by chen on 2015/12/17.
 * Modify:Ae 2015/12/30.
 */
public class SharePreferenceUtils {

    //设置单例
    private static SharePreferenceUtils utils;

    //获取操作对象
    private SharedPreferences preferences;

    private static final String fileName = "SETTING_PREFERENCE";
    private SharedPreferences.Editor editor;

    public static SharePreferenceUtils getInstance(Context context) {
        if (utils == null) {
            synchronized (SharePreferenceUtils.class) {
                utils = new SharePreferenceUtils(context);
            }
        }
        return utils;
    }

    private SharePreferenceUtils(Context context) {
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    //应用是否第一次运行
    public boolean isFirstTimeUse() {
        boolean res = preferences.getBoolean("firstTime", true);
        return res;
    }

    public void setNotFirst() {
        editor.putBoolean("firstTime", false);
        editor.commit();
    }

    //定时
    public void setTimeToLeft(int time) {
        editor.putInt("timeToLeave", time);
        editor.commit();
    }

    public int getLeaveTime() {
        int res = preferences.getInt("timeToLeave", -1);
        return res;
    }

    //设置播放模式
    //0顺序
    //1随机
    //2单曲
    public void setPlayMode(int mode) {
        editor.putInt("playMode", mode);
        editor.commit();
    }

    public int getPlayMode() {
        int res = preferences.getInt("playMode", 0);
        return res;
    }

    //设置扫描音乐完成标志
    public void setScanFlag(boolean flag) {
        editor.putBoolean("scanflag", flag);
        editor.commit();
    }
    //获取扫描音乐完成标志
    public boolean getScanFlag() {
        boolean res = preferences.getBoolean("scanflag", false);
        return res;
    }

    //设置最后播放歌曲id
    public void setCurrentSongId(int id) {
        editor.putInt("setCurrentSong", id);
        editor.commit();
    }
    //获取最后播放歌曲id
    public int getCurrentSongId() {
        int res = preferences.getInt("setCurrentSong", 0);
        return res;
    }
    //设置最后播放歌曲进度
    public void setCurrentPos(int id) {
        editor.putInt("setCurrentPos", id);
        editor.commit();
    }
    //获取最后播放歌曲进取
    public int getCurrentPos() {
        int res = preferences.getInt("setCurrentPos", 0);
        return res;
    }
}
