package com.gouzhong1223.netty.rpc.consumer.processor;

import com.gouzhong1223.netty.rpc.consumer.annotation.RpcReference;
import com.gouzhong1223.netty.rpc.consumer.proxy.RpcProxy;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-06-15 23:33
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : com.gouzhong1223.netty.rpc.consumer.processor
 * @ProjectName : simple-netty-rpc
 * @Version : 1.0.0
 */
@Component
public class RpcProcessor implements BeanPostProcessor {
    private final RpcProxy rpcProxy;

    public RpcProcessor(RpcProxy rpcProxy) {
        this.rpcProxy = rpcProxy;
    }

    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference annotation = declaredField.getAnnotation(RpcReference.class);
            if (annotation != null) {
                Object proxy = rpcProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                declaredField.set(bean, proxy);
            }
        }
        return bean;
    }
}
