package com.gouzhong1223.netty.rpc.consumer.client;

import com.gouzhong1223.netty.rpc.consumer.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-06-15 22:25
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : com.gouzhong1223.netty.rpc.consumer.client
 * @ProjectName : simple-netty-rpc
 * @Version : 1.0.0
 */
@Component
public class NettyClient implements InitializingBean {

    private final NettyClientHandler nettyClientHandler;
    NioEventLoopGroup eventExecutors;
    Bootstrap bootstrap;
    Channel channel;
    ExecutorService executorService = Executors.newCachedThreadPool();

    public NettyClient(NettyClientHandler nettyClientHandler) {
        this.nettyClientHandler = nettyClientHandler;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            eventExecutors = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(nettyClientHandler);
                        }
                    });
            channel = bootstrap.connect("127.0.0.1", 8899).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
            if (eventExecutors != null) {
                eventExecutors.shutdownGracefully();
            }
            if (channel != null) {
                channel.close();
            }
        }
    }

    /**
     * 发送消息
     *
     * @param meg 消息
     * @return Object
     */
    public Object send(String meg) throws ExecutionException, InterruptedException {
        nettyClientHandler.setRegMsg(meg);
        Future<Object> submit = executorService.submit(nettyClientHandler);
        return submit.get();
    }


    @PreDestroy
    public void close() {
        if (eventExecutors != null) {
            eventExecutors.shutdownGracefully();
        }
        if (channel != null) {
            channel.close();
        }
    }
}
