package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {

    R<ShoppingCart> addShoppingCart(ShoppingCart shoppingCart);

    R<String> sub(ShoppingCart shoppingCart);
}
