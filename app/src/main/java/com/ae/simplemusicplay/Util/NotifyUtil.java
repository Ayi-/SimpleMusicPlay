package com.ae.simplemusicplay.Util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.ae.simplemusicplay.R;
import com.ae.simplemusicplay.services.MusicPlayService;

/**
 * Created by AE on 2016/1/4.
 */
public class NotifyUtil {
    /**
     * Notification管理
     */
    public static NotificationManager mNotificationManager;

    public static void showButtonNotify(Context context, boolean playFlag,String name,String singer) {

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.view_custom_button);
        mRemoteViews.setImageViewResource(R.id.custom_song_icon, R.mipmap.ic_launcher);
        //API3.0 以上的时候显示按钮，否则消失
        mRemoteViews.setTextViewText(R.id.tv_custom_song_singer, singer);
        mRemoteViews.setTextViewText(R.id.tv_custom_song_name, name);
        //如果版本号低于（3。0），那么不显示按钮
        if (getSystemVersion() <= 9) {
            mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.GONE);
        } else {
            mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
            //
            if (playFlag) {
                mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.mipmap.ic_pause_circle_outline_grey600_48dp);
            } else {
                mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.mipmap.ic_play_circle_outline_grey600_48dp);
            }
        }
/**
 //广播的action
 public static final String BROADCAST_BTN = "com.ae.simplemusicplay.services.btn";

 //按钮
 public static final int OP_PLAY = 0x01;
 public static final int OP_PAUSE = 0x02;
 public static final int OP_CONTINUE = 0x03;
 public static final int OP_NEXT = 0x04;
 public static final int OP_PREVIOUS = 0x05;
 **/
        //点击的事件处理
        Intent buttonIntent = new Intent(MusicPlayService.BROADCAST_BTN);
        /* 上一首按钮 */
        buttonIntent.putExtra("op", MusicPlayService.OP_PREVIOUS);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_prev = PendingIntent.getBroadcast(context, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_prev, intent_prev);
		/* 播放/暂停  按钮 */
        buttonIntent.putExtra("op", MusicPlayService.OP_PLAY);
        PendingIntent intent_paly = PendingIntent.getBroadcast(context, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_play, intent_paly);
		/* 下一首 按钮  */
        buttonIntent.putExtra("op", MusicPlayService.OP_NEXT);
        PendingIntent intent_next = PendingIntent.getBroadcast(context, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_next, intent_next);

        mBuilder.setContent(mRemoteViews)
                .setContentIntent(getDefalutIntent(context, Notification.FLAG_ONGOING_EVENT))
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker("正在播放")
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        //会报错，还在找解决思路
//		notify.contentView = mRemoteViews;
//		notify.contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
        mNotificationManager.notify(200, notify);
    }

    /**
     * 获取当前应用版本号
     *
     * @param context
     * @return version
     * @throws Exception
     */
    public static String getAppVersion(Context context) throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        String versionName = packInfo.versionName;
        return versionName;
    }

    /**
     * 获取当前系统SDK版本号
     */
    public static int getSystemVersion() {
		/*获取当前系统的android版本号*/
        int version = android.os.Build.VERSION.SDK_INT;
        return version;
    }



    /**
     * 清除当前创建的通知栏
     */
    public void clearNotify(int notifyId) {

        mNotificationManager.cancel(notifyId);//删除一个特定的通知ID对应的通知
//		mNotification.cancel(getResources().getString(R.string.app_name));
    }


    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    public static PendingIntent getDefalutIntent(Context context, int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, new Intent(), flags);
        return pendingIntent;
    }
}

