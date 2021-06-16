package com.gouzhong1223.netty.rpc.consumer.proxy;

import com.alibaba.fastjson.JSON;
import com.gouzhong1223.netty.rpc.consumer.client.NettyClient;
import com.gouzhong1223.simple.netty.rpc.common.RpcRequest;
import com.gouzhong1223.simple.netty.rpc.common.RpcResponse;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-06-15 23:05
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : com.gouzhong1223.netty.rpc.consumer.proxy
 * @ProjectName : simple-netty-rpc
 * @Version : 1.0.0
 */
@Component
public class RpcProxy {

    private final NettyClient nettyClient;

    Map<Class<?>, Object> SERVICE_MAP = new HashMap<>();

    public RpcProxy(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    public Object getProxy(Class<?> serviceClass) {
        Object serviceProxy = SERVICE_MAP.get(serviceClass);
        if (serviceProxy == null) {
            Object proxyInstance = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{serviceClass}, (proxy, method, args) -> {
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setRequestId(UUID.randomUUID().toString());
                rpcRequest.setClassName(method.getDeclaringClass().getName());
                rpcRequest.setParameterTypes(method.getParameterTypes());
                rpcRequest.setParameters(args);
                rpcRequest.setMethodName(method.getName());
                try {
                    Object response = nettyClient.send(JSON.toJSONString(rpcRequest));
                    RpcResponse rpcResponse = JSON.parseObject(response.toString(), RpcResponse.class);
                    if (rpcResponse.getError() != null) {
                        throw new RuntimeException(rpcResponse.getError());
                    }
                    if (rpcResponse.getResult() != null) {
                        return JSON.parseObject(rpcResponse.getResult().toString(), method.getReturnType());
                    }
                    return null;
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    throw e;
                }
            });
            SERVICE_MAP.put(serviceClass, proxyInstance);
            return proxyInstance;
        } else {
            return serviceProxy;
        }
    }
}
