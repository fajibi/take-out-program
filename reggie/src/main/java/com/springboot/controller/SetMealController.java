package com.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.R;
import com.springboot.dto.DishDto;
import com.springboot.dto.SetmealDto;
import com.springboot.entity.Category;
import com.springboot.entity.Dish;
import com.springboot.entity.DishFlavor;
import com.springboot.entity.Setmeal;
import com.springboot.service.CategoryService;
import com.springboot.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetMealController {

    @Autowired
    SetMealService setMealService;

    @Autowired
    CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);
        //构造分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> SetMealPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setMealService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, SetMealPage, "records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }


            return setmealDto;
        }).collect(Collectors.toList());


        SetMealPage.setRecords(list);

        return R.success(SetMealPage);
    }


    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> insert(@RequestBody SetmealDto setmealDto) {

        setMealService.saveWithSetMealDish(setmealDto);
        return R.success("新增菜品成功");
    }

    /**
     * 根据id查询套餐信息和对应的口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {

        SetmealDto byWithSetMealDish = setMealService.getByWithSetMealDish(id);
        return R.success(byWithSetMealDish);
    }

    /**
     * 删除套餐
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> id) {

        setMealService.remove(id);
        return R.success("套餐数据删除成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list( Setmeal setmeal) {
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setMealService.list(queryWrapper);


        return R.success(setmealList);
    }
}

