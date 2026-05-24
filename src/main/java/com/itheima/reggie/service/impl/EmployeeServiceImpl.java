package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>  implements EmployeeService {


    @Override
    public R<Employee> login(HttpServletRequest request, Employee employee) {
        //根据用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee one = getOne(queryWrapper);
        if(one==null){
            //用户不存在
            return R.error("用户不存在");
        }
        //将页面提交密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if(!password.equals(one.getPassword())){
            //密码错误
            return R.error("密码错误");
        }
        if (one.getStatus()==0){
            return R.error("员工账户被禁用");
        }
        //将员工id存入session
        request.getSession().setAttribute("employee",one.getId());
        return R.success(one);
    }

    @Override
    public R<String> logout(HttpServletRequest request) {
        //清理session中员工信息
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    @Override
    public void saveEmployee(HttpServletRequest request,Employee employee) {
        LocalDateTime now = LocalDateTime.now();
        //employee.setCreateTime(now);
        //employee.setUpdateTime(now);
        //获取当前用户id
        Long userid =(long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(userid);
        //employee.setUpdateUser(userid);
        //加密密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        save(employee);
    }

    @Override
    public R<Page> getpage(int page, int pageSize, String name) {
        //构造分页条件
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @Override
    public R<String> updateEmployee(HttpServletRequest request,Employee employee) {

        long id  = Thread.currentThread().getId();
        log.info("线程id：{}",id);
        employee.setUpdateTime(LocalDateTime.now());
        Long userId = (long)request.getSession().getAttribute("employee");
        employee.setUpdateUser(userId);
        updateById(employee);
        return R.success("员工信息修改成功");

    }

}
