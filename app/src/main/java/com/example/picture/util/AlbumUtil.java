package com.example.picture.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


import com.example.picture.entity.AlbumInfo;
import com.example.picture.entity.ImagesInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/*Created by 邱强 on 2019/1/25.
 * E-Mail 2536555456@qq.com
 */
public class AlbumUtil {


    private AlbumUtil() {
    }

    public static AlbumUtil getInstance() {
        return UtilsClass.INSTANCE;
    }

    private static class UtilsClass {
        private static final AlbumUtil INSTANCE = new AlbumUtil();
    }


    public List<AlbumInfo> getAllAlbumInfo(Context context) {
        List<AlbumInfo> list = new ArrayList<>();
        List<AlbumInfo> externalLAlbumInfo = getExternalLAlbumInfo(context,true);
        List<AlbumInfo> internalLAlbumInfo = getInternalLAlbumInfo(context,false);
        list.addAll(externalLAlbumInfo);
        list.addAll(internalLAlbumInfo);
        return list;
    }

    /**
     * 从images表中提取出相册名字信息
     *
     * @param context 上下文
     * @return 返回相册集合
     */
    public List<AlbumInfo> getExternalLAlbumInfo(Context context,boolean isExternal) {
        ContentResolver contentResolver = context.getContentResolver();
        List<AlbumInfo> list = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        //BUCKET_DISPLAY_NAME 所属相册名字 直接包含图片的文件夹就是该图片的 bucket，就是文件夹名
        Cursor cur = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        //contentResolver 也可以查询去重
        TreeSet<String> BUCKET_IDList = new TreeSet<>();
        try {
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        int BUCKET_ID_index = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                        String BUCKET_ID = cur.getString(BUCKET_ID_index);
                        BUCKET_IDList.add(BUCKET_ID);//对结果去重
                    } while (cur.moveToNext());
                }
                cur.close();
                for (String v : BUCKET_IDList) {
                    String name = getBucketNameById(context, v, true);
                    int count = getCountOfAlbumById(context, v,isExternal);
                    String firstImagePath = getFirstImagePath(context, v,isExternal);
                    AlbumInfo info = new AlbumInfo();
                    info.setBucket_id(v);
                    info.setBucket_display_name(name);
                    info.setCount(count + "");
                    info.setFirstImagePath(firstImagePath);
                    info.setExternal(true);
                    list.add(info);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<ImagesInfo> getAllImagesOfAlbum(Context context, String bucket_id, boolean isExternal) {
        ContentResolver contentResolver = context.getContentResolver();
        List<ImagesInfo> list = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.MIME_TYPE,
                 MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        //BUCKET_DISPLAY_NAME 所属相册名字 直接包含图片的文件夹就是该图片的 bucket，就是文件夹名
        Cursor cur = contentResolver.query(getUri(isExternal), projection, MediaStore.Images.Media.BUCKET_ID + "=" + bucket_id, null, MediaStore.Images.Media.DATE_MODIFIED + " asc");
        try {
            // TODO: 2019/1/25  待优化
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        ImagesInfo info = new ImagesInfo();
                        info.set_id(cur.getString(cur.getColumnIndex(MediaStore.Images.Media._ID)));
                        info.setPath(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA)));
                        info.setSize(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.SIZE)));
                        info.setName(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                        info.setMime_type(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)));
                        info.setBucket_id(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)));
                        info.setBucket_display_name(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
                        list.add(info);
                    } while (cur.moveToNext());
                }
                cur.close();
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllImagesPathOfAlbum(Context context, String bucket_id, boolean isExternal) {
        ContentResolver contentResolver = context.getContentResolver();
        List<String> data = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Cursor cur = contentResolver.query(getUri(isExternal), projection, MediaStore.Images.Media.BUCKET_ID + "=" + bucket_id, null, null);
        try {
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        data.add(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
                    } while (cur.moveToNext());
                }
                cur.close();
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 从images表中提取出相册名字信息
     *
     * @param context 上下文
     * @return 返回相册集合
     */
    public List<AlbumInfo> getInternalLAlbumInfo(Context context,boolean isExternal) {
        ContentResolver contentResolver = context.getContentResolver();
        List<AlbumInfo> list = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        //BUCKET_DISPLAY_NAME 所属相册名字 直接包含图片的文件夹就是该图片的 bucket，就是文件夹名
        Cursor cur = contentResolver.query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, projection, null, null, null);
        //contentResolver 也可以查询去重
        TreeSet<String> idList = new TreeSet<>();
        try {
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        int BUCKET_ID_index = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                        String BUCKET_ID = cur.getString(BUCKET_ID_index);
                        idList.add(BUCKET_ID);//对结果去重
                    } while (cur.moveToNext());
                }
                cur.close();
                for (String v : idList) {
                    String name = getBucketNameById(context, v, true);
                    int count = getCountOfAlbumById(context, v,isExternal);
                    String firstImagePath = getFirstImagePath(context, v,isExternal);
                    AlbumInfo info = new AlbumInfo();
                    info.setExternal(false);
                    info.setBucket_id(v);
                    info.setBucket_display_name(name);
                    info.setCount(count + "");
                    info.setFirstImagePath(firstImagePath);
                    list.add(info);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据id 获取名字
     *
     * @param context    上下文
     * @param bucket_id  相册名字
     * @param isExternal 表示 EXTERNAL_CONTENT_URI
     */
    public String getBucketNameById(Context context, String bucket_id, boolean isExternal) {
        ContentResolver contentResolver = context.getContentResolver();
        List<String> list = new ArrayList<>();
        //第一个放相册的 首图  第二个放相册 图片张数
        String[] projection = {MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        //BUCKET_DISPLAY_NAME 所属相册名字 直接包含图片的文件夹就是该图片的 bucket，就是文件夹名
        try {
            Cursor cur = contentResolver.query(getUri(true), projection, MediaStore.Images.Media.BUCKET_ID + "=" + bucket_id, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String name = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    cur.close();
                    return name;
                } else {
                    cur.close();
                    return "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 根据id 获取缩略图path
     *
     * @param context    上下文
     * @param image_id   图片id
     * @param isExternal true表示 EXTERNAL_CONTENT_URI
     */
    public String getThumbnailsPathById(Context context, String image_id, boolean isExternal) {
        ContentResolver contentResolver = context.getContentResolver();
        List<String> list = new ArrayList<>();
        //第一个放相册的 首图  第二个放相册 图片张数
        String[] projection = {MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA};
        //BUCKET_DISPLAY_NAME 所属相册名字 直接包含图片的文件夹就是该图片的 bucket，就是文件夹名
        try {
            Cursor cur = contentResolver.query(getThumbUri(true), projection, MediaStore.Images.Thumbnails.IMAGE_ID + "=" + image_id, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String path = cur.getString(cur.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                    cur.close();
                    return path;
                } else {
                    cur.close();
                    return "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取相册的第一张图片路径
     *
     * @param context   上下文
     * @param bucket_id 相册id
     */
    public String getFirstImagePath(Context context, String bucket_id,boolean isExternal) {
        ContentResolver contentResolver = context.getContentResolver();
        List<String> list = new ArrayList<>();
        //第一个放相册的 首图  第二个放相册 图片张数
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.DATE_MODIFIED};
        //BUCKET_DISPLAY_NAME 所属相册名字 直接包含图片的文件夹就是该图片的 bucket，就是文件夹名
        try {
            //asc desc
            Cursor cur = contentResolver.query(getUri(isExternal), projection, MediaStore.Images.Media.BUCKET_ID + "=" + bucket_id, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
            if (cur != null) {
                if (cur.moveToNext()) {
                    String DATE_MODIFIED = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));
                    cur.close();
                    return DATE_MODIFIED;
                } else {
                    cur.close();
                    return "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取相册的第一张图片路径
     *
     * @param context   上下文
     * @param bucket_id 相册id
     */
    public String getImage_idByBucket_id(Context context, String bucket_id, boolean isExternal) {
        ContentResolver contentResolver = context.getContentResolver();
        List<String> list = new ArrayList<>();
        //第一个放相册的 首图  第二个放相册 图片张数
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID};
        //BUCKET_DISPLAY_NAME 所属相册名字 直接包含图片的文件夹就是该图片的 bucket，就是文件夹名
        try {
            //asc desc
            Cursor cur = contentResolver.query(getUri(isExternal), projection, MediaStore.Images.Media.BUCKET_ID + "=" + bucket_id, null, null);
            if (cur != null) {
                if (cur.moveToNext()) {
                    String _ID = cur.getString(cur.getColumnIndex(MediaStore.Images.Media._ID));
                    cur.close();
                    return _ID;
                } else {
                    cur.close();
                    return "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 查询一个相册类包含的图片数量
     *
     * @param context   上下文
     * @param bucket_id 相册id
     */
    public int getCountOfAlbumById(Context context, String bucket_id,boolean isExternal) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.Images.Media.BUCKET_ID};
        //BUCKET_DISPLAY_NAME 所属相册名字 直接包含图片的文件夹就是该图片的 bucket，就是文件夹名
        try {
            Cursor cur = contentResolver.query(getUri(isExternal), projection, MediaStore.Images.Media.BUCKET_ID + "=" + bucket_id, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    int count = cur.getCount();
                    cur.close();
                    return count;
                } else {
                    cur.close();
                    return 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询一个相册类包含的图片地址
     *
     * @param context 上下文
     * @param name
     */
    public List<String> getImagesOfAlbum(Context context, String name) {
        ContentResolver contentResolver = context.getContentResolver();
        List<String> list = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        //BUCKET_DISPLAY_NAME 所属相册名字 直接包含图片的文件夹就是该图片的 bucket，就是文件夹名
        try {
            Cursor cur = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=" + name, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        list.add(cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA)));
                    } while (cur.moveToNext());
                }
                cur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private Uri getUri(boolean isExternal) {
        Uri uri = null;
        if (isExternal) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else {
            uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        }
        return uri;
    }

    private Uri getThumbUri(boolean isExternal) {
        Uri uri = null;
        if (isExternal) {
            uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        } else {
            uri = MediaStore.Images.Thumbnails.INTERNAL_CONTENT_URI;
        }
        return uri;
    }

}
