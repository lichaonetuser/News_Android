package com.box.app.news.data.adapter.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.util.ArrayList;

import paperparcel.TypeAdapter;

public class ParcelablesArrayListTypeAdapter<T extends Parcelable> implements TypeAdapter<ArrayList<T>> {
    @Override
    public ArrayList<T> readFromParcel(@NonNull Parcel source) {
        ArrayList<T> outList = new ArrayList<>();
        source.readList(outList, ParcelablesArrayListTypeAdapter.class.getClassLoader());
        return outList;
    }

    @Override
    public void writeToParcel(ArrayList<T> value, @NonNull Parcel dest, int flags) {
        dest.writeList(value);
    }
}