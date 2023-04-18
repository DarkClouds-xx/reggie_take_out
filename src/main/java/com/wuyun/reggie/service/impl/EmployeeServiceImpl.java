package com.wuyun.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyun.reggie.entity.Employee;
import com.wuyun.reggie.mapper.EmployeeMapper;
import com.wuyun.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * Author：wy
 * Date：2023/4/11
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{

}
