package com.george.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.george.dto.UserDetailsExpand;
import com.george.entity.TUser;
import com.george.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * 自定义查询用户接口
 * 关于自定义的 MyUserDetailsServiceImpl何时会被调用：https://blog.csdn.net/weixin_46107120/article/details/137699337
 * @author kdyzm
 */
@Service
@Slf4j
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        TUser tUser = userMapper.selectOne(new LambdaQueryWrapper<TUser>().eq(TUser::getUsername, username));
        if (Objects.isNull(tUser)) {
            throw new UsernameNotFoundException(username + "账号不存在");
        }
        List<String> allPermissions = userMapper.findAllPermissions(tUser.getId());
        String[] array = null;
        if (CollectionUtils.isEmpty(allPermissions)) {
            log.warn("{} 无任何权限", tUser.getUsername());
            array = new String[]{};
        } else {
            array = new String[allPermissions.size()];
            allPermissions.toArray(array);
        }

        // 自定义token信息扩张
        UserDetailsExpand userDetailsExpand = new UserDetailsExpand(tUser.getUsername(), tUser.getPassword(), AuthorityUtils.createAuthorityList(array));
        userDetailsExpand.setId(tUser.getId());
        userDetailsExpand.setMobile(tUser.getMobile());
        userDetailsExpand.setFullname(tUser.getFullname());
        return userDetailsExpand;
    }
}
