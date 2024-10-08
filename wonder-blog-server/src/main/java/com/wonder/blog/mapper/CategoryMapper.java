package com.wonder.blog.mapper;

import com.wonder.blog.entity.CategoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CategoryMapper {

    List<CategoryDO> listAll();

    int insertSelective(CategoryDO record);

    int updateSelective(CategoryDO record);

    int deleteById(Long id);

}
