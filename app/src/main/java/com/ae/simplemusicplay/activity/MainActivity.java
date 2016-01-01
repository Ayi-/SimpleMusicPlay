package com.ae.simplemusicplay.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.ae.simplemusicplay.PlayList;
import com.ae.simplemusicplay.R;
import com.ae.simplemusicplay.Util.HanZiToPinYinUtils;
import com.ae.simplemusicplay.Util.SharePreferenceUtils;
import com.ae.simplemusicplay.model.SongInfo;

import org.litepal.crud.DataSupport;

import static com.ae.simplemusicplay.Util.ToastUtil.showToast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //歌曲列表
    private PlayList playList;
    //设置参数
    private SharePreferenceUtils sharePreferenceUtils;
    //两次返回退出计时用
    long[] mHits = new long[2];

    private Button btn_play;
    private Button btn_updatelist;

    private Handler handler;

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

        //处理UI Toast
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                Bundle bundle = msg.getData();
                String message = bundle.getString("message");
                showToast(getApplicationContext(), message);
            }
        };
        sharePreferenceUtils = SharePreferenceUtils.getInstance(getApplicationContext());
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
        playList = PlayList.getInstance(this);

        //图片打开播放器
        ImageButton imageIcon = (ImageButton) findViewById(R.id.image_icon);
        imageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playList.getListsize() <= 0) {
                    //更新播放列表
                    //showToast(MainActivity.this,"并没有音乐");
                    handler.post(runnable);
                }
                if (playList.getListsize() > 0) {

                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, PlayMusic.class);
                    startActivity(intent);
                }
            }
        });



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
        }
        return true;
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
//
//        if (id == R.id.nav_camara) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

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

                if (duration > 240000) {
                    //先判断0是否存在
                    Log.i("Simple", songName + "  " + id + "  " + albumId + "  " + duration);
                    if (DataSupport.where("songId = ?", String.valueOf(id)).find(SongInfo.class).isEmpty()) {
                        SongInfo info = new SongInfo();
                        info.setPath(songPath);
                        info.setSongName(songName);
                        info.setAlbumName(album);
                        info.setAlbumId(albumId);
                        info.setArtistName(artist);

                        info.setPinyin(HanZiToPinYinUtils.HanZiToPinYin(songName));
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
                showToastForHandler(handler, "加载" + playList.getListsize()+"首歌");
                Log.i("Simple", "加载"+playList.getListsize()+"首歌");

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
}
