package com.ae.simplemusicplay.activity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.ae.simplemusicplay.Myadapter.SongListAdapter;
import com.ae.simplemusicplay.PlayList;
import com.ae.simplemusicplay.R;
import com.ae.simplemusicplay.Util.HanZiToPinYinUtils;
import com.ae.simplemusicplay.Util.OpUtil;
import com.ae.simplemusicplay.Util.SharePreferenceUtils;
import com.ae.simplemusicplay.model.SongInfo;

import org.litepal.crud.DataSupport;

import static com.ae.simplemusicplay.Util.StartService.startservice;
import static com.ae.simplemusicplay.Util.ToastUtil.showToast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SongListAdapter.ClickListener {

    //歌曲列表
    private PlayList playList;
    //设置参数用的工具类
    private SharePreferenceUtils sharePreferenceUtils;
    //两次返回退出计时用
    long[] mHits = new long[2];

    //按两次同一首歌就切换到播放界面,通过前后点击位置来判断
    private int firstPositionClick = -1;

    private Handler handler;
    //按钮
    ImageButton imageIcon;
    ImageButton imgbtn_previous_List;
    ImageButton imgbtn_play_List;
    ImageButton imgbtn_next_List;
    //列表
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SongListAdapter songListAdapter;
    //定义一个广播，用来修改UI界面
    private NameSingerBroadCast receiverNameSinger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //右侧滑动菜单
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //初始化sharePreference
        sharePreferenceUtils = SharePreferenceUtils.getInstance(getApplicationContext());
        //初始化list实例
        playList = PlayList.getInstance(this);

        //添加按钮
        imageIcon = (ImageButton) findViewById(R.id.image_icon);
        imgbtn_previous_List = (ImageButton) findViewById(R.id.imgbtn_previous_List);
        imgbtn_play_List = (ImageButton) findViewById(R.id.imgbtn_play_List);
        imgbtn_next_List = (ImageButton) findViewById(R.id.imgbtn_next_List);

        //初始化recycleview
        mRecyclerView = (RecyclerView) findViewById(R.id.music_list);
        //这里用线性显示 类似于listview
        linearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        //如果每个item的大小都是固定的,加这个能加速运行
        mRecyclerView.setHasFixedSize(true);

        //处理UI Toast
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                Bundle bundle = msg.getData();
                String message = bundle.getString("message");
                showToast(getApplicationContext(), message);
                //设置最后播放的歌曲和进度
                playList.setCurrentPos(sharePreferenceUtils.getCurrentPos());
                playList.setCurrent(sharePreferenceUtils.getCurrentSongId());
                //修改UI
                changeUI();
                //启动服务
                startservice(getApplicationContext());
            }
        };

        //是否第一次使用本APP（进行歌曲扫描）
        Log.i("Simple", "scan");
        if (sharePreferenceUtils.isFirstTimeUse()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    searchSongs();
                    sharePreferenceUtils.setNotFirst();
                }
            }).start();
        }

        Log.i("Simple", "load");

        //更新播放列表
//        if (playList.getListsize() <= 0)
        handler.post(runnable);

        //按钮事件注册
        imageIcon.setOnClickListener(this);
        imgbtn_previous_List.setOnClickListener(this);
        imgbtn_play_List.setOnClickListener(this);
        imgbtn_next_List.setOnClickListener(this);

        //歌曲填充
        songListAdapter = new SongListAdapter(this, playList.songInfos);
        songListAdapter.setOnItemClickListener(this);

        //歌曲列表填充
        mRecyclerView.setAdapter(songListAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //注册UI修改广播
        receiverNameSinger = new NameSingerBroadCast();
        IntentFilter filterNameSinger = new IntentFilter();
        filterNameSinger.addAction(OpUtil.BROADCAST_PLAY_NAME_SINGER);
        registerReceiver(receiverNameSinger, filterNameSinger);


    }

    //按返回键关掉菜单
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //两次返回退出APP
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();

            if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {

                finish();
            } else {
                showToast(this, "再次点击关闭音乐退出");

            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    //OptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //OptionsMenu事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //侧滑菜单点击
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_native_music) {

        } else if (id == R.id.nav_sleeptime) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_exit) {
            //退出应用
            Log.i("ae", id + "   " + R.id.nav_exit);
            Intent intentExit = new Intent();
            intentExit.setAction(OpUtil.BROADCAST_EXIT);
            sendBroadcast(intentExit);
            Log.i("ae", "send exit!");
            finish();
        } else if (id == R.id.nav_search_music) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        /*
                         *   (non-Javadoc)
                         * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                         */
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
                    searchSongs();
                    handler.post(runnable);
                }
            }).start();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //获取歌曲信息

    public void searchSongs() {
        showToastForHandler(handler, "正在扫描歌曲");
        sharePreferenceUtils.setScanFlag(true);
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {

                //专辑
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));

                //歌曲名称
                String songName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));

                //歌曲路径
                String songPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                //演唱者
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                //播放时长
                long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲id
                long id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //专辑id
                long albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                if (duration > 120000) {
                    //先判断0是否存在
                    Log.i("Simple", songName + "  " + id + "  " + albumId + "  " + duration);
                    if (DataSupport.where("songId = ?", String.valueOf(id)).find(SongInfo.class).isEmpty()) {
                        SongInfo info = new SongInfo();
                        info.setPath(songPath);
                        info.setSongName(songName);
                        info.setAlbumName(album);
                        info.setAlbumId(albumId);
                        info.setArtistName(artist);
                        try {

                            info.setPinyin(HanZiToPinYinUtils.HanZiToPinYin(songName));
                        } catch (Exception e) {
                            info.setPinyin("#");
                            e.printStackTrace();
                        }
                        info.setSongId(id);
                        info.setDuration(duration);
                        info.save();
                    }
                }

            }
        }
        cursor.close();
        //设置扫描结束
        sharePreferenceUtils.setScanFlag(false);
        //发送吐司
        showToastForHandler(handler, "扫描结束");

        //更新播放列表
        handler.post(runnable);
    }

    //更新播放列表playlist
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (!sharePreferenceUtils.getScanFlag()) {
                showToastForHandler(handler, "正在加载歌曲列表");
                playList.addToList(DataSupport.findAll(SongInfo.class), 0);
                showToastForHandler(handler, "加载" + playList.getListsize() + "首歌");
                Log.i("Simple", "加载" + playList.getListsize() + "首歌");
                songListAdapter.notifyDataSetChanged();
            }
        }
    };


    //通过handler发送Toast
    public void showToastForHandler(Handler handler, String msg) {
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("message", msg);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    protected void onResume() {
        //切换播放按钮图标
        changeUI();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Intent intentOp = getOPIntent();
        Log.i("btn_current_song", playList.getCurrent() + "");
        Log.i("btn_playlist_size", playList.getListsize() + "");
        //通过广播形式发送操作
        switch (v.getId()) {
            //播放与暂停
            case R.id.imgbtn_play_List:
                //发送广播

                if (playList.isPlaying()) {
                    intentOp.putExtra("op", OpUtil.OP_PAUSE);
                } else {
                    intentOp.putExtra("op", OpUtil.OP_CONTINUE);
                }

                sendBroadcast(intentOp);
                break;
            //上一首
            case R.id.imgbtn_previous_List:
                intentOp.putExtra("op", OpUtil.OP_PREVIOUS);
                sendBroadcast(intentOp);

                break;
            //下一首
            case R.id.imgbtn_next_List:
                intentOp.putExtra("op", OpUtil.OP_NEXT);
                sendBroadcast(intentOp);
                break;
            case R.id.image_icon:
                if (playList.getListsize() > 0) {
                    //切换到播放界面
                    startPlayMusicActivity();
                }
                break;
        }
    }

    //设置recycleview item的点击事件
    @Override
    public void onItemClick(int position, View v) {
        firstPositionClick = playList.getCurrent();
        playList.setCurrent(position);
        //判断是否同一首歌按了两次
        if (firstPositionClick != position) {
            //播放点击的音乐
            Intent intentOp = getOPIntent();
            intentOp.putExtra("op", OpUtil.OP_PLAY);
            changeUI();
            sendBroadcast(intentOp);
            Log.i("item", "onItemClick position: " + position);

        } else {
            //第二次进入播放界面
            startPlayMusicActivity();
        }
    }

    //获取操作音乐播放的intent,用来发送广播
    public Intent getOPIntent() {
        Intent intentOp = new Intent();
        intentOp.setAction(OpUtil.BROADCAST_BTN);
        return intentOp;
    }

    //切换到播放界面
    public void startPlayMusicActivity() {
        Intent intent = new Intent(MainActivity.this, PlayMusic.class);

        startActivity(intent);
    }


    //UI修改
    public void changeUI() {
        if (playList.getListsize() > 0) {
            SongInfo song = playList.getCurrentSong();
            if (playList.isPlaying())
                imgbtn_play_List.setImageResource(R.mipmap.ic_pause_circle_outline_black_48dp);
            else
                imgbtn_play_List.setImageResource(R.mipmap.ic_play_circle_outline_black_48dp);
        }
    }

    //广播 修改歌曲名和歌手名
    public class NameSingerBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            changeUI();
        }
    }

    @Override
    protected void onDestroy() {
        //注销广播
        if (receiverNameSinger != null) {
            unregisterReceiver(receiverNameSinger);
        }
        if (playList != null)
            playList = null;

        super.onDestroy();
    }
}
