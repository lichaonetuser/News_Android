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

package com.box.common.core.net.http.exception;

import java.util.Locale;

public class ServerException extends RuntimeException {

    private int status;
    private String message;
    private String error;

    public ServerException(int status, String message, String error) {
        super(message);
        this.message = message;
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "\nstatus = %d\n" +
                        "message = %s\n" +
                        "error = %s", status, message, error);
    }
}