package com.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.dto.DishDto;
import com.springboot.dto.SetmealDto;
import com.springboot.entity.Setmeal;
import com.springboot.entity.SetmealDish;

import java.util.List;


public interface SetMealService  extends IService<Setmeal> {
    //新增套餐,同时插入套餐对应的菜品数据，需要操作两张表：setmeal，setmealdish
    public void saveWithSetMealDish(SetmealDto setmealDto);
    //根据id查询菜品信息和对应的口味信息
    public  SetmealDto  getByWithSetMealDish(Long id);

    public void  updateWitSetMealDish(SetmealDto setmealDto);

    public void remove(List<Long> id);
}
