/**
 * Copyright © YOLANDA. All Rights Reserved
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
package com.hy.http

/**
 * Headers
 * @author HeYan
 * @time 2017/5/31 14:53
 */
interface Headers {
    companion object {

        val HEAD_KEY_RESPONSE_CODE = "ResponseCode"

        val HEAD_KEY_RESPONSE_MESSAGE = "ResponseMessage"

        val HEAD_KEY_ACCEPT = "Accept"

        val HEAD_KEY_ACCEPT_ENCODING = "Accept-Encoding"

        val HEAD_VALUE_ACCEPT_ENCODING = "gzip, deflate"// no sdch

        val HEAD_KEY_ACCEPT_LANGUAGE = "Accept-Language"

        val HEAD_KEY_CONTENT_TYPE = "Content-Type"

        val HEAD_KEY_CONTENT_LENGTH = "Content-Length"

        val HEAD_KEY_CONTENT_ENCODING = "Content-Encoding"

        val HEAD_KEY_CONTENT_RANGE = "Content-Range"

        val HEAD_KEY_CACHE_CONTROL = "Cache-Control"

        val HEAD_KEY_CONNECTION = "Connection"

        val HEAD_VALUE_CONNECTION_KEEP_ALIVE = "keep-alive"

        val HEAD_VALUE_CONNECTION_CLOSE = "close"

        val HEAD_KEY_DATE = "Date"

        val HEAD_KEY_EXPIRES = "Expires"

        val HEAD_KEY_E_TAG = "ETag"

        val HEAD_KEY_PRAGMA = "Pragma"

        val HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since"

        val HEAD_KEY_IF_NONE_MATCH = "If-None-Match"

        val HEAD_KEY_LAST_MODIFIED = "Last-Modified"

        val HEAD_KEY_LOCATION = "Location"

        val HEAD_KEY_USER_AGENT = "User-Agent"

        val HEAD_KEY_COOKIE = "Cookie"

        val HEAD_KEY_COOKIE2 = "Cookie2"

        val HEAD_KEY_SET_COOKIE = "Set-Cookie"

        val HEAD_KEY_SET_COOKIE2 = "Set-Cookie2"

        val HEAD_ACCEPT_STRING = "text/html,application/xhtml+xml,application/xml;*/*;q=0.9"

        val HEAD_ACCEPT_IMAGE = "image/*,*/*;q=1"

        val HEAD_ACCEPT_JSON = "application/json;q=1"

        val HEAD_ACCEPT_FILE = "*/*"
    }
}
