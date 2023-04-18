package com.wuyun.reggie.dto;

import com.wuyun.reggie.entity.Setmeal;
import com.wuyun.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
