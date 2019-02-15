package com.example.picture.entity;

import android.os.Parcel;
import android.os.Parcelable;

/*Created by 邱强 on 2019/2/11.
 * E-Mail 2536555456@qq.com
 */
public class UpTempInfo  implements Parcelable {
  private   String msg;
  private  ImagesInfo info;

    public UpTempInfo() {
    }

    protected UpTempInfo(Parcel in) {
        msg = in.readString();
        info = in.readParcelable(ImagesInfo.class.getClassLoader());
    }

    public static final Creator<UpTempInfo> CREATOR = new Creator<UpTempInfo>() {
        @Override
        public UpTempInfo createFromParcel(Parcel in) {
            return new UpTempInfo(in);
        }

        @Override
        public UpTempInfo[] newArray(int size) {
            return new UpTempInfo[size];
        }
    };

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ImagesInfo getInfo() {
        return info;
    }

    public void setInfo(ImagesInfo info) {
        this.info = info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msg);
        dest.writeParcelable(info, flags);
    }
}
