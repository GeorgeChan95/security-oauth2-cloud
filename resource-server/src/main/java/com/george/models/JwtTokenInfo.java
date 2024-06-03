package com.george.models;

import com.george.dto.UserDetailsExpand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author kdyzm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenInfo {

    private List<String> aud;
    private String user_name;
    private List<String> scope;
    private Date exp;
    private List<String> authorities;
    private String jti;
    private String client_id;
    //用户信息扩展
    private UserDetailsExpand user_info;
}
