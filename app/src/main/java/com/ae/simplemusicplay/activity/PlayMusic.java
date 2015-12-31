package com.ae.simplemusicplay.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ae.simplemusicplay.PlayList;
import com.ae.simplemusicplay.R;
import com.ae.simplemusicplay.services.MusicPlayService;
import com.ae.simplemusicplay.widgets.CircleImageView;
import com.ae.simplemusicplay.widgets.CircularSeekBar;

import java.util.List;

public class PlayMusic extends Activity implements View.OnClickListener {

    //歌曲名
    private TextView tv_name;
    //歌手名
    private TextView tv_singer;

    //播放按钮
    private ImageButton imgbtn_play;
    //上一首
    private ImageButton previous;
    //下一首
    private ImageButton next;

    //设置binder，用来和服务通信
    private MusicPlayService.PlayBinder myBinder;
    //歌曲列表
    private PlayList playList;
    //临时使用Binder连接
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("onServiceConnected", "onServiceConnected");

            myBinder = (MusicPlayService.PlayBinder) service;
            //开始播放
            tv_name.setText(playList.getCurrentSong().getSongName());
            tv_singer.setText(playList.getCurrentSong().getArtistName());
            imgbtn_play.setImageResource(R.mipmap.ic_pause_circle_outline_black_48dp);
            myBinder.play();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        CircleImageView image = (CircleImageView) findViewById(R.id.album_art);
        CircularSeekBar seekBar = (CircularSeekBar) findViewById(R.id.song_progress_circular);
        image.setImageResource(R.mipmap.test_icon);
        seekBar.setMax(100);
        seekBar.setProgress(25);

        Log.i("playactivity2", "create 1");
        Log.i("playactivity2", "create 2");

        //获取播放列表
        playList = PlayList.getInstance(this);

        //获取文本框和按钮

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_singer = (TextView) findViewById(R.id.tv_singer);
        imgbtn_play = (ImageButton) findViewById(R.id.imgbtn_play);
        previous = (ImageButton) findViewById(R.id.previous);
        next = (ImageButton) findViewById(R.id.next);
        imgbtn_play.setOnClickListener(this);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        //启动服务
        Log.i("playactivity2", "init setvice");

        initServiceBinder();
    }


    public void initServiceBinder() {
        //先检查服务是否已经先启动，然后再启动服务
        Log.i("initservice", MusicPlayService.class.getName());

        if (!isWorked(MusicPlayService.class.getName())) {
            Log.i("initservice", "start service");
            Intent startIntent = new Intent(getApplicationContext(), MusicPlayService.class);
            //设置服务不自动重新启动
            startIntent.setFlags(Service.START_NOT_STICKY);
            startService(startIntent);
        }
        Log.i("initservice", "bindService");

        Intent bindIntent = new Intent(getApplicationContext(), MusicPlayService.class);
        //BIND_AUTO_CREATE会自动创建服务（如果服务并没有start）,这里设置0（不会自动start服务）
        bindService(bindIntent, connection, 0);
    }

    //查询所有服务，检查服务是否已经启动
    boolean isWorked(String className) {
        ActivityManager manager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        boolean res = false;
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(50);
        if (services.size() <= 0) {
            res = false;
        }
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).service.getClassName().equals(className)) {
                res = true;
                break;
            }
        }
        return res;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_play:
                if (playList.isPlaying()) {
                    myBinder.pause();
                    imgbtn_play.setImageResource(R.mipmap.ic_play_circle_outline_black_48dp);

                } else {
                    myBinder.continueplay();
                    tv_name.setText(playList.getCurrentSong().getSongName());
                    tv_singer.setText(playList.getCurrentSong().getArtistName());
                    imgbtn_play.setImageResource(R.mipmap.ic_pause_circle_outline_black_48dp);

                }
        }
    }

    @Override
    protected void onDestroy() {

        unbindService(connection);
        super.onDestroy();
    }
}
