package com.george.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * <p>
 *     扩展SpringSecurity自带的User对象的属性
 * </p>
 *
 * @author George
 * @date 2024.06.03 14:07
 */
public class UserDetailsExpand extends User {
    public UserDetailsExpand(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    //userId
    private Integer id;

    //手机号
    private String mobile;

    private String fullname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
