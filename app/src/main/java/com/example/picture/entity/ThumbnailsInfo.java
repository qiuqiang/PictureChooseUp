package com.example.picture.entity;

import android.os.Parcel;
import android.os.Parcelable;

/*Created by 邱强 on 2019/1/25.
 * E-Mail 2536555456@qq.com
 */
//对应thumbnails 缩略图表
public class ThumbnailsInfo implements Parcelable {
    private  int id;
    private  String path;
    private  String image_id;
    private  String kind;
    private  int width;
    private  int height;

    public ThumbnailsInfo() {
    }

    protected ThumbnailsInfo(Parcel in) {
        id = in.readInt();
        path = in.readString();
        image_id = in.readString();
        kind = in.readString();
        width = in.readInt();
        height = in.readInt();
    }

    public static final Creator<ThumbnailsInfo> CREATOR = new Creator<ThumbnailsInfo>() {
        @Override
        public ThumbnailsInfo createFromParcel(Parcel in) {
            return new ThumbnailsInfo(in);
        }

        @Override
        public ThumbnailsInfo[] newArray(int size) {
            return new ThumbnailsInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(path);
        dest.writeString(image_id);
        dest.writeString(kind);
        dest.writeInt(width);
        dest.writeInt(height);
    }
}
