package com.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
