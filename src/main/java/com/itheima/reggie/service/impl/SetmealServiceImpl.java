package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;

import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;

import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    public R<String> addWithDish(SetmealDto setmealDto) {
        //向数据库加入套餐基本数据
        save(setmealDto);
        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(item->{
            item.setSetmealId(setmealId);

            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
        return R.success("添加成功");
    }

    @Override
    public R<String> removeWithDish(List<Long> ids) {
        //查询套餐状态看是否能删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        long count = count(queryWrapper);
        if (count > 0) {
            throw new CustomException("套餐正在售卖不能删除");
        }
        removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper1);
        return R.success("删除成功");
    }

    @Override
    public R<String> updateStatus(int status, List<Long> ids) {
        List<Setmeal> setmeals = listByIds(ids);
        setmeals.forEach(item->{
            item.setStatus(status);
        });
        updateBatchById(setmeals);
        return R.success("停售成功");
    }

    @Override
    public R<List<SetmealDto>> listWithDish(Long categoryId, int status) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(Setmeal::getStatus,status);
        List<Setmeal> setmeals = list(queryWrapper);
        List<SetmealDto> list1 = setmeals.stream().map(item -> {
            Long setmealId = item.getId();
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(SetmealDish::getSetmealId, setmealId);
            List<SetmealDish> list = setmealDishService.list(queryWrapper1);
            setmealDto.setSetmealDishes(list);
            return setmealDto;
        }).collect(Collectors.toList());
        return R.success(list1);
    }


}
