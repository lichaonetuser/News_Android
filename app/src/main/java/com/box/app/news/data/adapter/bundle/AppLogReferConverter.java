package com.box.app.news.data.adapter.bundle;

import com.google.protobuf.InvalidProtocolBufferException;
import com.yatatsu.autobundle.AutoBundleConverter;

import com.box.app.news.proto.AppLog;

public class AppLogReferConverter implements AutoBundleConverter<AppLog.Refer, byte[]> {

    @Override
    public byte[] convert(AppLog.Refer o) {
        return o.toByteArray();
    }

    @Override
    public AppLog.Refer original(byte[] s) {
        try {
            return AppLog.Refer.parseFrom(s);
        } catch (InvalidProtocolBufferException e) {
            return AppLog.Refer.getDefaultInstance();
        }
    }
}