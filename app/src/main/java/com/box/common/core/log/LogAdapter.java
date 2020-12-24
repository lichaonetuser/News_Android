package com.box.common.core.log;

public interface LogAdapter {

  boolean isLoggable(int priority, String tag);

  void log(int priority, String tag, String message);
}