package com.wuyun.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyun.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author：wy
 * Date：2023/4/14
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
