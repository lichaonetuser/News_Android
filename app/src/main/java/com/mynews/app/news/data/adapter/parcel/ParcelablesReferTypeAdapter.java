package com.mynews.app.news.data.adapter.parcel;

import android.os.Parcel;
import androidx.annotation.NonNull;

import com.google.protobuf.InvalidProtocolBufferException;

import com.mynews.app.news.proto.AppLog;
import paperparcel.TypeAdapter;

public class ParcelablesReferTypeAdapter implements TypeAdapter<AppLog.Refer> {

    @Override
    public AppLog.Refer readFromParcel(@NonNull Parcel source) {
        byte[] out = new byte[]{};
        source.readByteArray(out);
        try {
            return AppLog.Refer.parseFrom(out);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return AppLog.Refer.getDefaultInstance();
        }
    }

    @Override
    public void writeToParcel(AppLog.Refer value, @NonNull Parcel dest, int flags) {
        dest.writeByteArray(value.toByteArray());
    }

}