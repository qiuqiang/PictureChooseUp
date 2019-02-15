package com.example.picture.util;

import android.app.Activity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QiuQ on 2016-12-13
 */
public final class ActivityUtils {
    private static List<Activity> list = new ArrayList<>();

    private ActivityUtils() {
    }

    public static ActivityUtils newInstance() {
        return UtilsClass.INSTANCE;
    }

    private static class UtilsClass {
        private static final ActivityUtils INSTANCE = new ActivityUtils();
    }

    public void addActivity(Activity activity) {
        if (activity != null) {
            list.add(activity);
        }
    }

    public static void exitApp() {
        if (list != null) {
            for (Activity aa : list) {
                aa.finish();
            }
        }
        System.exit(0);
    }


}
