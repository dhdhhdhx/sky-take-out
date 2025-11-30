package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DishServiceImpl  implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishService dishService;

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new  PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 新增菜品与口味
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        log.info("参数：{}",dishDTO);
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

//        向菜品表插入1条数据
        dishMapper.insert(dish);

//        获取insert语句生成的主键值
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();

        flavors.forEach(fs ->{fs.setDishId(dishId);});

        if(flavors != null && flavors.size() >0) {
//        向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public void deleteBatch(List<Long> ids) {
//        判断当前菜品是否能够被删除--> 是否存在起售中的菜品？
        ids.forEach(id -> {
            Dish dish = dishMapper.getById(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)){
//                当前菜品出于起售中不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
//        判断当前菜品是否能够被删除--> 是否被套餐关联了？
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && !setmealIds.isEmpty()){
            throw  new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

//       删除菜品中的菜品数据
        dishMapper.delete(ids);
//        删除菜品关联的口味数据
        dishFlavorMapper.delete(ids);

    }
    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
//        查询菜品表
        Dish dish = dishMapper.getById(id);
//        查询口味表
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
//        封装数据到VO中
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);

        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 修改菜品信息及口味
     * @param dishDTO
     * @return
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
//        修改菜品表基本信息
        dishMapper.update(dish);
//        删除原有口味信息
        List<Long> ids = new ArrayList<>();
        Long dishId = dishDTO.getId();
        ids.add(dishId);
        dishFlavorMapper.delete(ids);
//        修改口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(f -> f.setDishId(dishId));
        if(!flavors.isEmpty()){
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品起售与停售
     * @param id
     * @param status
     * @return
     */
    @Override
    @AutoFill(value = OperationType.INSERT)
    public void updateStatus(Long id, Integer status) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();

        dishMapper.update(dish);
    }
}
