package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增菜品和对应的口味数据
     * @param dishDTO
     */
    @Override
    @Transactional
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dish.setStatus(StatusConstant.DISABLE);

        dishMapper.insert(dish); //执行后id回填到dish中

        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if(dishFlavors != null && !dishFlavors.isEmpty()) {
            for(DishFlavor dishFlavor : dishFlavors){
                dishFlavor.setDishId(dish.getId());
            }
            dishFlavorMapper.insertBatch(dishFlavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 1.检查是否有启售的菜品，存在起售的菜品则无法删除
        for(Long id : ids){
            if(Objects.equals(dishMapper.getStatusById(id), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 2.检查是否有套餐管关联的菜品，存在套餐关联的菜品则无法删除
        for(Long id : ids){
            if(setmealDishMapper.getSetmealIdsByDishId(id) != null && !setmealDishMapper.getSetmealIdsByDishId(id).isEmpty()){
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }

        // 3.删除菜品，包含dish和dish_flavor中的删除
        for (Long id : ids){
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        DishVO dishVO = dishMapper.getById(id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void updateById(DishDTO dishDTO) {
        // 1.更新dish表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateByDishId(dish);

        // 2.更新dish_flavor表(先删除后添加)
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
           for(DishFlavor flavor : flavors){
               flavor.setDishId(dishDTO.getId());
           }
           dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 根据id起售停售
     * @param status
     * @param id
     */
    @Override
    public void changeStatus(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.updateByDishId(dish);

        // 如果是停售操作，还需要将包含当前菜品的套餐也停售
        if (status == StatusConstant.DISABLE) {
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishId(id);
            for(Long setmealId : setmealIds){
                Setmeal setmeal = Setmeal.builder()
                        .id(setmealId)
                        .status(StatusConstant.DISABLE)
                        .build();
                setmealMapper.update(setmeal);
            }
        }
    }

    /**
     * 根据分类id或名称查询菜品
     * @param categoryId
     * @param name
     * @return
     */
    @Override
    public List<Dish> queryByCategoryIdOrName(Integer categoryId, String name) {
        if(categoryId!=null)
            return dishMapper.listByCategoryId(categoryId);
        else
            return dishMapper.listByName(name);
    }
}
