package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;

import javax.servlet.http.HttpServletRequest;


public interface EmployeeService extends IService<Employee> {
    R<Employee> login(HttpServletRequest request, Employee employee);

    R<String> logout(HttpServletRequest request);

    void saveEmployee(HttpServletRequest request,Employee employee);

    R<Page> getpage(int page, int pageSize, String name);

    R<String> updateEmployee(HttpServletRequest request,Employee employee);
}

