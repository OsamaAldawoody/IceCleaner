package com.phonecleaner.icecleaner.utils;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtil {

    /**
     * This method is used to get width of screen
     *
     * @param context is current context
     * @return return width of screen in pixel
     */
    public static int getWidthScreen(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dimenson = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dimenson);
        return dimenson.widthPixels;
    }
    /**
     * This method is used to get height of screen
     *
     * @param context is current context
     * @return return height screen in pixel
     */
    public static int getHeightScreen(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dimenson = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dimenson);
        return dimenson.heightPixels;
    }
}
