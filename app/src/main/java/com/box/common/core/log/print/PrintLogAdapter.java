package com.box.common.core.log.print;

import com.box.app.news.BuildConfig;
import com.box.common.core.log.FormatStrategy;
import com.box.common.core.log.LogAdapter;
import com.box.common.core.log.PrettyFormatStrategy;

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