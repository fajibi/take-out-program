package com.springboot.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.common.CustomException;
import com.springboot.dto.DishDto;
import com.springboot.dto.SetmealDto;
import com.springboot.entity.Dish;
import com.springboot.entity.DishFlavor;
import com.springboot.entity.Setmeal;
import com.springboot.entity.SetmealDish;
import com.springboot.mapper.SetMealMapper;
import com.springboot.service.SetMealDishService;
import com.springboot.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {

    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private SetMealService setMealService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithSetMealDish(SetmealDto setmealDto) {
        //保存菜品的基本信息,操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保持菜品口味数据到菜品菜品口味表dish_flavor
        setMealDishService.saveBatch(setmealDishes);
    }

    @Override
    public SetmealDto getByWithSetMealDish(Long id) {
        //查询菜品基本信息，从dish表查询
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        //查询当前菜品对应的口味信息，从dish_flavor
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getDishId,setmeal.getId());
        List<SetmealDish> setmealdish = setMealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealdish);
        return setmealDto;

    }

    @Override
    public void updateWitSetMealDish(SetmealDto setmealDto) {

    }

    /**
     * 根据id删除套餐,删除之间需要进行判断
     * @param id
     */
    @Override
    @Transactional
    public void remove(List<Long> id) {
        //查询套餐状态，确定时候可以删除
        LambdaQueryWrapper<Setmeal> status = new LambdaQueryWrapper<>();
        status.in(Setmeal::getId,id);
        status.eq(Setmeal::getStatus,"1");
        int count = this.count(status);
        if (count > 0){
            //如果不可以，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件,根据套餐id进行查询
        queryWrapper.eq(SetmealDish::getSetmealId,id);
         setMealDishService.remove(queryWrapper);

        super.removeByIds(id);
    }
    }







