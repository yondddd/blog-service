package com.yond.blog.entity;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description: 关于我
 * @Author: Naccl
 * @Date: 2020-08-31
 */
public class AboutDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7160388998935748571L;

    private Long id;
    private String nameEn;
    private String nameZh;
    private String value;

    public AboutDO() {
    }

    public Long getId() {
        return this.id;
    }

    public String getNameEn() {
        return this.nameEn;
    }

    public String getNameZh() {
        return this.nameZh;
    }

    public String getValue() {
        return this.value;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return "AboutDO(id=" + this.getId() + ", nameEn=" + this.getNameEn() + ", nameZh=" + this.getNameZh() + ", value=" + this.getValue() + ")";
    }

}
