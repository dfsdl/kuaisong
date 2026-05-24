package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;
    @Override
    public R<String> addCategory(Category category) {
        save(category);
        return R.success("添加分类成功");
    }

    @Override
    public R<Page> pageGet(Integer page, Integer pageSize) {
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        Page page1 = page(pageInfo,queryWrapper);
        return R.success(page1);

    }

    @Override
    public R<String> deleteCategory(Long id) {

        //查询当前分类是否关联了套餐
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,id);
         long count= setmealService.count(queryWrapper);
        if(count>0){
            //已经关联套餐，抛出异常
            throw  new CustomException("当前分类关联了套餐，无法删除");
        }
        //查询当前分类是否关联了菜品
        LambdaQueryWrapper<Dish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Dish::getCategoryId,id);
        long count1= dishService.count(queryWrapper1);
        if(count1>0){
            //已经关联菜品，抛出异常
            throw  new CustomException("当前分类关联了套餐，无法删除");
        }
        removeById(id);
        return R.success("删除成功");
    }

    @Override
    public R<List<Category>> listCategory(Category category) {
        log.info(category.toString());
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = list(queryWrapper);
        return R.success(list);
    }
}

