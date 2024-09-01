package com.yond.blog.web.blog.admin.req;

import com.yond.common.req.PageReq;

import java.io.Serial;

/**
 * @author yond
 * @date 9/1/2024
 * @description
 */
public class CommentPageReq extends PageReq {
    @Serial
    private static final long serialVersionUID = -2588463862115421435L;

    private Integer page;
    private Long blogId;

    public Integer getPage() {
        return page;
    }

    public CommentPageReq setPage(Integer page) {
        this.page = page;
        return this;
    }

    public Long getBlogId() {
        return blogId;
    }

    public CommentPageReq setBlogId(Long blogId) {
        this.blogId = blogId;
        return this;
    }

    @Override
    public String toString() {
        return "CommentPageReq{" +
                "page=" + page +
                ", blogId=" + blogId +
                '}';
    }
}
