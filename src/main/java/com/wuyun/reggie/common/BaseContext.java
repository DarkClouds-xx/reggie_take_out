package com.wuyun.reggie.common;

/**
 * Author：wy
 * Date：2023/4/12
 * 基于threadLocal封装的工具类，用于保存和获取当前登录用户的id
 */

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
