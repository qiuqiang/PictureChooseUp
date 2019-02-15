package com.example.picture.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/*Created by 邱强 on 2019/1/30.
 * E-Mail 2536555456@qq.com
 */
public class FileTypeInfo implements Parcelable {
    private ArrayList<ImagesInfo> paths;//图片路径
    private String name;//名字
    private String fileCode;//code码
    private boolean isChooseOne;//是否多选一
    private String sample;  //样图
    private String upCount="0";//已上传张数

    public FileTypeInfo() {
    }

    protected FileTypeInfo(Parcel in) {
        paths = in.createTypedArrayList(ImagesInfo.CREATOR);
        name = in.readString();
        fileCode = in.readString();
        isChooseOne = in.readByte() != 0;
        sample = in.readString();
        upCount = in.readString();
    }

    public static final Creator<FileTypeInfo> CREATOR = new Creator<FileTypeInfo>() {
        @Override
        public FileTypeInfo createFromParcel(Parcel in) {
            return new FileTypeInfo(in);
        }

        @Override
        public FileTypeInfo[] newArray(int size) {
            return new FileTypeInfo[size];
        }
    };

    public ArrayList<ImagesInfo> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<ImagesInfo> paths) {
        this.paths = paths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileCode() {
        return fileCode;
    }

    public void setFileCode(String fileCode) {
        this.fileCode = fileCode;
    }

    public boolean isChooseOne() {
        return isChooseOne;
    }

    public void setChooseOne(boolean chooseOne) {
        isChooseOne = chooseOne;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getUpCount() {
        return upCount;
    }

    public void setUpCount(String upCount) {
        this.upCount = upCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(paths);
        dest.writeString(name);
        dest.writeString(fileCode);
        dest.writeByte((byte) (isChooseOne ? 1 : 0));
        dest.writeString(sample);
        dest.writeString(upCount);
    }
}
