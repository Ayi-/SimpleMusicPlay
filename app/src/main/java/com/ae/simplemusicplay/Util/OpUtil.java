package com.ae.simplemusicplay.Util;

import android.net.Uri;

/**
 * Created by AE on 2016/1/4.
 */
public class OpUtil {

    //广播的action
    //按钮操作
    public static final String BROADCAST_BTN = "com.ae.simplemusicplay.services.btn";
    //退出应用
    public static final String BROADCAST_EXIT = "com.ae.simplemusicplay.MainActivity.exit";
    //SeekBar处理
    public static final String BROADCAST_SEEKBAR = "com.ae.simplemusicplay.services.seekbar";
    //修改播放界面歌手和歌曲名
    public static final String BROADCAST_PLAY_NAME_SINGER = "com.ae.simplemusicplay.playmusic.namesinger";



    //操作
    public static final int OP_PLAY = 0x01;
    public static final int OP_PAUSE = 0x02;
    public static final int OP_CONTINUE = 0x03;
    public static final int OP_NEXT = 0x04;
    public static final int OP_PREVIOUS = 0x05;

        public static final int NOTIFYID = 0x200;
    //获取专辑图片
    public static final Uri ARTISTURI = Uri.parse("content://media/external/audio/albumart");

}
