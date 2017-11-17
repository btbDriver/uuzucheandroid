package com.youyou.uucar.PB;

import android.os.Parcel;
import android.os.Parcelable;

import com.uu.client.bean.head.HeaderCommon;

/**
 * Created by 16515_000 on 2014/12/2.
 */
public class BaseModel implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public HeaderCommon.ResponseCommonMsg getMsg() {
        return msg;
    }

    public void setMsg(HeaderCommon.ResponseCommonMsg msg) {
        this.msg = msg;
    }

    HeaderCommon.ResponseCommonMsg msg;
}
