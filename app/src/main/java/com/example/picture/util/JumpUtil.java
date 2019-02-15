package com.example.picture.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Created by QiuQ on 2017-06-21.
 */

public class JumpUtil {
    public static void jumpToActivity(Context context, Class<? extends Activity> target) {
        Intent t = new Intent(context, target);
        context.startActivity(t);
    }

    public static void jumpToActivity(Context context, Class<? extends Activity> target, Map<String, String> args) {
        Intent t = new Intent(context, target);
        Set<Map.Entry<String, String>> entries = args.entrySet();
        for (Map.Entry<String, String> next : entries) {
            t.putExtra(next.getKey(), next.getValue());
        }
        context.startActivity(t);
    }

    public static void jumpToActivity(Context context, Class<? extends Activity> target, String key, String value) {
        Intent t = new Intent(context, target);
        t.putExtra(key, value);
        context.startActivity(t);
    }
}
