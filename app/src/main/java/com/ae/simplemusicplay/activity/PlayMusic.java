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
import android.widget.Button;
import android.widget.TextView;

import com.ae.simplemusicplay.PlayList;
import com.ae.simplemusicplay.R;
import com.ae.simplemusicplay.services.MusicPlayService;

import java.util.List;

public class PlayMusic extends Activity implements View.OnClickListener{

    private TextView tb_name;
    //播放按钮
    private Button playBtn;

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
            tb_name.setText(playList.getCurrentSong().getSongName());
            myBinder.play();
            playBtn.setText("暂停");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("playactivity2", "create 1");

        setContentView(R.layout.activity_play_music);

        Log.i("playactivity2", "create 2");

        playList = PlayList.getInstance(this);
        tb_name = (TextView) findViewById(R.id.tv_name);
        playBtn = (Button) findViewById(R.id.btn_start);
        playBtn.setOnClickListener(this);
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
            case R.id.btn_start:
                if(playList.isPlaying()) {
                    myBinder.pause();
                    playBtn.setText("播放");
                }
                else {
                    myBinder.continueplay();
                    tb_name.setText(playList.getCurrentSong().getSongName());
                    playBtn.setText("暂停");
                }
        }
    }

    @Override
    protected void onDestroy() {

        unbindService(connection);
        super.onDestroy();
    }
}
