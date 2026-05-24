package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    R<String> addWithDish(SetmealDto setmealDto);

    R<String> removeWithDish(List<Long> ids);

    R<String> updateStatus(int status, List<Long> ids);

    R<List<SetmealDto>> listWithDish(Long categoryId, int status);
}
