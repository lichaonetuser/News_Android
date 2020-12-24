package com.box.app.news.util;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.box.common.core.CoreApp;

public class ToastUtils {


    private static final Object SYNC_LOCK = new Object();

    private static Toast mToast;

    private static long sLastShowTime;
    private static int sLastDuration;
    private static String sLastMsg = "";
    public static final int LONG_DURATION = 3500;
    public static final int SHORT_DURATION = 2000;

    @SuppressLint("ShowToast")
    private static void checkToastInstance() {
        if (mToast == null) {
            synchronized (SYNC_LOCK) {
                if (mToast == null) {
                    mToast = Toast.makeText(CoreApp.getInstance(), "", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    /**
     * 隐藏Toast
     */
    public static void cancel() {
        if (mToast == null) {
            return;
        }
        mToast.cancel();
    }

    /**
     * 展示吐司
     *
     * @param text 内容
     */
    public static void showToast(@NonNull String text) {
        showToast(text, false);
    }

    /**
     * @param text         内容
     * @param isLongLength 是否长时间展示
     */
    public static void showToast(String text, boolean isLongLength) {
        if (!TextUtils.isEmpty(text)) {
            checkToastInstance();
            if (text.equals(sLastMsg) && sLastDuration != 0) {
                long offset = System.currentTimeMillis() - sLastShowTime;
                if (offset < sLastDuration) { //如果前一个同内容的toast还在显示，就不显示下一个toast
                    return;
                }
            }

            sLastMsg = text;
            sLastDuration = isLongLength ? LONG_DURATION : SHORT_DURATION;
            sLastShowTime = System.currentTimeMillis();
            mToast.setDuration(isLongLength ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            mToast.setText(text);
            mToast.show();
        }
    }


    /**
     * 展示新吐司
     *
     * @param text 内容
     */
    public static void showNewToast(String text) {
        showNewToast(text, false);
    }

    /**
     * 展示新吐司
     *
     * @param text         内容
     * @param isLongLength 是否长时间展示
     */
    public static void showNewToast(String text, boolean isLongLength) {
        if (!TextUtils.isEmpty(text)) {
            Toast.makeText(CoreApp.getInstance(), text, isLongLength ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }
}
