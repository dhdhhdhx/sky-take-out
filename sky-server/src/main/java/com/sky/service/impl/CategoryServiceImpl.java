package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;

import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper cateGoryMapper;


    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {

        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());

        Page<Category> page = cateGoryMapper.pageQuery(categoryPageQueryDTO);

        long total = page.getTotal();
        List<Category> records = page.getResult();

        return new PageResult(total,records);
    }

    /**
     * 新增分类
     * @param category
     */
    @Override
    public Boolean save(Category category) {

        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        category.setCreateTime(now);
//        category.setUpdateTime(now);

        category.setCreateUser(currentId);
//        category.setUpdateUser(currentId);

        category.setStatus(1);

        return cateGoryMapper.save(category);
    }

    @Override
    public Boolean deleteById(Long id) {
        return cateGoryMapper.deleteById(id);
    }

    /**
     * 修改分类
     * @param categoryDTO
     */
    @Override
    public void update(CategoryDTO categoryDTO) {

        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);

        category.setUpdateUser(BaseContext.getCurrentId());

        cateGoryMapper.update(category);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .status( status)
                .id(id)
                .updateUser(BaseContext.getCurrentId())
                .build();

        cateGoryMapper.update(category);
    }

    @Override
    public List<Category> list(Integer type) {

        return cateGoryMapper.List(type);
    }
}
