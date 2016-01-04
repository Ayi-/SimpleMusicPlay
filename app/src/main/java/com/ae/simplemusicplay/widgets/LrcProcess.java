package com.ae.simplemusicplay.widgets;

import android.util.Log;

import com.ae.simplemusicplay.model.LrcContent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by chen on 2015/12/26.
 */
public class LrcProcess {

    private static final String TAG = "LrcProcess";
    private List<LrcContent> contents;


    public LrcProcess() {
        contents = new ArrayList<>();
    }

    public void readLrc(String path) {
        StringBuilder sb = new StringBuilder();

        if (!contents.isEmpty()) {
            contents.clear();
        }

        Log.i(TAG, path);
        File file = new File(path);

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis, "utf-8");
            BufferedReader br = new BufferedReader(reader);

            String s = "";
            while ((s = br.readLine()) != null) {
                s = s.replace("[", "");
                s = s.replace("]", "#");    //作为分隔符
                // [01:43.00][00:19.00]今天我寒夜里看雪飘过
                // 01:43.00#00:19.00#今天我寒夜里看雪飘过

                s = s.replace(" ", "");
                String splitData[] = s.split("#");
                Log.i("TAG", s);
                String lrc = splitData[splitData.length - 1];

                for (int i = (splitData.length - 2); i >= 0; i--) {

                    LrcContent content = new LrcContent();
                    content.setLrcTime(time2Str(splitData[i]));
                    content.setLrcStr(lrc);
                    contents.add(content);
                }
            }
        } catch (Exception e) {
            sb.append("没有歌词文件");
            e.printStackTrace();
        }
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 00:19.00
     * 02:28.00
     *
     * @param s 歌词字符串
     * @return 当前播放时间
     */
    private int time2Str(String s) {
        s = s.replace(":", ".");
        s = s.replace(".", "#");  //00:19.00--> 00#19#00  分,秒,毫秒

        String[] split = s.split("#");

        int minute = Integer.parseInt(split[0]);
        int second = Integer.parseInt(split[1]);
        int millisecond = Integer.parseInt(split[2]);

        int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
        return currentTime;
    }


    public List<LrcContent> getContents() {
        return contents;
    }
}
