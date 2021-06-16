package com.gouzhong1223.netty.rpc.provider.server;

import com.gouzhong1223.netty.rpc.provider.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description : NettyServer
 * @Date : create by QingSong in 2021-06-14 22:17
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : com.gouzhong1223.netty.rpc.provider.server
 * @ProjectName : simple-netty-rpc
 * @Version : 1.0.0
 */
@Component
public class NettyServer {

    private final NettyServerHandler nettyServerHandler;

    NioEventLoopGroup bossGroup;
    NioEventLoopGroup wokerGroup;
    ChannelFuture sync;

    public NettyServer(NettyServerHandler nettyServerHandler) {
        this.nettyServerHandler = nettyServerHandler;
    }

    /**
     * Netty服务启动
     *
     * @param host ip
     * @param port 端口
     */
    public void start(String host, int port) {
        try {
            bossGroup = new NioEventLoopGroup(1);
            wokerGroup = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, wokerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(nettyServerHandler);
                        }
                    });

            sync = serverBootstrap.bind(host, port).sync();
            System.out.println("Netty Start...");
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (wokerGroup != null) {
                wokerGroup.shutdownGracefully();
            }
        }

    }

    /**
     * 初始化完成调用
     */
    @PostConstruct
    public void run() {
        System.out.println("PostConstruct");
        new Thread(() -> start("127.0.0.1", 8899)).start();
    }

    /**
     * 容器销毁调动
     */
    @PreDestroy
    public void close() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (wokerGroup != null) {
            wokerGroup.shutdownGracefully();
        }
    }
}
