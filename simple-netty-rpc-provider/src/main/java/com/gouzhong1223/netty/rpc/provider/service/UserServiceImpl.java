package com.gouzhong1223.netty.rpc.provider.service;

import com.gouzhong1223.netty.rpc.provider.annotation.RpcService;
import com.gouzhong1223.simple.netty.rpc.api.UserService;
import com.gouzhong1223.simple.netty.rpc.pojo.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-06-14 22:10
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : com.gouzhong1223.netty.rpc.provider.service
 * @ProjectName : simple-netty-rpc
 * @Version : 1.0.0
 */
@RpcService
@Service
public class UserServiceImpl implements UserService {
    static HashMap<Integer, User> hashMap = new HashMap<>();

    @Override
    public User getUserById(Integer id) {
        hashMap.put(1, new User(1, "zhangsan"));
        hashMap.put(2, new User(2, "lisi"));
        return hashMap.getOrDefault(id, hashMap.get(1));
    }
}
