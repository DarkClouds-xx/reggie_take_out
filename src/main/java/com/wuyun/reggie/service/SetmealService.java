package com.wuyun.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyun.reggie.dto.DishDto;
import com.wuyun.reggie.dto.SetmealDto;
import com.wuyun.reggie.entity.Setmeal;
import com.wuyun.reggie.entity.SetmealDish;

import java.util.List;

/**
 * Author：wy
 * Date：2023/4/12
 */

public interface SetmealService extends IService<Setmeal> {

    //新增套餐，同时保存套餐和菜品的关联信息
    public void saveWithDish(SetmealDto setmealDto);

    //删除套餐，同时删除套餐和菜品的关联信息
    public void removeWithDish(List<Long> ids);

    //根据id查询套餐信息，以及菜品信息
    public SetmealDto getByIdWithSetmealDish(Long id);

    //根据id修改套餐信息
    public void updateWithSetmealDish(SetmealDto setmealDto);
}
