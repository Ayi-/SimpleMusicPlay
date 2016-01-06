package com.ae.simplemusicplay.Util;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ae.simplemusicplay.services.MusicPlayService;

import java.util.List;

/**
 * Created by AE on 2016/1/3.
 */
public class StartService {
    public static Context mContext;
    public static void startservice(Context context){
mContext = context;
          if (!isWorked(MusicPlayService.class.getName())) {

            Log.i("initservice", "start service");
            Intent startIntent = new Intent(mContext.getApplicationContext(), MusicPlayService.class);
            //设置服务不自动重新启动
            startIntent.setFlags(Service.START_NOT_STICKY);
            mContext.startService(startIntent);
        }
    }

    //查询所有服务，检查服务是否已经启动
    static boolean isWorked(String className) {
        ActivityManager manager = (ActivityManager) mContext.getApplicationContext()
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
}
