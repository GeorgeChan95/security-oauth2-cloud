package com.george.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author George
 */
@Data
@TableName("t_user")
public class TUser {

    private Integer id;

    private String username;

    private String password;

    private String fullname;

    private String mobile;
}
