package com.wuyun.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuyun.reggie.common.BaseContext;
import com.wuyun.reggie.common.R;
import com.wuyun.reggie.entity.ShoppingCart;
import com.wuyun.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author：wy
 * Date：2023/4/18
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        //设置用户id，指定当前购物车的数据是哪个用户的
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //判断购物车中是否已经存在该菜品或套餐
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        if (shoppingCart.getDishId() != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(queryWrapper);

        if (shoppingCartOne != null) {
            //存在就在原来的数量上修改
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartOne);

        }else {
            //不存在就添加新的菜品
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }

        return R.success(shoppingCartOne);
    }


    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        //获取用户id，根据id查询
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        //修改购物车里面菜品价格
        list = list.stream().map((item) -> {
            BigDecimal number = new BigDecimal(item.getNumber().toString());

            //菜品价格=菜品数量*菜品单价
            BigDecimal amount = item.getAmount().multiply(number);

            item.setAmount(amount);

            return item;
        }).collect(Collectors.toList());

        return R.success(list);
    }

    /**
     * 购物车菜品数量减少
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getDishId,dishId);

        ShoppingCart shoppingCartOne = shoppingCartService.getOne(queryWrapper);
        Integer number = shoppingCartOne.getNumber();
        //判断当前菜品数量，如果等于1，就删除菜品，
        if (number == 1) {
            shoppingCartService.removeById(shoppingCartOne);
        }

        //否则就减少一份
        shoppingCartOne.setNumber(number - 1);
        shoppingCartService.updateById(shoppingCartOne);

        return R.success("减少菜品数量成功");
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        //获取用户id，根据id删除
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}
