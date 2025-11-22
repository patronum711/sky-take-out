package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId") //key: setmealCache::100
    public Result<List<Setmeal>> listByCategoryId(Long categoryId){
        log.info("根据分类id查询套餐，id：{}", categoryId);

        // 只查询起售的套餐
        List<Setmeal> list = setmealService.listByCategoryId(categoryId, StatusConstant.ENABLE);
        return Result.success(list);
    }

    /**
     * 查询套餐内包含的菜品
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("查询套餐内包含的菜品")
    public Result<List<DishItemVO>> getDishesById(@PathVariable Long id){
        log.info("查询套餐内包含的菜品");
        List<DishItemVO> dishes = setmealService.getDishesById(id);
        return Result.success(dishes);
    }
}
