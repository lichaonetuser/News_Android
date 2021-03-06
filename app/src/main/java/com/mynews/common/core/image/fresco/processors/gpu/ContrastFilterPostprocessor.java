package com.mynews.common.core.image.fresco.processors.gpu;

/**
 * Copyright (C) 2017 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;

import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;

/**
 * contrast value ranges from 0.0 to 4.0, with 1.0 as the normal level
 */
public class ContrastFilterPostprocessor extends GPUFilterPostprocessor {

  private float contrast;

  public ContrastFilterPostprocessor(Context context) {
    this(context, 1.0f);
  }

  public ContrastFilterPostprocessor(Context context, float contrast) {
    super(context, new GPUImageContrastFilter());
    this.contrast = contrast;

    GPUImageContrastFilter filter = getFilter();
    filter.setContrast(contrast);
  }

  @Override public CacheKey getPostprocessorCacheKey() {
    return new SimpleCacheKey("contrast=" + contrast);
  }
}