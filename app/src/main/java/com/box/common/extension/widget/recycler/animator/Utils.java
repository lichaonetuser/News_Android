package com.box.common.extension.widget.recycler.animator;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public final class Utils {

    public static Point getScreenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);

        Point point = new Point();
        point.set(dm.widthPixels, dm.heightPixels);
        return point;
    }

}