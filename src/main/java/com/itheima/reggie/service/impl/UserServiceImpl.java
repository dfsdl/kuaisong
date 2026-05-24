package com.itheima.reggie.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public R<String> login(Map map, HttpServletRequest request) {
        //获取手机号
        String phone =(String) map.get("phone");
        //获取验证码
        String code = (String)map.get("code");
        //获取session中的验证码
        String rcode =(String) request.getSession().getAttribute(phone);
        //判断是否是新用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User one = getOne(queryWrapper);
        if (one == null) {
            User newUser = new User();
            newUser.setPhone(phone);
            newUser.setStatus(1);
            save(newUser);
            if (code.equals(rcode)) {
                request.getSession().setAttribute("user", newUser.getId());
                return R.success("登录成功");
            }
        }
        if (code.equals(rcode)) {
            request.getSession().setAttribute("user",one.getId() );
            return R.success("登录成功");
        }
        return R.error("验证码错误");
    }

    @Override
    public R<String> sendMsg(User user, HttpServletRequest request) {
        //获取电话号
        String phone = user.getPhone();
        log.info("手机号：{}",phone);
        if (!StringUtils.isEmpty(phone)) {
            //生成随机的四位验证码
            Random random = new Random();
            int code = random.nextInt(9000)+1000;
            String codeStr = String.valueOf(code);
            log.info("code:{}", codeStr);
            //将验证码保存到session中
            request.getSession().setAttribute(phone, codeStr);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }
}
