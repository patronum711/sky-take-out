package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询套餐id
     * @param id
     * @return
     */
    @Select("select setmeal_id from setmeal_dish where dish_id = #{id}")
    List<Long> getSetmealIdsByDishId(Long id);

    /**
     * 插入套餐和菜品关系
     * @param setmealDish
     */
    @Insert("insert into setmeal_dish (setmeal_id, dish_id, name, price, copies) " +
            "values (#{setmealId}, #{dishId}, #{name}, #{price}, #{copies})")
    void insert(SetmealDish setmealDish);

    /**
     * 根据套餐id查询套餐和菜品关系条目
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    /**
     * 根据套餐id删除套餐和菜品关系
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    /**
     * 根据套餐id查询菜品id
     * @param id
     * @return
     */
    @Select("select dish_id from setmeal_dish where setmeal_id = #{id}")
    List<Long> getDishIdsBySetmealId(Long id);
}
