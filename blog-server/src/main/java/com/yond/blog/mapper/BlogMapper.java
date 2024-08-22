package com.yond.blog.mapper;

import com.yond.blog.entity.BlogDO;
import com.yond.blog.web.blog.view.dto.BlogView;
import com.yond.blog.web.blog.view.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: 博客文章持久层接口
 * @Author: Naccl
 * @Date: 2020-07-26
 */
@Mapper
@Repository
public interface BlogMapper {

    List<BlogDO> listAll();

    Integer updateSelective(BlogDO blogDO);

    List<BlogDO> getIdAndTitleList();

    List<NewBlog> getNewBlogListByIsPublished(@Param("limit") Integer limit);

    List<BlogInfo> getBlogInfoListByIsPublished(@Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    List<BlogInfo> getBlogInfoListByCategoryNameAndIsPublished(@Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize, @Param("categoryName") String categoryName);

    List<BlogInfo> getBlogInfoListByTagNameAndIsPublished(@Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize, @Param("tagName") String tagName);

    List<String> getGroupYearMonthByIsPublished();

    List<ArchiveBlog> getArchiveBlogListByYearMonthAndIsPublished(String yearMonth);

    List<RandomBlog> getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend(Integer limitNum);

    List<BlogView> getBlogViewsList();

    int deleteBlogById(Long id);

    int deleteBlogTagByBlogId(Long blogId);

    int saveBlog(com.yond.blog.web.blog.view.dto.Blog blog);

    int saveBlogTag(Long blogId, Long tagId);

    int updateViews(Long blogId, Integer views);

    BlogDO getBlogById(Long id);

    String getTitleByBlogId(Long id);

    BlogDetail getBlogByIdAndIsPublished(Long id);

    String getBlogPassword(Long blogId);

    int updateBlog(com.yond.blog.web.blog.view.dto.Blog blog);

    int countBlog();

    int countBlogByIsPublished();

    int countBlogByCategoryId(Long categoryId);

    int countBlogByTagId(Long tagId);

    Boolean getCommentEnabledByBlogId(Long blogId);

    Boolean getPublishedByBlogId(Long blogId);

    List<CategoryBlogCount> getCategoryBlogCountList();
}
