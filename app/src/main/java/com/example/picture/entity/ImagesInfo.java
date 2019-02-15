package com.example.picture.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ScrollView;

/*Created by 邱强 on 2019/1/21.
 * E-Mail 2536555456@qq.com
 */

//对应表 images：图片信息
public class ImagesInfo implements Parcelable {
    private  String _id;
    private  String path;
    private String size;
    private  String  name;
    private  String mime_type;
    private String date_added;
    private  String date_modified;
    private  String bucket_id;
    private  String bucket_display_name;
    private  boolean isPrivate;

    //上传进度
    private  int progress;
    private  int resID;
    //是否选中
    private  boolean checked;
    //是否是点击拍照拍摄的
    private  boolean isCamera;

    public ImagesInfo() {
    }

    protected ImagesInfo(Parcel in) {
        _id = in.readString();
        path = in.readString();
        size = in.readString();
        name = in.readString();
        mime_type = in.readString();
        date_added = in.readString();
        date_modified = in.readString();
        bucket_id = in.readString();
        bucket_display_name = in.readString();
        isPrivate = in.readByte() != 0;
        progress = in.readInt();
        resID = in.readInt();
        checked = in.readByte() != 0;
        isCamera = in.readByte() != 0;
    }

    public static final Creator<ImagesInfo> CREATOR = new Creator<ImagesInfo>() {
        @Override
        public ImagesInfo createFromParcel(Parcel in) {
            return new ImagesInfo(in);
        }

        @Override
        public ImagesInfo[] newArray(int size) {
            return new ImagesInfo[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
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

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isCamera() {
        return isCamera;
    }

    public void setCamera(boolean camera) {
        isCamera = camera;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(path);
        dest.writeString(size);
        dest.writeString(name);
        dest.writeString(mime_type);
        dest.writeString(date_added);
        dest.writeString(date_modified);
        dest.writeString(bucket_id);
        dest.writeString(bucket_display_name);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeInt(progress);
        dest.writeInt(resID);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeByte((byte) (isCamera ? 1 : 0));
    }

}
