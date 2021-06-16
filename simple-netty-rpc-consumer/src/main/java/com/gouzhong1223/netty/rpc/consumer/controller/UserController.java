package com.gouzhong1223.netty.rpc.consumer.controller;

import com.gouzhong1223.netty.rpc.consumer.annotation.RpcReference;
import com.gouzhong1223.simple.netty.rpc.api.UserService;
import com.gouzhong1223.simple.netty.rpc.pojo.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-06-15 22:24
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : com.gouzhong1223.netty.rpc.consumer.controller
 * @ProjectName : simple-netty-rpc
 * @Version : 1.0.0
 */
@RestController
@RequestMapping("user")
public class UserController {

    @RpcReference
    UserService userService;

    @RequestMapping("getUserById")
    public User getUserById(Integer id) {
        return userService.getUserById(id);
    }
}
