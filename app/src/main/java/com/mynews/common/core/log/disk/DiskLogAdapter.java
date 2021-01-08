package com.mynews.common.core.log.disk;

import com.mynews.common.core.log.FormatStrategy;
import com.mynews.common.core.log.LogAdapter;

public class DiskLogAdapter implements LogAdapter {

  private final FormatStrategy formatStrategy;

  public DiskLogAdapter() {
    formatStrategy = CsvFormatStrategy.newBuilder().build();
  }

  public DiskLogAdapter(FormatStrategy formatStrategy) {
    this.formatStrategy = formatStrategy;
  }

  @Override public boolean isLoggable(int priority, String tag) {
    return true;
  }

  @Override public void log(int priority, String tag, String message) {
    formatStrategy.log(priority, tag, message);
  }
}