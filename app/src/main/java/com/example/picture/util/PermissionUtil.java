package com.example.picture.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/*Created by 邱强 on 2019/1/14.
 * E-Mail 2536555456@qq.com
 */
public class PermissionUtil {

   //判断读写权限是否开启
    public static boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) == PackageManager.PERMISSION_DENIED;
    }



}
