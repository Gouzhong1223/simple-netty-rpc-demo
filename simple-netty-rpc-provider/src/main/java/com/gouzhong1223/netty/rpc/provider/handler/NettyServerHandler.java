package com.gouzhong1223.netty.rpc.provider.handler;

import com.alibaba.fastjson.JSON;
import com.gouzhong1223.netty.rpc.provider.annotation.RpcService;
import com.gouzhong1223.simple.netty.rpc.common.RpcRequest;
import com.gouzhong1223.simple.netty.rpc.common.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description : NettyServerHandler
 * @Date : create by QingSong in 2021-06-14 22:31
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : com.gouzhong1223.netty.rpc.provider.handler
 * @ProjectName : simple-netty-rpc
 * @Version : 1.0.0
 */
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<String> implements ApplicationContextAware {
    static HashMap<String, Object> SERVICE_INSTANCE_MAP = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        RpcRequest rpcRequest = JSON.parseObject(msg, RpcRequest.class);
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResponseId(rpcRequest.getRequestId());
        try {
            rpcResponse.setResult(handler(rpcRequest));
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setError(e.getMessage());
        }
        ctx.writeAndFlush(JSON.toJSONString(rpcResponse));
    }

    private Object handler(RpcRequest rpcRequest) throws InvocationTargetException {
        Object serviceBean = SERVICE_INSTANCE_MAP.get(rpcRequest.getClassName());
        if (serviceBean == null) {
            throw new RuntimeException("服务端没有找到服务");
        }
        FastClass fastClass = FastClass.create(serviceBean.getClass());
        FastMethod method = fastClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        return method.invoke(serviceBean, rpcRequest.getParameters());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> rpcServices = applicationContext.getBeansWithAnnotation(RpcService.class);
        for (Map.Entry<String, Object> entry : rpcServices.entrySet()) {
            Object serviceBean = entry.getValue();
            if (serviceBean.getClass().getInterfaces().length == 0) {
                throw new RuntimeException("RPC 服务类必须实现接口");
            }
            String serviceName = serviceBean.getClass().getInterfaces()[0].getName();
            SERVICE_INSTANCE_MAP.put(serviceName, serviceBean);
        }
    }
}
