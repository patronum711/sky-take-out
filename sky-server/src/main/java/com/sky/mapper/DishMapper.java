package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    /**
     * 新增菜品数据
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品售卖状态
     * @param id
     * @return
     */
    @Select("select status from dish where id = #{id}")
    Integer getStatusById(Long id);

    /**
     * 批量删除菜品
     * @param ids
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id查询菜品（包含分类名字）
     * @param id
     * @return
     */
    DishVO getById(Long id);

    /**
     * 修改菜品数据
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void updateByDishId(Dish dish);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> listByCategoryId(Integer categoryId);

    /**
     * 根据名称查询菜品
     * @param name
     * @return
     */
    @Select("select * from dish where name like concat('%', #{name}, '%')")
    List<Dish> listByName(String name);
}
