package com.gouzhong1223.simple.netty.rpc.api;

import com.gouzhong1223.simple.netty.rpc.pojo.User;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-06-14 22:06
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : com.gouzhong1223.simple.netty.rpc.api
 * @ProjectName : simple-netty-rpc
 * @Version : 1.0.0
 */
public interface UserService {

    /**
     * 根据用户 ID 获取用户信息
     *
     * @param id 用户 ID
     * @return {@link User}
     */
    User getUserById(Integer id);
}
