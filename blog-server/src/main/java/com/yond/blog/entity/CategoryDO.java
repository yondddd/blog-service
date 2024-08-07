package com.yond.blog.entity;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 博客分类
 * @Author: Naccl
 * @Date: 2020-07-26
 */
public class CategoryDO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2214374622963598886L;

    /**
     * 分类id
     */
    private Long id;
    /**
     * 分类名称
     */
    private String name;

    public CategoryDO() {
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "CategoryDO(id=" + this.getId() + ", name=" + this.getName() + ")";
    }
}
