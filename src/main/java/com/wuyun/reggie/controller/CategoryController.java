package com.wuyun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyun.reggie.common.R;
import com.wuyun.reggie.entity.Category;
import com.wuyun.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author：wy
 * Date：2023/4/12
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询菜品分类分页信息
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize) {
        //构造分页条件
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 新增菜品，套餐
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {

        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 删除菜品，套餐
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        categoryService.remove(id);

        return R.success("删除成功");
    }

    /**
     * 根据id修改分类信息
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 根据type查询菜品分类
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> getType(Category category) {
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //添加条件
        queryWrapper.eq(category.getType() != null, Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }

}
