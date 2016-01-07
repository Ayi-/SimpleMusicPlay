package com.ae.simplemusicplay.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ae.simplemusicplay.PlayList;
import com.ae.simplemusicplay.Util.NotifyUtil;
import com.ae.simplemusicplay.Util.OpUtil;
import com.ae.simplemusicplay.Util.SharePreferenceUtils;
import com.ae.simplemusicplay.model.SongInfo;

import java.io.IOException;

import static com.ae.simplemusicplay.Util.NotifyUtil.showButtonNotify;


/**
 * Created by dell on 2015/12/29.
 */
public class MusicPlayService extends Service implements MediaPlayer.OnCompletionListener /*implements IMusicService*/ {
    private MediaPlayer mediaPlayer;       //媒体播放器对象
    //定义binder
    private PlayBinder mybinder = new PlayBinder();
    //歌曲列表
    private PlayList playList;
    //设置参数用的工具类
    private SharePreferenceUtils sharePreferenceUtils;
    //定义一个广播，用来按钮操作
    private MyBroadCast receiver;
    //定义一个广播，用来执行退出
    private ExitBroadCast receiverExit;

    //歌曲id
    private int songid;
    //进度条处理
    private Handler seekHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        //获取列表
        playList = PlayList.getInstance(getApplicationContext());

        if (playList.getListsize() > 0) {
            sharePreferenceUtils = SharePreferenceUtils.getInstance(getApplicationContext());
            //创建播放器对象
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            //初始化handler
            seekHandler = new Handler();
            Log.i("service", playList.getListsize() + "");
            Log.i("service", "create service");
            songid = -1;

            if (playList != null)
                if (!playList.isPlaying()) {
                    //设置最后播放歌曲资源
                    try {
                        mediaPlayer.setDataSource(playList.getCurrentSong().getPath());
                        //必须在prepare()之前调用这个
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            /**
             * 注册按钮操作广播
             */
            receiver = new MyBroadCast();
            IntentFilter filter = new IntentFilter();
            filter.addAction(OpUtil.BROADCAST_BTN);
            registerReceiver(receiver, filter);
            /**
             * 注册退出事件广播
             */
            Log.i("initservice", "startBroadCast");

            receiverExit = new ExitBroadCast();
            IntentFilter filterExit = new IntentFilter();
            filterExit.addAction(OpUtil.BROADCAST_EXIT);
            registerReceiver(receiverExit, filterExit);
            Log.i("initservice", "init ok!");

        }
        super.onCreate();
    }

    //更新进度条
    Runnable runnableSeekBar = new Runnable() {
        @Override
        public void run() {
            if (playList.isPlaying()) {
                int position = mediaPlayer.getCurrentPosition();
                Intent intent = new Intent();
                intent.setAction(OpUtil.BROADCAST_SEEKBAR);
                intent.putExtra("progress", position);
                playList.setCurrentPos(position);
                sharePreferenceUtils.setCurrentPos(position);
                Log.i("run progress", position + "");
                sendBroadcast(intent);

                seekHandler.postDelayed(this, 1000);
            }
        }
    };

    /**
     * 播放音乐
     */
    public void playMusic(SongInfo song) {
        //检测当前播放歌曲的id与正要播放的歌的id是否一致，一致就不进行操作
        playList.setIsPlaying(true);

        if (song.getId() != songid || !mediaPlayer.isPlaying()) {
            songid = song.getId();
            //如果正在播放音乐
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }
            //修改为新的播放资源
            try {
                Log.i("playMusic", "playMusic");

                //设置歌曲资源
                mediaPlayer.setDataSource(song.getPath());
                //必须在prepare()之前调用这个
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.start();

                seekHandler.removeCallbacks(runnableSeekBar);
                seekHandler.postDelayed(runnableSeekBar, 0);

            } catch (IOException e) {
                e.printStackTrace();
            }

            /**
             * 通知设置
             */
            showButtonNotify(getApplicationContext(), playList.isPlaying(), song.getSongName(), song.getAlbumId());
            //发送修改UI界面广播
            Intent intent = new Intent();
            intent.setAction(OpUtil.BROADCAST_PLAY_NAME_SINGER);
            sendBroadcast(intent);
            //保存当前播放歌曲和记录

            sharePreferenceUtils.setCurrentPos(mediaPlayer.getCurrentPosition());
            sharePreferenceUtils.setCurrentSongId(playList.getCurrent());
            Log.i("main handle", sharePreferenceUtils.getCurrentSongId() + "");
            Log.i("main handle", sharePreferenceUtils.getCurrentPos() + "");

        }
    }

    /**
     * 下一首
     */
    public void playNext() {
        playMusic(playList.getNext());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i("completion", "next");
        seekHandler.removeCallbacks(runnableSeekBar);
        playNext();
        //发送修改UI界面广播
        Intent intent = new Intent();
        intent.setAction(OpUtil.BROADCAST_PLAY_NAME_SINGER);
        sendBroadcast(intent);
    }

    /**
     * 上一首
     */
    public void playPrevious() {
        playMusic(playList.getPrevious());
    }

    /**
     * 暂停
     */
    //@Override
    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playList.setIsPlaying(false);
            SongInfo song = playList.getCurrentSong();
            /**
             * 通知设置
             */
            showButtonNotify(getApplicationContext(), playList.isPlaying(), song.getSongName(), song.getAlbumId());
            //发送修改UI界面广播
            Intent intent = new Intent();
            intent.setAction(OpUtil.BROADCAST_PLAY_NAME_SINGER);
            sendBroadcast(intent);
        }
    }

    /**
     * 音乐停止
     */
    //@Override
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 继续播放音乐
     */
    public void continuePlay() {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(playList.getCurrentPos());
            mediaPlayer.start();
            playList.setIsPlaying(true);

            seekHandler.removeCallbacks(runnableSeekBar);
            seekHandler.postDelayed(runnableSeekBar, 0);
            SongInfo song = playList.getCurrentSong();
            /**
             * 通知设置
             */
            showButtonNotify(getApplicationContext(), playList.isPlaying(), song.getSongName(), song.getAlbumId());
            //发送修改UI界面广播
            Intent intent = new Intent();
            intent.setAction(OpUtil.BROADCAST_PLAY_NAME_SINGER);
            sendBroadcast(intent);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("onBind", "return  Bind");


        return mybinder;
    }

    /**
     * 音乐操作
     */
    public class PlayBinder extends Binder {

        public void play() {
            playMusic(playList.getCurrentSong());
        }

        public void pause() {
            pauseMusic();
        }

        public void next() {
            playNext();
        }

        public void previous() {
            playPrevious();
        }

        public void continueplay() {
            continuePlay();
        }

        public void stop() {

        }

    }

    //广播 用来接收通知栏按钮
    public class MyBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            int op = intent.getIntExtra("op", -1);
            Log.i("BTN Broadcast", "get");
            Log.i("BTN Broadcast", op + "");
            switch (op) {
                case OpUtil.OP_PLAY:
                    playMusic(playList.getCurrentSong());
                    break;
                case OpUtil.OP_PAUSE:
                    pauseMusic();
                    break;
                case OpUtil.OP_CONTINUE:
                    continuePlay();
                    break;
                case OpUtil.OP_NEXT:
                    playNext();
                    break;
                case OpUtil.OP_PREVIOUS:
                    playPrevious();
                    break;

            }
        }
    }

    //广播 用来接收退出
    public class ExitBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("exit", "exit");
            MusicPlayService.this.onDestroy();

        }
    }


    @Override
    public void onDestroy() {
        Log.i("destroy", "cancle notify");
        //关闭通知栏
        if (NotifyUtil.mNotificationManager != null)
            NotifyUtil.clearNotify(OpUtil.NOTIFYID);
        Log.i("destroy", "destroy  mediaPlayer");
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Log.i("destroy", "destroy  playList");
        if (playList != null)
            playList.setIsPlaying(false);
        Log.i("destroy", "destroy  seekHandler");
        //销毁handler
        seekHandler.removeCallbacks(runnableSeekBar);
        Log.i("destroy", "unregister  receiverExit");

        //注销广播
        try {
            if (receiverExit != null) {
                unregisterReceiver(receiverExit);
            }
        } catch (Exception e) {
            receiverExit = null;
        }
        Log.i("destroy", "unregister  receiver");

        try {
            if (receiver != null)
                unregisterReceiver(receiver);
        } catch (Exception e) {
            receiver = null;
        }
        stopSelf();
        super.onDestroy();
    }
}
