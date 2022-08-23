package com.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.entity.Orders;

public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param order
     */
    public void submit(Orders orders);
}
