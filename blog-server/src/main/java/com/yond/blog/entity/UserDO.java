package com.yond.blog.entity;

import java.io.Serial;
import java.util.Date;

/**
 * @Description: 用户实体类
 * @Author: Naccl
 * @Date: 2020-07-19
 */

public class UserDO {
    @Serial
    private static final long serialVersionUID = 7431951624460448915L;
    private Long id;
    private String guid;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    private Date createTime;
    private Date updateTime;
    private String role;


    public UserDO() {
    }

    public Long getId() {
        return this.id;
    }

    public String getGuid() {
        return this.guid;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public String getEmail() {
        return this.email;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public String getRole() {
        return this.role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String toString() {
        return "UserDO(id=" + this.getId() + ", guid=" + this.getGuid() + ", username=" + this.getUsername() + ", password=" + this.getPassword() + ", nickname=" + this.getNickname() + ", avatar=" + this.getAvatar() + ", email=" + this.getEmail() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ", role=" + this.getRole() + ")";
    }
}
