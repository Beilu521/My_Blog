package com.deerecho.My_Blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.deerecho.My_Blog.entity.User;

public interface UserService extends IService<User> {
    User findByEmail(String email);
    User findByNickname(String nickname);
    void register(String email, String nickname, String password);
    String login(String email, String password);
}
