package com.example.picture.entity;

import android.os.Parcel;
import android.os.Parcelable;

/*Created by 邱强 on 2019/1/24.
 * E-Mail 2536555456@qq.com
 */

//相册信息 从images表中提取出来

public class AlbumInfo implements  Parcelable{
    private String bucket_id;//相册id
    private String bucket_display_name;//相册名字
    private String count;//相册图片数量
    private String date_added;//创建日期
    private String date_modified;
    private  String firstImagePath;
    private  boolean isExternal=true;
    public AlbumInfo() {
    }

    protected AlbumInfo(Parcel in) {
        bucket_id = in.readString();
        bucket_display_name = in.readString();
        count = in.readString();
        date_added = in.readString();
        date_modified = in.readString();
        firstImagePath = in.readString();
        isExternal = in.readByte() != 0;
    }

    public static final Creator<AlbumInfo> CREATOR = new Creator<AlbumInfo>() {
        @Override
        public AlbumInfo createFromParcel(Parcel in) {
            return new AlbumInfo(in);
        }

        @Override
        public AlbumInfo[] newArray(int size) {
            return new AlbumInfo[size];
        }
    };

    public String getBucket_id() {
        return bucket_id;
    }

    public void setBucket_id(String bucket_id) {
        this.bucket_id = bucket_id;
    }

    public String getBucket_display_name() {
        return bucket_display_name;
    }

    public void setBucket_display_name(String bucket_display_name) {
        this.bucket_display_name = bucket_display_name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bucket_id);
        dest.writeString(bucket_display_name);
        dest.writeString(count);
        dest.writeString(date_added);
        dest.writeString(date_modified);
        dest.writeString(firstImagePath);
        dest.writeByte((byte) (isExternal ? 1 : 0));
    }
}
