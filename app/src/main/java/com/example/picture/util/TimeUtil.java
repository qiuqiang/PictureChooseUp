package com.example.picture.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by QiuQ on 2017-03-01.
 */
//日期时间工具类
public class TimeUtil {

    public static String getCurrentYyyyMmDd() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sif = new SimpleDateFormat("yyyyMMdd");
        String date = sif.format(new Date(time));
        return date;
    }

    public static String getCurrentYyyy_Mm_Dd() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM-dd");
        String date = sif.format(new Date(time));
        return date;
    }

    public static String getCurrentYyyyMm() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sif = new SimpleDateFormat("yyyyMM");
        String date = sif.format(new Date(time));
        return date;
    }

    public static String getCurrentYyyy_Mm() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sif = new SimpleDateFormat("yyyy-MM");
        String date = sif.format(new Date(time));
        return date;
    }

    //求多少年前的日期
    public static String countDateByYear(String format, String str, int year) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date_ = null;
        try {
            date_ = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cdDate = Calendar.getInstance();
        cdDate.setTime(date_);
        cdDate.add(cdDate.YEAR, year);
        String resultDate = sdf.format(cdDate.getTime());
        return resultDate;
    }

    //求多少年前的日期
    public static String format( String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(str);
        return format;
    }


    public static String getCurrentTime() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sif = new SimpleDateFormat("HHmmss");
        String date = sif.format(new Date(time));
        return date;
    }

    //判断开户日期和当前日期相比是否超过三年
    public  static  boolean isUpThreeYear(String date){
        if(date.length()!=8){
            return  false;
        }else {
            String now=getCurrentYyyyMmDd();//当前日期
            String beforeNowThree =countDateByYear("yyyyMMdd", now, -3);//当前日期的三年前日期
            if(Integer.valueOf(date).intValue()-Integer.valueOf(beforeNowThree).intValue()<0){
                return  true;
            }else if(Integer.valueOf(date).intValue()-Integer.valueOf(beforeNowThree).intValue()==0){
                return  false;
            }else {
                return  false;
            }
        }
    }
}
