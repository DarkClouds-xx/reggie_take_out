package com.wuyun.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyun.reggie.entity.Category;

/**
 * Author：wy
 * Date：2023/4/12
 */

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
