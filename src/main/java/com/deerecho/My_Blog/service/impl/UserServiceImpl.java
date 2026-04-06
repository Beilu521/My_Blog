package com.deerecho.My_Blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deerecho.My_Blog.entity.User;
import com.deerecho.My_Blog.mapper.UserMapper;
import com.deerecho.My_Blog.service.UserService;
import com.deerecho.My_Blog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public User findByEmail(String email) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User findByNickname(String nickname) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("nickname", nickname);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public void register(String email, String nickname, String password) {
        if (findByEmail(email) != null) {
            throw new RuntimeException("该邮箱已被注册");
        }
        if (findByNickname(nickname) != null) {
            throw new RuntimeException("该昵称已被使用");
        }
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setPassword(password);
        user.setRole(0);
        save(user);
    }

    @Override
    public String login(String email, String password) {
        User user = findByEmail(email);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return jwtUtil.createToken(user.getId(), user.getNickname());
    }
}
