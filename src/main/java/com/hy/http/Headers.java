/**
 * Copyright © YOLANDA. All Rights Reserved
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hy.http;

/**
 * <p>Http header.</p>
 * Created in Jan 10, 2016 2:29:42 PM.
 *
 * @author YOLANDA;
 */
public interface Headers {

    String HEAD_KEY_RESPONSE_CODE = "ResponseCode";

    String HEAD_KEY_RESPONSE_MESSAGE = "ResponseMessage";

    String HEAD_KEY_ACCEPT = "Accept";

    String HEAD_KEY_ACCEPT_ENCODING = "Accept-Encoding";

    String HEAD_VALUE_ACCEPT_ENCODING = "gzip, deflate";// no sdch

    String HEAD_KEY_ACCEPT_LANGUAGE = "Accept-Language";

    String HEAD_KEY_CONTENT_TYPE = "Content-Type";

    String HEAD_KEY_CONTENT_LENGTH = "Content-Length";

    String HEAD_KEY_CONTENT_ENCODING = "Content-Encoding";

    String HEAD_KEY_CONTENT_RANGE = "Content-Range";

    String HEAD_KEY_CACHE_CONTROL = "Cache-Control";

    String HEAD_KEY_CONNECTION = "Connection";

    String HEAD_VALUE_CONNECTION_KEEP_ALIVE = "keep-alive";

    String HEAD_VALUE_CONNECTION_CLOSE = "close";

    String HEAD_KEY_DATE = "Date";

    String HEAD_KEY_EXPIRES = "Expires";

    String HEAD_KEY_E_TAG = "ETag";

    String HEAD_KEY_PRAGMA = "Pragma";

    String HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since";

    String HEAD_KEY_IF_NONE_MATCH = "If-None-Match";

    String HEAD_KEY_LAST_MODIFIED = "Last-Modified";

    String HEAD_KEY_LOCATION = "Location";

    String HEAD_KEY_USER_AGENT = "User-Agent";

    String HEAD_KEY_COOKIE = "Cookie";

    String HEAD_KEY_COOKIE2 = "Cookie2";

    String HEAD_KEY_SET_COOKIE = "Set-Cookie";

    String HEAD_KEY_SET_COOKIE2 = "Set-Cookie2";

    String HEAD_ACCEPT_STRING = "text/html,application/xhtml+xml,application/xml;*/*;q=0.9";

    String HEAD_ACCEPT_IMAGE = "image/*,*/*;q=1";

    String HEAD_ACCEPT_JSON = "application/json;q=1";

    String HEAD_ACCEPT_FILE = "*/*";
    ;
}