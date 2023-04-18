package com.wuyun.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.wuyun.reggie.common.BaseContext;
import com.wuyun.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author：wy
 * Date：2023/4/11'
 * 检查用户是否已经登录
 */
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //A. 获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}" , requestURI);
        //不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/sendMsg"
        };

        //B. 判断本次请求, 是否需要登录, 才可以访问
        boolean check = check(urls, requestURI);

        //C. 如果不需要，则直接放行
        if (check) {
            log.info("本次请求{}不需要处理" , requestURI);

            //放行
            filterChain.doFilter(request,response);
            return;
        }

        //D-1. 判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null){
            Long empId = (Long) request.getSession().getAttribute("employee");
            log.info("用户已经登录，id为 {}" ,empId);
            //将用户id传入线程存储
            BaseContext.setCurrentId(empId) ;
            //放行
            filterChain.doFilter(request,response);
            return;
        }

        //D-2. 判断移动端登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user") != null){
            Long userId = (Long) request.getSession().getAttribute("user");
            log.info("用户已经登录，id为 {}" ,userId);
            //将用户id传入线程存储
            BaseContext.setCurrentId(userId) ;
            //放行
            filterChain.doFilter(request,response);
            return;
        }

        //E. 如果未登录, 则返回未登录结果
        //通过输出流的方式向客户端页面返回数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，判断本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
