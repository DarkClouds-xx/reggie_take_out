package com.wuyun.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyun.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author：wy
 * Date：2023/4/11
 */

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
