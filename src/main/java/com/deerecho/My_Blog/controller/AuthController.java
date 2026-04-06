package com.deerecho.My_Blog.controller;

import com.deerecho.My_Blog.common.Result;
import com.deerecho.My_Blog.entity.User;
import com.deerecho.My_Blog.service.MailService;
import com.deerecho.My_Blog.service.UserService;
import com.deerecho.My_Blog.util.JwtUtil;
import com.deerecho.My_Blog.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/register")
    public Result<String> register(@RequestParam String email,
                                   @RequestParam String nickname,
                                   @RequestParam String password) {
        try {
            userService.register(email, nickname, password);
            return Result.success("注册成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<User> login(@RequestParam String email,
                             @RequestParam String password,
                             @RequestParam(required = false) String code) {
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                return Result.error("用户不存在");
            }
            if (!password.equals(user.getPassword())) {
                return Result.error("密码错误");
            }
            if (user.getRole() == User.ROLE_ADMIN) {
                if (code == null || code.isBlank()) {
                    return Result.error(403, "管理员登录需要验证码");
                }
                String redisCode = redisTemplate.opsForValue().get("admin:code:" + email);
                if (redisCode == null) {
                    return Result.error("验证码已过期，请重新发送");
                }
                if (!redisCode.equals(code)) {
                    return Result.error("验证码错误");
                }
                redisTemplate.delete("admin:code:" + email);
            }
            String token = jwtUtil.createToken(user.getId(), user.getNickname());
            user.setToken(token);
            return Result.success("登录成功", user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/sendCode")
    public Result<String> sendCode(@RequestParam String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (user.getRole() != User.ROLE_ADMIN) {
            return Result.error("该用户无管理员权限");
        }
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().set("admin:code:" + email, code, 5, TimeUnit.MINUTES);
        mailService.sendMail(email, "博客管理员登录验证码",
                "您的登录验证码是：" + code + "，5分钟内有效，请勿泄露给他人。");
        return Result.success("验证码发送成功", null);
    }
}
