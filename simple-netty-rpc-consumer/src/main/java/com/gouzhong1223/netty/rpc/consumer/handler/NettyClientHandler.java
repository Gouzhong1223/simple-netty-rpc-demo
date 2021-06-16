package com.gouzhong1223.netty.rpc.consumer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-06-15 22:33
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : com.gouzhong1223.netty.rpc.consumer.handler
 * @ProjectName : simple-netty-rpc
 * @Version : 1.0.0
 */
@Component
public class NettyClientHandler extends SimpleChannelInboundHandler<String> implements Callable<Object> {
    ChannelHandlerContext context;
    private String regMsg;
    private String respMsg;

    public void setRegMsg(String regMsg) {
        this.regMsg = regMsg;
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        respMsg = msg;
        notify();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.context = ctx;
    }

    @Override
    public synchronized Object call() throws Exception {
        context.writeAndFlush(regMsg);
        wait();
        return respMsg;
    }
}
