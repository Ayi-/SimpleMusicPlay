package com.ae.simplemusicplay.Util;


import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.ae.simplemusicplay.model.SongInfo;
import com.ae.simplemusicplay.widgets.GeCiMiLyric;
import com.google.gson.Gson;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import cz.msebera.android.httpclient.Header;


public class LyricUtils {

    public static final String URL = "http://geci.me/api/lyric/";

    static boolean again = false;
    static SongInfo currentSong;
    static Context thisContext;

    static String dirPath = "";

    static SyncHttpClient client = new SyncHttpClient();

    /**
     * 这个类全部都是在子线程里面执行的!!!
     *
     * @param info
     * @param
     * @return
     */
    public static String getLrcPath(SongInfo info, Context context) {
        currentSong = info;
        thisContext = context;
        dirPath = getDirPath(context);

        String path = loadFromDisk(info);
        Log.i("TAG", path);
        return path;
    }


    private static String loadFromDisk(SongInfo info) {
        Log.i("TAG", "loadFromDisk");

        String lrcPath = dirPath + File.separator +
                info.getSongName() + "-" + info.getArtistName() + ".lrc";

        File file = new File(lrcPath);

        Log.i("TAG", lrcPath);

        if (!file.exists()) {
            loadFromNet(info.getSongName(), "/" + info.getArtistName());
        }


        if (!file.exists()) {
            Log.i("TAG", "==========================================");
            return "";
        } else {
            Log.i("TAG", "oooooooooooooooooooo===========");
            return file.getAbsolutePath();
        }
    }

    private static void loadFromNet(final String songName, String artistName) {
        String url = URL + songName + artistName;
        Log.i("TAG", url);
        client.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                parseJson(responseString);
            }
        });
    }

    private static void parseJson(String json) {
        Log.i("TAG", json);
        Gson gson = new Gson();
        GeCiMiLyric lyric = gson.fromJson(json, GeCiMiLyric.class);
        if (lyric.getCount() == 0 && !again) {
            again = true;
            loadFromNet(currentSong.getSongName(), "");
        } else {
            again = false;
            if (lyric.getCount() == 0) {
                //说明不管怎样都找不到歌词....
            } else {
                String url = lyric.getResult().get(0).getLrc();
                Log.i("TAG", "parseJson :" + url);
                downloadLrc(url);
            }
        }
    }

    private static void downloadLrc(String url) {
        client.get(url, new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                try {
                    FileOutputStream fos = new FileOutputStream(
                            dirPath + File.separator + currentSong.getSongName() + "-" + currentSong.getArtistName() + ".lrc");

                    fos.write(binaryData);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {

            }
        });
    }

    private static String getDirPath(Context context) {
        String path;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = context.getExternalCacheDir().getPath();
        } else {
            path = context.getCacheDir().getPath();
        }
        File file = new File(path + File.separator + "lyc");
        if (!file.exists()) {
            file.mkdir();
        }
        //返回/storage/emulated/0/Android/data/com.chenjiayao.musicplayer/cache/lyc
        return file.getAbsolutePath();
    }
}
