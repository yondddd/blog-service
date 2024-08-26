package com.yond.blog.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.yond.blog.entity.BlogTagDO;

/**
* @author yond
* @description 针对表【blog_tag】的数据库操作Mapper
* @createDate 2024-08-26 16:59:00
* @Entity com.yond.blog.entity.BlogTag
*/
public interface BlogTagMapper {
    
    List<BlogTagDO> listAll();
    
    int deleteByPrimaryKey(Long id);

    int insert(BlogTagDO record);

    int insertSelective(BlogTagDO record);

    BlogTagDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BlogTagDO record);

    int updateByPrimaryKey(BlogTagDO record);
    
    int deleteByIds(@Param("ids") List<Long> ids);
}
