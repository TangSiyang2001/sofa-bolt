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
package com.alipay.remoting.util;

import com.alipay.remoting.config.ConfigManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ThreadFactory;

/**
 * Utils for netty EventLoop
 *
 * @author YANGLiiN
 * @version $Id: NettyEventLoopUtil.java, v 1.5 2018-05-28 14:07 YANGLiiN $
 */
public class NettyEventLoopUtil {

    /** check whether epoll enabled, and it would not be changed during runtime. */
    private static boolean epollEnabled = ConfigManager.netty_epoll() && Epoll.isAvailable();

    /**
     * Create the right event loop according to current platform and system property, fallback to NIO when epoll not enabled.
     *
     * @param nThreads
     * @param threadFactory
     * @return an EventLoopGroup suitable for the current platform
     */
    public static EventLoopGroup newEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        return epollEnabled ? new EpollEventLoopGroup(nThreads, threadFactory)
            : new NioEventLoopGroup(nThreads, threadFactory);
    }

    /**
     * @return a SocketChannel class suitable for the given EventLoopGroup implementation
     */
    public static Class<? extends SocketChannel> getClientSocketChannelClass() {
        return epollEnabled ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    /**
     * @return a ServerSocketChannel class suitable for the given EventLoopGroup implementation
     */
    public static Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        return epollEnabled ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    /**
     * Use {@link EpollMode#LEVEL_TRIGGERED} for server bootstrap if level trigger enabled by system properties,
     *   otherwise use {@link EpollMode#EDGE_TRIGGERED}.
     * @param serverBootstrap server bootstrap
     */
    public static void enableTriggeredMode(ServerBootstrap serverBootstrap) {
        if (epollEnabled) {
            //TODO:epoll相关内容 https://www.cnblogs.com/aspirant/p/9166944.html
            //TODO:epoll的触发模式：https://cloud.tencent.com/developer/article/1636224
            //    对于水平触发模式，一个事件只要有，就会一直触发；
            //    对于边缘触发模式，只有一个事件从无到有才会触发。
            //LT模式下，只要这个fd还有数据可读，每次 epoll_wait都会返回它的事件，提醒用户程序去操作，
            // 而在ET（边缘触发）模式中，它只会提示一次，直到下次再有数据流入之前都不会再提示了，无论fd中是否还有数据可读。
            // 所以在ET模式下，read一个fd的时候一定要把它的buffer读光，也就是说一直读到read的返回值小于请求值，或者 遇到EAGAIN错误。
            if (ConfigManager.netty_epoll_lt_enabled()) {
                serverBootstrap.childOption(EpollChannelOption.EPOLL_MODE,
                    EpollMode.LEVEL_TRIGGERED);
            } else {
                serverBootstrap
                    .childOption(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
            }
        }
    }
}
