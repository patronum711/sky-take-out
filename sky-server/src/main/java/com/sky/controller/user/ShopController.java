package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("userShopController")
@RequestMapping("/user/shop")
public class ShopController {
    @Autowired
    RedisTemplate redisTemplate;

    public static final String KEY = "SHOP_STATUS";

    /**
     * 查询店铺营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> getStatus(){
        log.info("查询营业状态");
        return Result.success((Integer) redisTemplate.opsForValue().get("KEY"));
    }
}
