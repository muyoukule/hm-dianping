package com.hmdp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RedisData {
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    /**
     * 存入的数据
     */
    private Object data;
}