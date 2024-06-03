package com.george.dto;

import lombok.Data;

/**
 * @author kdyzm
 */
@Data
public class UserDetailsExpand {

    private String username;

    //userId
    private Integer id;

    //手机号
    private String mobile;

    private String fullname;
}
