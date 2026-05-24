package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    R<String> saveWithFlavor(DishDto dishDto);

    public DishDto getWithFlavor(Long id);

    R<String> updateWithFlavor(DishDto dishDto);

    R<List<DishDto>> listWithFlavors(Dish  dish);
}
