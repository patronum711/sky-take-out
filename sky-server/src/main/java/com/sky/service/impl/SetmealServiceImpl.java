package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void insert(SetmealDTO setmealDTO) {
        // 1.添加到setmeal表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);

        // 2.添加到setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        if(setmealDishes != null && !setmealDishes.isEmpty()){
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmeal.getId());
                setmealDishMapper.insert(setmealDish);
            }
        }
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.page(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page);
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        // 1.查询setmeal表
        SetmealVO setmealVO = setmealMapper.getById(id);

        // 2.查询setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
       // 1.更新setmeal表
       Setmeal setmeal = new Setmeal();
       BeanUtils.copyProperties(setmealDTO, setmeal);
       setmealMapper.update(setmeal);

       // 2.更新setmeal_dish表（先删除后添加）
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && !setmealDishes.isEmpty()){
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmeal.getId());
                setmealDishMapper.insert(setmealDish);
            }
        }
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        // 1.查询套餐状态，有启售的套餐无法删除
        for(Long id : ids){
            if(Objects.equals(setmealMapper.queryStatusById(id), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        // 2.删除setmeal表以及对应的setmeal_dish表
        for(Long id : ids){
            setmealMapper.deleteById(id);
            setmealDishMapper.deleteBySetmealId(id);
        }
    }

    /**
     * 修改套餐起售停售
     * @param status
     * @param id
     */
    @Override
    public void changeStatus(Integer status, Long id) {
        if(Objects.equals(status, StatusConstant.ENABLE)) {
            // 查询当前套餐包含的菜品是否有已经停售的菜品，如果有，拒绝起售
            List<Long> dishIds = setmealDishMapper.getDishIdsBySetmealId(id);
            if (dishIds != null && !dishIds.isEmpty()) {
                for (Long dishId : dishIds) {
                    if (Objects.equals(dishMapper.getStatusById(dishId), StatusConstant.DISABLE))
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        setmealMapper.update(Setmeal.builder().status(status).id(id).build());
    }
}
