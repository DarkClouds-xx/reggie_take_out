package com.wuyun.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyun.reggie.dto.DishDto;
import com.wuyun.reggie.entity.Dish;

import java.util.List;

/**
 * Author：wy
 * Date：2023/4/12
 */

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据
    public void saveWithFlavor(DishDto dishDto);

    //修改菜品，同时插入菜品对应的口味数据
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，口味信息
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品以及对应的口味
    public void removeWithFlavor(List<Long> ids);

}
