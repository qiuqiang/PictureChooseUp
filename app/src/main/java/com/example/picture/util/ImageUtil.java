package com.example.picture.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import com.example.picture.entity.AlbumInfo;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/*Created by 邱强 on 2019/1/18.
 * E-Mail 2536555456@qq.com
 */

public class ImageUtil {
    private ImageUtil() {
    }

    public static ImageUtil getInstance() {
        return UtilsClass.INSTANCE;
    }

    private static class UtilsClass {
        private static final ImageUtil INSTANCE = new ImageUtil();
    }

    /**
     * 质量压缩  压缩一次 宽高不变 质量压缩并不会改变图片在内存中的大小 不适合作为缩略图  适用于保存图片
     *
     * @param image   图片
     * @param quality 压缩质量 0-100 100表示不压缩
     */
    public Bitmap compressImageByQuality(Bitmap image, int quality) {
        if (quality < 0 || quality > 100) {
            quality = 100;
        }
        if (image != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 质量压缩方法
            byte[] bytes = baos.toByteArray();
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }

    /**
     * 质量压缩  压缩一次
     *
     * @param imgPath 图片路径
     * @param quality 压缩质量 0-100 100表示不压缩
     */
    public Bitmap compressImageByQuality(String imgPath, int quality) {
        if (quality < 0 || quality > 100) {
            quality = 100;
        }
        if (imgPath != null) {
            File file = new File(imgPath);
            if (file.exists() && file.isFile()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 质量压缩方法
                bitmap.recycle();
                ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
                return BitmapFactory.decodeStream(isBm);
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 质量压缩  压缩到指定kb以下
     *
     * @param imgPath 图片路径
     * @param kb      压缩到 kb以下
     */
    public Bitmap compressImageByQualityToLength(String imgPath, int kb) {
        if (kb < 0) {
            kb = 200;
        }
        if (imgPath != null) {
            File file = new File(imgPath);
            if (file.exists() && file.isFile()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法
                int options = 90;
                while (baos.toByteArray().length / 1024 > kb) {
                    baos.reset();
                    if (options > 0 && options < 100) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    }
                    options -= 10;// 每次都减少10
                }
                byte[] bytes = baos.toByteArray();
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * 质量压缩  压缩到指定kb以下
     *
     * @param image 图片路径
     * @param kb    压缩到 kb以下
     */
    public Bitmap compressImageByQualityToLength(Bitmap image, int kb) {
        if (image != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法
            int options = 90;
            while (baos.toByteArray().length / 1024 > kb) {
                baos.reset();
                if (options > 0 && options < 100) {
                    image.compress(Bitmap.CompressFormat.JPEG, options, baos);
                }
                options -= 10;// 每次都减少10
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            return BitmapFactory.decodeStream(isBm, null, null);
        } else {
            return null;
        }
    }


    /**
     * 按比例/采样率 压缩
     *
     * @param imgPath    图片路径
     * @param sampleSize
     */
    public Bitmap compressByScale(String imgPath, int sampleSize) {
        if (checkPath(imgPath)) {
            if (sampleSize < 0 || sampleSize > 1) {
                sampleSize = 4;
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            //开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);//此时返回bm为空
            newOpts.inJustDecodeBounds = false;
            //该值只能为2的幂 当不为2的幂时，解码器会取与该值最接近的2的幂
            //设置的inSampleSize会导致压缩的图片的宽高都为1/inSampleSize，整体大小变为原始图片的inSampleSize平方分之一
            newOpts.inSampleSize = sampleSize;
            return BitmapFactory.decodeFile(imgPath, newOpts);
        }
        return null;
    }

    private boolean checkPath(String imgPath) {
        if (imgPath != null) {
            File file = new File(imgPath);
            return file.exists() && file.isFile();
        }
        return false;
    }

    /**
     * 通过减少图片的像素来降低图片的磁盘空间大小和内存大小，可以用于缓存缩略图
     * 获取缩略图 适用于recycleView等的适配器图片列表
     *
     * @param imgPath 图片路径
     */
    public Bitmap getBitmapThumbnail(String imgPath) {
        if (checkPath(imgPath)) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            if(bitmap!=null){
                //设置缩放比
                int radio = 8;
                float width = bitmap.getWidth() / radio;
                float height = bitmap.getHeight() / radio;
                Bitmap result = Bitmap.createBitmap(bitmap.getWidth() / radio, bitmap.getHeight() / radio, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(result);
                RectF rectF = new RectF(0, 0, width, height);
                //将原图画在缩放之后的矩形上
                canvas.drawBitmap(bitmap, null, rectF, null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                return BitmapFactory.decodeStream(new ByteArrayInputStream(bos.toByteArray()));
            }else{
                return null;
            }
        }
        return null;
    }

    /**
     * 按指定尺寸压缩图片
     *
     * @param bmp    原图片
     * @param width  宽
     * @param height 高
     */
    public Bitmap compressBitmapBySize(Bitmap bmp, int width, int height) {
        Bitmap bitmap = null;
        if (bmp != null) {
            int bmpWidth = bmp.getWidth();
            int bmpHeight = bmp.getHeight();
            if (width != 0 && height != 0) {
                Matrix matrix = new Matrix();
                float scaleWidth = ((float) width / bmpWidth);
                float scaleHeight = ((float) height / bmpHeight);
                matrix.postScale(scaleWidth, scaleHeight);
                bitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
            } else {
                bitmap = bmp;
            }
        }
        return bitmap;
    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    public void saveBitmap(Bitmap bitmap, String parentPath, String name) {
        if (bitmap != null && parentPath != null && name != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法
            File parent = new File(parentPath);
            if (!parent.exists()) {
                parent.mkdirs();
            }
            File file = new File(parent, name);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 根据图片大小调整尺寸缩小的比例
     */
    private int getScale(String path) {
        if (path == null) {
            return 0;
        }
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);
        int width = newOpts.outWidth;
        int height = newOpts.outHeight;
        if (width <= 400) {
            return 1;
        } else if (width <=800) {
            return 2;
        } else if (width <= 1200) {
            return 4;
        } else {
            return 8;
        }
    }




}
