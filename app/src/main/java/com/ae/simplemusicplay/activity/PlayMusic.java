package com.ae.simplemusicplay.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ae.simplemusicplay.PlayList;
import com.ae.simplemusicplay.R;
import com.ae.simplemusicplay.Util.OpUtil;
import com.ae.simplemusicplay.model.SongInfo;
import com.ae.simplemusicplay.services.MusicPlayService;
import com.ae.simplemusicplay.widgets.CircleImageView;
import com.ae.simplemusicplay.widgets.CircularSeekBar;

import static com.ae.simplemusicplay.Util.StartService.startservice;

public class PlayMusic extends Activity implements View.OnClickListener {

    //歌曲名
    private TextView tv_name;
    //歌手名
    private TextView tv_singer;

    //播放按钮
    private ImageButton imgbtn_play_play;
    //上一首
    private ImageButton imgbtn_previous_play;
    //下一首
    private ImageButton imgbtn_next_play;
    //歌词容器
    private FrameLayout lyricLayout;
    //中间圆形图片及拖动条容器
    private RelativeLayout circleImageLayout;
    private CircularSeekBar seekBar;
    private CircleImageView circleImage;
    //设置binder，用来和服务通信
    private MusicPlayService.PlayBinder myBinder;
    //歌曲列表
    private PlayList playList;

    //定义一个广播，用来执行退出
    private SeekBroadCast receiverSeek;
    //定义一个广播，用来修改UI界面
    private NameSingerBroadCast receiverNameSinger;

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
            //修改UI界面
            changeUI();

            myBinder.play();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //注册广播
        receiverSeek = new SeekBroadCast();
        IntentFilter filterExit = new IntentFilter();
        filterExit.addAction(OpUtil.BROADCAST_SEEKBAR);
        registerReceiver(receiverSeek, filterExit);

        receiverNameSinger = new NameSingerBroadCast();
        IntentFilter filterNameSinger = new IntentFilter();
        filterNameSinger.addAction(OpUtil.BROADCAST_PLAY_NAME_SINGER);
        registerReceiver(receiverNameSinger, filterNameSinger);

        //歌词容器
        lyricLayout = (FrameLayout) findViewById(R.id.lyric_layout);
        //中间圆形图片及拖动条容器
        circleImageLayout = (RelativeLayout) findViewById(R.id.circle_image_layout);
        //中间圆形图片设置点击事件：显示歌词
        circleImage = (CircleImageView) findViewById(R.id.album_art);
        circleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lyricLayout.getVisibility() == View.VISIBLE) {
                    lyricLayout.setVisibility(View.INVISIBLE);
                    circleImageLayout.setVisibility(View.VISIBLE);
                } else {
                    lyricLayout.setVisibility(View.VISIBLE);
                    circleImageLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
        lyricLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lyricLayout.getVisibility() == View.VISIBLE) {
                    lyricLayout.setVisibility(View.INVISIBLE);
                    circleImageLayout.setVisibility(View.VISIBLE);
                } else {
                    lyricLayout.setVisibility(View.VISIBLE);
                    circleImageLayout.setVisibility(View.INVISIBLE);
                }
            }
        });


        //CircleImageView image = (CircleImageView) findViewById(R.id.album_art);
        //image.setImageResource(R.mipmap.test_icon);

        //获取播放列表
        playList = PlayList.getInstance(this);
        //初始化seekBar
        seekBar = (CircularSeekBar) findViewById(R.id.song_progress_circular);
        seekBar.setMax((int) playList.getCurrentSong().getDuration());
        seekBar.setProgress(playList.getCurrentPos());
        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                //拖动进度条完毕后
                int progress = seekBar.getProgress();
                //更新进度
                playList.setCurrentPos(progress);
                myBinder.continueplay();
                //图片切换
                imgbtn_play_play.setImageResource(R.mipmap.ic_pause_circle_outline_black_48dp);

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });
        //获取文本框和按钮
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_singer = (TextView) findViewById(R.id.tv_singer);
        imgbtn_play_play = (ImageButton) findViewById(R.id.imgbtn_play_play);
        imgbtn_previous_play = (ImageButton) findViewById(R.id.imgbtn_previous_play);
        imgbtn_next_play = (ImageButton) findViewById(R.id.imgbtn_next_play);


        //按钮事件
        imgbtn_play_play.setOnClickListener(this);
        imgbtn_previous_play.setOnClickListener(this);
        imgbtn_next_play.setOnClickListener(this);
        //启动服务
        Log.i("playactivity2", "init setvice");

        initServiceBinder();
        seekBar.setProgress(playList.getCurrentPos());
    }


    public void initServiceBinder() {
        //先检查服务是否已经先启动，然后再启动服务
        Log.i("initservice", MusicPlayService.class.getName());

        startservice(getApplicationContext());
        Log.i("initservice", "bindService");

        Intent bindIntent = new Intent(getApplicationContext(), MusicPlayService.class);
        //BIND_AUTO_CREATE会自动创建服务（如果服务并没有start）,这里设置0（不会自动start服务）
        bindService(bindIntent, connection, 0);

    }

    //UI修改
    public void changeUI() {
        if (playList.getListsize() > 0) {
            SongInfo song = playList.getCurrentSong();
            //歌曲名
            tv_name.setText(song.getSongName());
            //歌手名
            tv_singer.setText(song.getArtistName());
            if (playList.isPlaying())
                imgbtn_play_play.setImageResource(R.mipmap.ic_pause_circle_outline_black_48dp);
            else
                imgbtn_play_play.setImageResource(R.mipmap.ic_play_circle_outline_black_48dp);

            Uri uri = ContentUris.withAppendedId(OpUtil.ARTISTURI, song.getAlbumId());
                               MainActivity.imageLoader.displayImage(String.valueOf(uri),circleImage , MainActivity.options);

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //播放与暂停
            case R.id.imgbtn_play_play:
                if (playList.isPlaying()) {
                    myBinder.pause();
                changeUI();

                } else {
                    myBinder.continueplay();
                changeUI();

                }
                break;
            //下一首
            case R.id.imgbtn_next_play:
                myBinder.next();
                changeUI();
                break;
            //上一首
            case R.id.imgbtn_previous_play:
                myBinder.previous();
                changeUI();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (receiverNameSinger != null)
            unregisterReceiver(receiverNameSinger);
        if (receiverSeek != null)
            unregisterReceiver(receiverSeek);
        //注销
        if (connection != null)
            unbindService(connection);

        super.onDestroy();
    }

    //广播 用来设置seekBar进度条
    public class SeekBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("seek", "get");
            if (playList.isPlaying()) {
                int progress = intent.getIntExtra("progress", 0);
                seekBar.setMax((int) playList.getCurrentSong().getDuration());
                seekBar.setProgress(progress);
            }
        }
    }

    //广播 修改歌曲名和歌手名
    public class NameSingerBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            changeUI();
        }
    }

}
