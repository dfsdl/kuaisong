package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    //登录
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
       return employeeService.login(request,employee);
    }
    //退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        return employeeService.logout(request);
    }
    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeService.saveEmployee(request,employee);
        return R.success("新增成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        return employeeService.getpage(page,pageSize,name);

    }

    //根据id修改员工信息
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        return employeeService.updateEmployee(request,employee);
    }

    //根据id查询员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }


}
