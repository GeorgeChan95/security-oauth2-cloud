package com.george.enhancer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 *
 * @author George
 * @date 2024.06.03 14:40
 */
@Slf4j
@Component
public class CustomTokenEnhancer implements TokenEnhancer {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String,Object> additionalInfo = new HashMap<>();
        Object principal = authentication.getPrincipal();
        try {
            String s = objectMapper.writeValueAsString(principal);
            Map map = objectMapper.readValue(s, Map.class);
            // 若干个不想要的字段属性
            map.remove("password");
            map.remove("authorities");
            map.remove("accountNonExpired");
            map.remove("accountNonLocked");
            map.remove("credentialsNonExpired");
            map.remove("enabled");
            // 添加用户信息到Token中
            additionalInfo.put("user_info", map);
            // 添加属性信息到AdditonInfo中，用于Token的增强
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        } catch (IOException e) {
            log.error("自定义token增强异常，", e);
        }
        return accessToken;
    }
}
