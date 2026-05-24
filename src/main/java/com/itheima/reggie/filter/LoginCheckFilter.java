package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//检查用户是否已经完成登录
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取请求uri
        String requestURI = request.getRequestURI();
        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/frontend/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //判断是否需要处理
        if (check(urls,requestURI)) {
            log.info("本次请求不需要处理");
            filterChain.doFilter(request,response);
            return;
        }
        //判断登录状态
        Object employee = request.getSession().getAttribute("employee");
        if (employee != null) {
            long empId = (long) request.getSession().getAttribute("employee");
            //已登录
            log.info("用户已登录，id：{}",empId);
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //判断移动端登录状态
        Object user = request.getSession().getAttribute("user");
        if (user != null) {
            long userId = (long) request.getSession().getAttribute("user");
            //已登录
            log.info("用户已登录，id：{}",userId);
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        /// 未登录，通过输出流向客户端响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


    }


    //路径匹配
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }
        return false;

    }
}
