package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    /**
     * 添加新套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("添加新套餐")
    public Result insert(@RequestBody SetmealDTO setmealDTO){
        log.info("添加新套餐");
        setmealService.insert(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询");
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> queryById(@PathVariable Long id){
        log.info("根据id查询套餐");
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐");
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result deleteBatch(@RequestParam List<Long> ids){
        log.info("批量删除套餐");
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 起售、停售套餐
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("起售、停售套餐")
    public Result changeStatus(@PathVariable Integer status, @RequestParam Long id){
        log.info("起售、停售套餐");
        setmealService.changeStatus(status, id);
        return Result.success();
    }
}
