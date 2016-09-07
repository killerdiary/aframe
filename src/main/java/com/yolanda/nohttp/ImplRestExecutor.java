/*
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
package com.yolanda.nohttp;

/**
 * <p>The request executor, Interact with the network layer.</p>
 * Created in Jan 6, 2016 5:45:51 PM.
 *
 * @author YOLANDA;
 */
public interface ImplRestExecutor {

    /**
     * Execution request: request network, local cache, redirection, etc.
     *
     * @param request request.
     * @return {@link HttpResponse}.
     */
    HttpResponse executeRequest(Request<?> request);

}
