package com.yond.blog.util.jwt;

import com.yond.blog.entity.UserDO;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author yond
 * @date 2023/10/21
 * @description token
 */
public class TokenVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8406474009218062910L;

    private String token;
    private UserDO user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDO getUser() {
        return user;
    }

    public void setUser(UserDO user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "TokenVO{" +
                "token='" + token + '\'' +
                ", user=" + user +
                '}';
    }
}
