package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;


public interface DishService {
    /**
     * 新增菜品与口味
     */
    void saveWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(List<Long> ids);


    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);
    /**
     * 修改菜品信息及口味
     * @param dishDTO
     * @return
     */
    void updateWithFlavor(DishDTO dishDTO);
    /**
     * 菜品起售与停售
     * @param id
     * @param status
     * @return
     */
    void updateStatus(Long id, Integer status);
}
