package com.ae.simplemusicplay.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ae.simplemusicplay.PlayList;
import com.ae.simplemusicplay.model.SongInfo;

import java.io.IOException;

import static com.ae.simplemusicplay.Util.NotifyUtil.showButtonNotify;


/**
 * Created by dell on 2015/12/29.
 */
public class MusicPlayService extends Service implements MediaPlayer.OnCompletionListener /*implements IMusicService*/ {
    private MediaPlayer mediaPlayer;       //媒体播放器对象

    //广播的action
    public static final String BROADCAST_BTN = "com.ae.simplemusicplay.services.btn";

    //按钮
    public static final int OP_PLAY = 0x01;
    public static final int OP_PAUSE = 0x02;
    public static final int OP_CONTINUE = 0x03;
    public static final int OP_NEXT = 0x04;
    public static final int OP_PREVIOUS = 0x05;

    private PlayBinder mybinder = new PlayBinder();
    //歌曲列表
    private PlayList playList;
    //定义一个广播，用来按钮操作
    private MyBroadCast receiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {

        //创建播放器对象
        mediaPlayer = new MediaPlayer();
        //
        playList = PlayList.getInstance(getApplicationContext());
        Log.i("service", playList.getListsize() + "");
        Log.i("service", "create service");

        /**
         * 注册广播
         */
        receiver= new MyBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicPlayService.BROADCAST_BTN);
        registerReceiver(receiver, filter);

        super.onCreate();
    }

    //播放音乐
    //@Override
    public void playMusic(String listName, int position) {

    }

    /**
     * 播放音乐
     */
    public void playMusic(SongInfo song) {

        //如果正在播放音乐
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
        //修改为新的播放资源
        try {
            Log.i("playMusic", "playMusic");
            mediaPlayer.setDataSource(song.getPath());
            //必须在prepare()之前调用这个
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
            playList.setIsPlaying(true);
        } catch (IOException e) {
            e.printStackTrace();
        }


        /**
         * 通知设置
         */
        showButtonNotify(getApplicationContext(),playList.isPlaying(),song.getSongName(),song.getArtistName());
    }

    /**
     * 下一首
     */
    public void playNext() {
        playMusic(playList.getNext());
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
            mediaPlayer.start();
            playList.setIsPlaying(true);
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext();
        Intent intent = new Intent();
        intent.setAction("com.chenjiayao.musicplayer.next");
        sendBroadcast(intent);
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
            switch (op) {
                case OP_PLAY:
                    playMusic(playList.getCurrentSong());
                    break;
                case OP_PAUSE:
                    pauseMusic();
                    break;
                case OP_CONTINUE:
                    continuePlay();
                    break;
                case OP_NEXT:
                    playNext();
                    break;
                case OP_PREVIOUS:
                    playPrevious();
                    break;

            }
        }
    }


}
