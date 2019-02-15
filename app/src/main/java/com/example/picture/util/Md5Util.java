package com.example.picture.util;

import android.content.Context;
import android.os.Looper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by QiuQ on 2017-07-27.
 */

public class Md5Util {
    private Md5Util() {
    }

    //生成唯一的MD5 摘要id
    public static String getKey(String id) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            if (id != null) {
                m.update(id.getBytes("UTF8"));
                byte s[] = m.digest();
                return convertToHexString(s);
            } else {
                return "";
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return id != null ? id : "";
    }


    public static String convertToHexString(byte data[]) {
        StringBuilder strBuffer = new StringBuilder();
        for (byte aData : data) {
            strBuffer.append(Integer.toHexString(0xff & aData));
        }
        return strBuffer.toString();
    }

    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


}
