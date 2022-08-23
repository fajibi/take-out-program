package com.springboot.dto;


import com.springboot.entity.Setmeal;
import com.springboot.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
