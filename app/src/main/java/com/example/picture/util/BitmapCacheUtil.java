package com.example.picture.util;

import com.example.picture.entity.ImagesInfo;

import java.util.ArrayList;
import java.util.List;

/*Created by 邱强 on 2019/1/21.
 * E-Mail 2536555456@qq.com
 */
public class BitmapCacheUtil {
    //不要再adapter中操作  最好在activity中来统一操作
    public  static  final int  MAX_NUM=9;


    //选择照片时候已经选中的数据
    public  static List<ImagesInfo> tempSelectBitmap=new ArrayList<>();
    //上传成功的数据
    public  static ArrayList<ImagesInfo> upSelectBitmap=new ArrayList<>();

}
