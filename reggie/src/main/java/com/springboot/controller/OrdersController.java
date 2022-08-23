package com.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.R;
import com.springboot.entity.Orders;
import com.springboot.entity.Setmeal;
import com.springboot.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    OrdersService ordersService;

    /**
     * 用户下单
     * @param order
     * @return
     */
    @PostMapping("/submit")
   public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
       ordersService.submit(orders);
       return R.success("下单成功");
   }
}
