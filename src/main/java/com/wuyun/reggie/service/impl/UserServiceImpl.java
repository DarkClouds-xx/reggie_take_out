package com.wuyun.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyun.reggie.entity.User;
import com.wuyun.reggie.mapper.UserMapper;
import com.wuyun.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Author：wy
 * Date：2023/4/14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
