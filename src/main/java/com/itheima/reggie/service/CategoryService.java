package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    R<String> addCategory(Category category);

    R<Page> pageGet(Integer page, Integer pageSize);

    R<String> deleteCategory(Long id);

    R<List<Category>> listCategory(Category category);
}
