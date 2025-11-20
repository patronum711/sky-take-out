package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SetmealMapper {

    /**
     * 修改套餐信息
     * @param setmeal
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 新增套餐
     * @param setmeal
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐（多表查询category_id对应的category_name）
     * @param id
     * @return
     */
    @Select("select s.*, c.name category_name from setmeal s left outer join category c " +
            "on s.category_id = c.id " +
            "where s.id = #{id}")
    SetmealVO getById(Long id);

    /**
     * 根据id查询套餐状态
     * @param id
     * @return
     */
    @Select("select status from setmeal where id = #{id}")
    Integer queryStatusById(Long id);

    /**
     * 删除套餐
     * @param id
     */
    @Delete("delete from setmeal where id = #{id}")
    void deleteById(Long id);
}
