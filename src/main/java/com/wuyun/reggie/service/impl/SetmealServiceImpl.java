package com.wuyun.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyun.reggie.common.CustomException;
import com.wuyun.reggie.common.R;
import com.wuyun.reggie.dto.DishDto;
import com.wuyun.reggie.dto.SetmealDto;
import com.wuyun.reggie.entity.DishFlavor;
import com.wuyun.reggie.entity.Setmeal;
import com.wuyun.reggie.entity.SetmealDish;
import com.wuyun.reggie.mapper.SetmealMapper;
import com.wuyun.reggie.service.SetmealDishService;
import com.wuyun.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author：wy
 * Date：2023/4/12
 */

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联信息
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时删除套餐和菜品的关联信息
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        if (count > 0) {
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中数据
        this.removeByIds(ids);

        //再删除关系表中数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }

    /**
     * 根据id查询套餐信息，以及菜品信息
     * @param id
     * @return
     */
    @Override
    @Transactional
    public SetmealDto getByIdWithSetmealDish(Long id) {
        //查询套餐基本信息
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        //数据迁移
        BeanUtils.copyProperties(setmeal,setmealDto);

        //构造条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    /**
     * 根据id修改套餐信息
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithSetmealDish(SetmealDto setmealDto) {
        //保存基本套餐信息
        this.updateById(setmealDto);

        //构造条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());

        //先删除当前套餐对应的菜品
        setmealDishService.remove(queryWrapper);

        //给套餐菜品赋值对应的套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        ///保存套餐菜品数据到菜品表setmeal_dish
        setmealDishService.saveBatch(setmealDishes);
    }

}
