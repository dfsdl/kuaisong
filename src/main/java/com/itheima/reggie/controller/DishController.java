package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;

import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//菜品管理
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("save dishDto:{}", dishDto);
        return dishService.saveWithFlavor(dishDto);
    }

    //菜品信息分类
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String name1 = category.getName();
                dishDto.setCategoryName(name1);
            }

            return dishDto;

        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }
    //根据id查询菜品信息和口味信息
    @GetMapping("{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getWithFlavor(id);
        return R.success(dishDto);
    }
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("update dishDto:{}", dishDto);
        return dishService.updateWithFlavor(dishDto);
    }

//    @GetMapping("/list")
//    public R<List<Dish>> list(@RequestParam Long categoryId) {
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Dish::getCategoryId, categoryId);
//        //起售状态
//        queryWrapper.eq(Dish::getStatus,1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }
@GetMapping("/list")
public R<List<DishDto>> list(@RequestParam Long categoryId) {
   return dishService.listWithFlavors(categoryId);


}

}
