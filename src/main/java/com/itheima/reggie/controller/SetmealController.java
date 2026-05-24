package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setMealService;
    @Autowired
    private CategoryService categoryService;
    //新增套餐
    @PostMapping
    public R<String> add(@RequestBody SetmealDto setmealDto) {
        return setMealService.addWithDish(setmealDto);
    }
    //套餐分页查询
    @GetMapping("/page")
    public R<Page> setmealPage(Integer page, Integer pageSize, String name) {
        Page<Setmeal>  setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.like(name!=null,Setmeal::getName,name);
        setmealWrapper.orderByDesc(Setmeal::getUpdateTime);
        setMealService.page(setmealPage,setmealWrapper);
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String name1 = category.getName();
                setmealDto.setCategoryName(name1);
            }
            return setmealDto;

        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        log.info("ids:{}",ids);
        return setMealService.removeWithDish(ids);
    }
    //停售套餐
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,@RequestParam List<Long> ids){
        return setMealService.updateStatus(status,ids);
    }
    //根据分类id查看菜品信息
    @GetMapping("/list")
    public R<List<SetmealDto>> list(@RequestParam Long categoryId,@RequestParam int status) {
        log.info("categoryId:{}",categoryId);
        log.info("status:{}",status);
        return setMealService.listWithDish(categoryId,status);
    }





}
