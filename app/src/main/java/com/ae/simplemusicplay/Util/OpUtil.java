package com.ae.simplemusicplay.Util;

/**
 * Created by AE on 2016/1/4.
 */
public class OpUtil {

    //广播的action
    //按钮操作
    public static final String BROADCAST_BTN = "com.ae.simplemusicplay.services.btn";
    //退出应用
    public static final String BROADCAST_EXIT = "com.ae.simplemusicplay.MainActivity.exit";

    //操作
    public static final int OP_PLAY = 0x01;
    public static final int OP_PAUSE = 0x02;
    public static final int OP_CONTINUE = 0x03;
    public static final int OP_NEXT = 0x04;
    public static final int OP_PREVIOUS = 0x05;

}
