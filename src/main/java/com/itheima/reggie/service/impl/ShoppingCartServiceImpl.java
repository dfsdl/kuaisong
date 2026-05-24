package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Override
    public R<ShoppingCart> addShoppingCart(ShoppingCart shoppingCart) {
        //设置用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //判断添加的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {
            //添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加的是套餐
            Long setmealId = shoppingCart.getSetmealId();
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }
        //查询当前菜品或套餐是否在购物车中
        ShoppingCart one = getOne(queryWrapper);
        if (one != null) {
            //已经存在，数量加1
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            one.setCreateTime(LocalDateTime.now());
            updateById(one);
        } else {
            //不存在，添加到购物车，数量为1
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            saveOrUpdate(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

    @Override
    public R<String> sub(ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            //减少的是菜品
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            ShoppingCart one = getOne(queryWrapper);
            if (one == null) {
                throw new CustomException("没有该菜品");
            }
            Integer number = one.getNumber();
            if (number > 1) {
                one.setNumber(number - 1);
                updateById(one);
            } else {
                removeById(one.getId());
            }
            return R.success("删除成功");
        } else {
            //减少的是套餐
            Long setmealId = shoppingCart.getSetmealId();
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
            queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            ShoppingCart one = getOne(queryWrapper);
            if (one == null) {
                throw new CustomException("没有该套餐");
            }
            Integer number = one.getNumber();
            if (number > 1) {
                one.setNumber(number - 1);
                updateById(one);
            } else {
                removeById(one.getId());
            }
            return R.success("删除成功");
        }
    }
}
