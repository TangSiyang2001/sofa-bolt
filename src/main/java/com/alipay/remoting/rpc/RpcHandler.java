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
package com.alipay.remoting.rpc;

import com.alipay.remoting.*;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ConcurrentMap;

/**
 * Dispatch messages to corresponding protocol.
 * 一个Dispatcher，集中处理请求
 * TODO:复习@Sharable注解的使用
 * @author jiangping
 * @version $Id: RpcHandler.java, v 0.1 2015-12-14 PM4:01:37 tao Exp $
 */
@ChannelHandler.Sharable
public class RpcHandler extends ChannelInboundHandlerAdapter {
    private final boolean serverSide;

    private final ConcurrentMap<String, UserProcessor<?>> userProcessors;

    public RpcHandler(ConcurrentMap<String, UserProcessor<?>> userProcessors) {
        serverSide = false;
        this.userProcessors = userProcessors;
    }

    public RpcHandler(boolean serverSide, ConcurrentMap<String, UserProcessor<?>> userProcessors) {
        this.serverSide = serverSide;
        this.userProcessors = userProcessors;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProtocolCode protocolCode = ctx.channel().attr(Connection.PROTOCOL).get();
        Protocol protocol = ProtocolManager.getProtocol(protocolCode);
        //TODO:在这里创建的RemotingContext，包装了netty的ChannelHandlerContext和其他一众玩意儿交给commandHandler
        protocol.getCommandHandler().handleCommand(
                new RemotingContext(ctx, new InvokeContext(), serverSide, userProcessors), msg);
        ctx.fireChannelRead(msg);
    }
}
