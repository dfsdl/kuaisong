package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {


    R<String> submit(Orders orders);
}
