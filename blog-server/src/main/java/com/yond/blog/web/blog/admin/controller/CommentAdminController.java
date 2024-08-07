package com.yond.blog.web.blog.admin.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yond.blog.entity.BlogDO;
import com.yond.blog.entity.CommentDO;
import com.yond.blog.service.BlogService;
import com.yond.blog.service.CommentService;
import com.yond.common.annotation.OperationLogger;
import com.yond.common.resp.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 博客评论后台管理
 * @Author: Naccl
 * @Date: 2020-08-03
 */
@RestController
@RequestMapping("/admin")
public class CommentAdminController {
    @Autowired
    CommentService commentService;
    @Autowired
    BlogService blogService;

    /**
     * 按页面和博客id分页查询评论List
     *
     * @param page     要查询的页面(博客文章or关于我...)
     * @param blogId   如果是博客文章页面 需要提供博客id
     * @param pageNum  页码
     * @param pageSize 每页个数
     * @return
     */
    @GetMapping("/comments")
    public Response<PageInfo<CommentDO>> comments(@RequestParam(defaultValue = "") Integer page,
                                                  @RequestParam(defaultValue = "") Long blogId,
                                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        String orderBy = "create_time desc";
        PageHelper.startPage(pageNum, pageSize, orderBy);
        List<CommentDO> comments = commentService.getListByPageAndParentCommentId(page, blogId, -1L);
        PageInfo<CommentDO> pageInfo = new PageInfo<>(comments);
        return Response.success(pageInfo);
    }

    /**
     * 获取所有博客id和title 供评论分类的选择
     *
     * @return
     */
    @GetMapping("/blogIdAndTitle")
    public Response<List<BlogDO>> blogIdAndTitle() {
        List<BlogDO> blogs = blogService.getIdAndTitleList();
        return Response.success(blogs);
    }

    /**
     * 更新评论公开状态
     *
     * @param id        评论id
     * @param published 是否公开
     * @return
     */
    @OperationLogger("更新评论公开状态")
    @PutMapping("/comment/published")
    public Response<Boolean> updatePublished(@RequestParam Long id, @RequestParam Boolean published) {
        commentService.updateCommentPublishedById(id, published);
        return Response.success();
    }

    /**
     * 更新评论接收邮件提醒状态
     *
     * @param id     评论id
     * @param notice 是否接收提醒
     * @return
     */
    @OperationLogger("更新评论邮件提醒状态")
    @PutMapping("/comment/notice")
    public Response<Boolean> updateNotice(@RequestParam Long id, @RequestParam Boolean notice) {
        commentService.updateCommentNoticeById(id, notice);
        return Response.success();
    }

    /**
     * 按id删除该评论及其所有子评论
     *
     * @param id 评论id
     * @return
     */
    @OperationLogger("删除评论")
    @DeleteMapping("/comment")
    public Response<Boolean> delete(@RequestParam Long id) {
        commentService.deleteCommentById(id);
        return Response.success();
    }

    /**
     * 修改评论
     *
     * @param comment 评论实体
     * @return
     */
    @OperationLogger("修改评论")
    @PutMapping("/comment")
    public Response<Boolean> updateComment(@RequestBody CommentDO comment) {
        if (StringUtils.isBlank(comment.getNickname())
                || StringUtils.isBlank(comment.getAvatar()) || StringUtils.isBlank(comment.getEmail()) ||
                StringUtils.isBlank(comment.getIp()) || StringUtils.isBlank(comment.getContent())) {
            return Response.<Boolean>custom().setFailure("参数有误");
        }
        commentService.updateComment(comment);
        return Response.success();
    }
}
