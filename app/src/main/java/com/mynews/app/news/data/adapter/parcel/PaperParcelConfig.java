package com.mynews.app.news.data.adapter.parcel;

import paperparcel.Adapter;
import paperparcel.ProcessorConfig;

@ProcessorConfig(adapters = {@Adapter(value = ParcelablesArrayListTypeAdapter.class, nullSafe = true),
        @Adapter(value = ParcelablesReferTypeAdapter.class, nullSafe = true)})
public interface PaperParcelConfig {
}