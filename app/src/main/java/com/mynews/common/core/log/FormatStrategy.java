package com.mynews.common.core.log;

public interface FormatStrategy {

  void log(int priority, String tag, String message);
}
