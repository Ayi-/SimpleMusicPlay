package com.ae.simplemusicplay.Util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by AE on 2015/12/30.
 */
public class ToastUtil {

    private static Toast mToast;

    public static void showToast(Context context,String text) {
        if(mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
