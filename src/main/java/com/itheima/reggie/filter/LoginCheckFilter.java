package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
//检查用户是否完成登录
@Slf4j
@WebFilter(filterName = "LoginnCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static  final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //获取本次请求url
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);
        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login"//移动端登录

        };
        //判断本次请求是否需要处理
        boolean check = check(requestURI, urls);
        if (check) {
            log.info("本次请求：{}不需要处理",requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //判断登录状态
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录,用户id：{}",request.getSession().getId());
            Long empid = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empid);
            filterChain.doFilter(request, response);
            return;
        }
        //判断移动端登录状态
        if(request.getSession().getAttribute("user")!=null){
            log.info("用户已登录,用户id：{}",request.getSession().getId());
            Long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }



        log.info("用户未登录");
        //如果未登录，通过输出流方式项向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return ;

    }

    //路径匹配
    public boolean check(String requestURI,String[] urls) {
        for (String url : urls) {
            if (antPathMatcher.match(url, requestURI)) {
                return true;
            }
        }
        return false;

    }

}
