package com.mynews.common.core.log.print;

import com.mynews.app.news.BuildConfig;
import com.mynews.common.core.log.FormatStrategy;
import com.mynews.common.core.log.LogAdapter;
import com.mynews.common.core.log.PrettyFormatStrategy;

public class PrintLogAdapter implements LogAdapter {

    private final FormatStrategy formatStrategy;

    public PrintLogAdapter() {
        this.formatStrategy = PrettyFormatStrategy.newBuilder().build();
    }

    public PrintLogAdapter(FormatStrategy formatStrategy) {
        this.formatStrategy = formatStrategy;
    }

    @Override public boolean isLoggable(int priority, String tag) {
        return BuildConfig.DEBUG;
    }

    @Override public void log(int priority, String tag, String message) {
        formatStrategy.log(priority, tag, message);
    }

}