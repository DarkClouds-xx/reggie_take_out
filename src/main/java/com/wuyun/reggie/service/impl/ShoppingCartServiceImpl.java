package com.wuyun.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyun.reggie.entity.ShoppingCart;
import com.wuyun.reggie.mapper.ShoppingCartMapper;
import com.wuyun.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * Author：wy
 * Date：2023/4/18
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
