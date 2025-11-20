package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/shop")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String KEY = "SHOP_STATUS";

    /**
     * 设置营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置营业状态：{}", status);
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    /**
     * 查询营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("查询营业状态")
    public Result<Integer> getStatus(){
        log.info("查询营业状态");
        return Result.success((Integer) redisTemplate.opsForValue().get(KEY));
    }
}
