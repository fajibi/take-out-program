package com.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.dto.DishDto;
import com.springboot.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品,同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品信息和对应的口味信息
    public  DishDto  getByWithFlavor(Long id);

    public void  updateWithFlavor(DishDto dishDto);
}
