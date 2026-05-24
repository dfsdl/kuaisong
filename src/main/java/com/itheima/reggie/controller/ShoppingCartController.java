package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")

public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    //添加购物车
    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart){
        log.info("shoppingCart:{}",shoppingCart);
        return shoppingCartService.addShoppingCart(shoppingCart);
    }
    //查看购物车
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        return R.success(shoppingCarts);
    }
    //减少菜品数量
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        return shoppingCartService.sub(shoppingCart);
    }
    //清空购物车
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(queryWrapper);
        return R.success("删除成功");
    }


}
