package com.wuyun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyun.reggie.common.CustomException;
import com.wuyun.reggie.common.R;
import com.wuyun.reggie.dto.DishDto;
import com.wuyun.reggie.dto.SetmealDto;
import com.wuyun.reggie.entity.Category;
import com.wuyun.reggie.entity.Dish;
import com.wuyun.reggie.entity.Setmeal;
import com.wuyun.reggie.entity.SetmealDish;
import com.wuyun.reggie.service.CategoryService;
import com.wuyun.reggie.service.SetmealDishService;
import com.wuyun.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author：wy
 * Date：2023/4/14
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize, String name) {
        //构造分页条件
        Page<Setmeal> pageInfo = new Page(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page();

        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行查询
        setmealService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类的信息
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //获取分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);

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

        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        //修改状态
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);

        //根据id批量修改信息
        setmealService.update(setmeal,queryWrapper);

        return R.success("修改成功");
    }

    /**
     * 根据id查询套餐信息回显
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<SetmealDto> getSetmealDto(@PathVariable("id") Long id) {
        //DishDto dishDto = dishService.getByIdWithFlavor(id);
        SetmealDto setmealDto = setmealService.getByIdWithSetmealDish(id);

        return R.success(setmealDto);
    }

    /**
     * 根据id修改套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithSetmealDish(setmealDto);

        return R.success("修改套餐成功");
    }

    /**
     * 根据条件查询对应的套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        //查询状态为1的
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,1);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 根据套餐id查询套餐对应的菜品数据
     * @param setmealId
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> SetmealDishList(@PathVariable("id") Long setmealId) {
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        return R.success(list);
    }
}
