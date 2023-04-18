package com.wuyun.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyun.reggie.common.CustomException;
import com.wuyun.reggie.dto.DishDto;
import com.wuyun.reggie.entity.*;
import com.wuyun.reggie.mapper.DishMapper;
import com.wuyun.reggie.service.DishFlavorService;
import com.wuyun.reggie.service.DishService;
import com.wuyun.reggie.service.SetmealDishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author：wy
 * Date：2023/4/12
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;


    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增菜品，同时保存口味数据
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存基本菜品信息
        this.save(dishDto);

        //获取菜品id
        Long dishId = dishDto.getId();

        //给菜品口味赋值对应的菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        ///保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息，以及口味信息
     */
    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //构造条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());

        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 修改菜品信息，同时修改口味数据
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //保存基本菜品信息
        this.updateById(dishDto);

        //构造条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        //先删除当前菜品对应的口味
        dishFlavorService.remove(queryWrapper);

        //给菜品口味赋值对应的菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        ///保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id删除菜品以及对应的口味
     */
    @Override
    @Transactional
    public void removeWithFlavor(List<Long> ids) {
        //查询菜品状态，是否可以删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(queryWrapper);
        if (count > 0) {
            //如果不能删除，抛出一个业务异常
            throw  new CustomException("菜品正在售卖中，不能删除");
        }

        //查询套餐中是否包含当前菜品，如果包含则不能删除
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        int setmealDishCount = setmealDishService.count(setmealDishLambdaQueryWrapper);
        if (setmealDishCount > 0) {
            //不能删除，抛出一个业务异常
            throw  new CustomException("有套餐包含当前菜品，请先删除相关套餐");
        }

        //如果可以删除，先删除菜品表中数据
        this.removeByIds(ids);

        //再删除口味表中数据
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lambdaQueryWrapper);
    }
}
