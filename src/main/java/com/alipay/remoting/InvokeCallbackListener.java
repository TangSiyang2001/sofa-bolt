/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.remoting;

/**
 * Listener to listen response and invoke callback.
 * TODO:回调触发监听器，用于监听到响应后触发
 * 
 * @author jiangping
 * @version $Id: InvokeCallbackListener.java, v 0.1 2015-9-21 PM5:17:08 tao Exp $
 */
public interface InvokeCallbackListener {
    /**
     * Response arrived.
     * 
     * @param future
     */
    void onResponse(final InvokeFuture future);

    /**
     * Get the remote address.
     * 
     * @return
     */
    String getRemoteAddress();
}
