/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mynews.common.core.net.http.func;

import com.mynews.common.core.net.http.bean.APIResponse;
import com.mynews.common.core.net.http.exception.APIException;
import com.mynews.common.core.net.http.exception.ServerException;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;


/**
 * <p>描述：ApiResult<T>转换T</p>
 * 作者： zhouyou<br>
 * 日期： 2017/5/15 16:54 <br>
 * 版本： v1.0<br>
 */
public class HandleFuc<T> implements Function<APIResponse<T>, T> {
    @Override
    public T apply(@NonNull APIResponse<T> apiResult) throws Exception {
        if (APIException.isOk(apiResult)) {
            return apiResult.getData();// == null ? Optional.ofNullable(tApiResult.getData()).orElse(null) : tApiResult.getData();
        } else {
            throw new ServerException(apiResult.getStatus(), apiResult.getMessage(), apiResult.getError());
        }
    }
}
