package com.mynews.common.core.log;

public interface LogStrategy {

  void log(int priority, String tag, String message);
}
