package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //新增分类
    @PostMapping
    public R<String> addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }
    //分页查询
    @GetMapping("/page")
    public R<Page> page(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "5") Integer pageSize) {
        return categoryService.pageGet(page,pageSize);
    }
    //根据id删除分类
    @DeleteMapping
    public R<String> deleteCategory(Long id) {
        log.info("删除种类id：{}",id);
        return categoryService.deleteCategory(id);
    }
    //根据id修改分类
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category) {
        log.info("修改种类id：{}",category.getId());
        categoryService.updateById(category);
        return R.success("修改成功");
    }
    //根据条件查询菜品分类数据
    @GetMapping("/list")
    public R<List<Category>> listCategory(Category category) {
        return categoryService.listCategory(category);
    }
}

