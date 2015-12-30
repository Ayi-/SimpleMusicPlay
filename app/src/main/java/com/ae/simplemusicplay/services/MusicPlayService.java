package com.ae.simplemusicplay.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ae.simplemusicplay.model.SongInfo;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by dell on 2015/12/29.
 */
public class MusicPlayService extends Service /*implements IMusicService*/ {
    private MediaPlayer mediaPlayer =  new MediaPlayer();       //媒体播放器对象
    private String path;                        //音乐文件路径
    private boolean isPause=false;                    //暂停状态
    private int currentSongId = -1;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    //播放音乐
    //@Override
    public void playMusic(String listName, int position) {

    }

    /**
     * 播放音乐
     * @param songId
     *     音乐id
     */
    public void playMusic(int songId) {
        SongInfo songInfo = DataSupport.find(SongInfo.class,songId);

        //如果正在播放音乐
        if(mediaPlayer!=null)
        {
            mediaPlayer.reset();
        }

        //修改为新的播放资源
        try {
            this.currentSongId = songId;
            mediaPlayer.setDataSource(songInfo.getPath());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * 通知设置
         */
    }

    /**
     * 暂停
     */
    //@Override
    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 音乐停止
     */
    //@Override
    public void stopMusic() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //@Override
    public ArrayList<SongInfo> getCurrentMusicList() {
        return null;
    }

    //@Override
    public ArrayList<SongInfo> getMusicListByName(Integer listId) {
        return null;
    }

    //@Override
//    public ArrayList<MusicList> getMusiclistList() {
//        return null;
//    }

    //@Override
    public void addNetWorkMusic(SongInfo music, Integer listId) {

    }

    //@Override
    public Integer findMusicOnMobile(Boolean filter) {
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 音乐操作
     */
    class PlayBinder extends Binder{

        public void play(int musicId){
            playMusic(musicId);
        }

        public void pause(){
            pauseMusic();
        }

        public void next(){

        }

        public void previous(){

        }

        public void stop(){

        }

    }

}
