package com.yond.blog.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 登录日志
 * @Author: Naccl
 * @Date: 2020-12-03
 */
public class LoginLogDO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6801601296035820131L;

    private Long id;
    private String username;//用户名称
    private String ip;//ip
    private String ipSource;//ip来源
    private String os;//操作系统
    private String browser;//浏览器
    private Boolean status;//登录状态
    private String description;//操作信息
    private Date createTime;//操作时间
    private String userAgent;

    public LoginLogDO(String username, String ip, boolean status, String description, String userAgent) {
        this.username = username;
        this.ip = ip;
        this.status = status;
        this.description = description;
        this.createTime = new Date();
        this.userAgent = userAgent;
    }

    public LoginLogDO() {
    }

    public Long getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getIp() {
        return this.ip;
    }

    public String getIpSource() {
        return this.ipSource;
    }

    public String getOs() {
        return this.os;
    }

    public String getBrowser() {
        return this.browser;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public String getDescription() {
        return this.description;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setIpSource(String ipSource) {
        this.ipSource = ipSource;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String toString() {
        return "LoginLogDO(id=" + this.getId() + ", username=" + this.getUsername() + ", ip=" + this.getIp() + ", ipSource=" + this.getIpSource() + ", os=" + this.getOs() + ", browser=" + this.getBrowser() + ", status=" + this.getStatus() + ", description=" + this.getDescription() + ", createTime=" + this.getCreateTime() + ", userAgent=" + this.getUserAgent() + ")";
    }
}