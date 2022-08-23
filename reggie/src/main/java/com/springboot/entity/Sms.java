package com.springboot.entity;

import lombok.Data;

@Data
public class Sms {
        //电话号码
        String phoneNumber;
        //验证码
        Integer code;
        //分钟
        Integer min;

}
