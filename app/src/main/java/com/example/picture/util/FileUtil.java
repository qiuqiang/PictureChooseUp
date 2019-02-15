package com.example.picture.util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*Created by 邱强 on 2019/1/21.
 * E-Mail 2536555456@qq.com
 */
public class FileUtil {
    private String tempName = "缓存图片";//暂存  上传结束立即删除
    private String photoName = "拍照材料";

    private FileUtil() {

    }

    public static FileUtil newInstance() {

        return FileUtil.UtilsClass.INSTANCE;
    }

    private static class UtilsClass {
        private static final FileUtil INSTANCE = new FileUtil();
    }

    public String getTakePhotoPath(Context context) {
        MyAppUtils myAppUtils = new MyAppUtils(context);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + myAppUtils.getApplicationName() + File.separator + photoName;
        }
        return null;
    }


    /**
     * 获取sd卡根目录
     */
    public String getExternalBasePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "";
    }

    /**
     * 读取文件
     *
     * @param filePath 文件路径
     * @param data     数据
     */
    public void writeToFile(String filePath, String data) {
        if (filePath != null && data != null) {
            File file = new File(filePath);
            if (file.exists()) {
                writeToFile(file, data);
            } else {
                try {
                    boolean newFile = file.createNewFile();
                    if (newFile) {
                        writeToFile(file, data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void write(File file, String data) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data.getBytes());
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void writeToFile(File file, String data) {
        if (file != null && file.exists() && data != null) {
            writeToFile(file.getAbsoluteFile(), data);
        }
    }

    /**
     * 读取文件
     *
     * @param path 文件路径
     */

    public String readFile(String path) {
        if (path != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            File file = new File(path);
            if (file.exists()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    byte[] bytes = new byte[2048];
                    int len = 0;
                    while ((len = fis.read(bytes)) != -1) {
                        outputStream.write(fis.read(bytes, 0, len));
                    }
                    outputStream.flush();
                    return new String(outputStream.toByteArray());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return "";
    }

    /**
     * 读取文件
     *
     * @param file 文件
     */
    public String readFile(File file) {
        return readFile(file.getAbsoluteFile());
    }

    private int c = 0;

    /**
     * 判断文件是否还在写入
     *
     * @param fileName 文件名
     * @param time     间隔时间 毫秒
     * @param count    读取次数
     */
    public boolean isFileWriteOver(String fileName, long time, int count) {
        long oldLen = 0;
        long newLen = 0;
        if (fileName == null) {
            return false;
        }
        File file = new File(fileName);
        if (file.exists()) {
            while (true) {
                newLen = file.length();
                if ((newLen - oldLen) > 0) {
                    oldLen = newLen;
                    try {
                        count++;
                        Thread.sleep(time);
                        if (c == count) {
                            return false;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }


}
