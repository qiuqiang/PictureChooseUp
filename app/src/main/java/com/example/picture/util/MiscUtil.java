package com.example.picture.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.Random;

/**
 * Created by littlejie on 2017/2/22.
 */

public class MiscUtil {

    public static int measure(int measureSpec, int defaultSize) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }


    // 获取数值精度格式化字符串
    public static String getPrecisionFormat(int precision) {
        return "%." + precision + "f";
    }

    //获取一个随机颜色
    public int getColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    public static String getModeName(int mode) {
        if (mode == View.MeasureSpec.AT_MOST) {
            return "View.MeasureSpec.AT_MOST";
        } else if (mode == View.MeasureSpec.EXACTLY) {
            return "View.MeasureSpec.EXACTLY";
        } else {
            return "View.MeasureSpec.UNSPECIFIED";
        }
    }

    public static int getWindowWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = null;
        if (manager != null) {
            defaultDisplay = manager.getDefaultDisplay();
            Point p = new Point();
            defaultDisplay.getSize(p);
            return p.x;
        }
        return 0;
    }

    public static int getWindowHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = null;
        if (manager != null) {
            defaultDisplay = manager.getDefaultDisplay();
            Point p = new Point();
            defaultDisplay.getSize(p);
            return p.y;
        }
        return 0;
    }

    //dp转px
    private static float dpToPx(Context context, float dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    //dp转px
    public static float pxToDp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;
        return px / density;
    }
}