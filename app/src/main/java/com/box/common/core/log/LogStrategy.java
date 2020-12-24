package com.box.common.core.log;

public interface LogStrategy {

  void log(int priority, String tag, String message);
}
