package com.box.app.news.data.adapter;


import androidx.annotation.NonNull;

import com.github.gfx.android.orma.annotation.StaticTypeAdapter;
import com.github.gfx.android.orma.annotation.StaticTypeAdapters;
import com.google.protobuf.InvalidProtocolBufferException;

import com.box.app.news.proto.AppLog;


/**
 * 翻译AppLog对象为比特数据存储到数据库
 * 如果反序列化失败，抛出运行时异常
 * 上层如果查询时捕获到这个异常，直接清空对应表
 * 因为表中的数据已经不适用用当前的统计了
 */
@StaticTypeAdapters({
        @StaticTypeAdapter(
                targetType = AppLog.Event.class,
                serializedType = byte[].class,
                serializer = "serializerAppLogEvent",
                deserializer = "deserializerAppLogEvent"
        ),
        @StaticTypeAdapter(
                targetType = AppLog.Impression.class,
                serializedType = byte[].class,
                serializer = "serializerAppLogImpression",
                deserializer = "deserializerAppLogImpression"
        ),
        @StaticTypeAdapter(
                targetType = AppLog.ImpressionCell.class,
                serializedType = byte[].class,
                serializer = "serializerAppLogImpressionCell",
                deserializer = "deserializerAppLogImpressionCell"
        )
})
@SuppressWarnings("WeakerAccess")
public class DBAppLogAdapter {

    public static byte[] serializerAppLogEvent(@NonNull AppLog.Event target) {
        return target.toByteArray();
    }

    @NonNull
    public static AppLog.Event deserializerAppLogEvent(byte[] bytes) {
        try {
            return AppLog.Event.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("deserializerAppLogEvent Exception!", e);
        }
    }

    public static byte[] serializerAppLogImpression(@NonNull AppLog.Impression target) {
        return target.toByteArray();
    }

    @NonNull
    public static AppLog.Impression deserializerAppLogImpression(byte[] bytes) {
        try {
            return AppLog.Impression.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("deserializerAppLogImpression Exception!", e);
        }
    }

    public static byte[] serializerAppLogImpressionCell(@NonNull AppLog.ImpressionCell target) {
        return target.toByteArray();
    }

    @NonNull
    public static AppLog.ImpressionCell deserializerAppLogImpressionCell(byte[] bytes) {
        try {
            return AppLog.ImpressionCell.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("deserializerAppLogImpressionCell Exception!", e);
        }
    }
}