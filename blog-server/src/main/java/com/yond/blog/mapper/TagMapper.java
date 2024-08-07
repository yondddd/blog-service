package com.yond.blog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.yond.blog.entity.TagDO;
import com.yond.blog.web.blog.view.vo.TagBlogCount;

import java.util.List;

/**
 * @Description: 博客标签持久层接口
 * @Author: Naccl
 * @Date: 2020-07-30
 */
@Mapper
@Repository
public interface TagMapper {
	List<TagDO> getTagList();

	List<TagDO> getTagListNotId();

	List<TagDO> getTagListByBlogId(Long blogId);

	int saveTag(TagDO tag);

	TagDO getTagById(Long id);

	TagDO getTagByName(String name);

	int deleteTagById(Long id);

	int updateTag(TagDO tag);

	List<TagBlogCount> getTagBlogCount();
}
