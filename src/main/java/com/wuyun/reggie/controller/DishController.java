package com.wuyun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyun.reggie.common.CustomException;
import com.wuyun.reggie.common.R;
import com.wuyun.reggie.dto.DishDto;
import com.wuyun.reggie.entity.*;
import com.wuyun.reggie.service.CategoryService;
import com.wuyun.reggie.service.DishFlavorService;
import com.wuyun.reggie.service.DishService;
import com.wuyun.reggie.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author：wy
 * Date：2023/4/13
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 菜品分页查询
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name) {
        //构造分页条件
        Page<Dish> pageInfo = new Page(page,pageSize);
        Page<DishDto> dishDtoPage = new Page();

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);//根据id查询分类的信息
            if (category != null) {
                String categoryName = category.getName();//获取分类名称
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 根据id查询菜品信息回显
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<DishDto> getDishDto(@PathVariable("id") Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 根据id修改菜品以及口味信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }

    /**
     * 根据id删除对应的菜品信息和口味信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        dishService.removeWithFlavor(ids);

        return R.success("删除成功");
    }

    /**
     * 根据id修改菜品启售停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") int status,@RequestParam List<Long> ids) {
        //先判断是否有套餐包含当前菜品,如果有则不能停售
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        int setmealDishCount = setmealDishService.count(setmealDishLambdaQueryWrapper);
        if (setmealDishCount > 0) {
            //不能停售，抛出一个业务异常
            throw  new CustomException("有套餐正在出售当前菜品，无法停售");
        }

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        //修改状态
        Dish dish = new Dish();
        dish.setStatus(status);

        //根据id批量修改信息
        dishService.update(dish,queryWrapper);

        return R.success("修改成功");
    }

//    /**
//     * 根据条件查询对应的菜品数据
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//        //构造条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId, dish.getCategoryId());
//        //查询状态为1的
//        queryWrapper.eq(Dish::getStatus, 1);
//        queryWrapper.like(dish.getName() != null,Dish::getName, dish.getName());
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }


    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId, dish.getCategoryId());
        //查询状态为1的
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.like(dish.getName() != null,Dish::getName, dish.getName());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);//根据id查询分类的信息
            if (category != null) {
                String categoryName = category.getName();//获取分类名称
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品id
            Long dishId = item.getId();
            //构造条件构造器
            LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper();
            dishFlavorQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //查询菜品对应的口味信息
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorQueryWrapper);
            if (dishFlavorList.size() > 0) {
                dishDto.setFlavors(dishFlavorList);
            }

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dtoList);
    }
}
