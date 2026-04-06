package com.deerecho.My_Blog.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_user")
public class User extends BaseEntity {
    private String email;
    private String nickname;
    private String password;
    private Integer role;
    @TableField(exist = false)
    private String token;

    public static final int ROLE_GUEST = -1;
    public static final int ROLE_USER = 0;
    public static final int ROLE_ADMIN = 1;
}