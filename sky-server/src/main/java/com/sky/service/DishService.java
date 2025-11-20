package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品
     * @param dishDTO
     */
    void save(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    DishVO getById(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void updateById(DishDTO dishDTO);

    /**
     * 修改菜品起售停售状态
     * @param status
     * @param id
     */
    void changeStatus(Integer status, Long id);

    /**
     * 根据分类id或名称查询菜品
     * @param categoryId
     * @param name
     * @return
     */
    List<Dish> queryByCategoryIdOrName(Integer categoryId, String name);
}
