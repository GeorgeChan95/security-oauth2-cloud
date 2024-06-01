package com.george.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * <p>
 *     通过Spring Security和Spring Security OAuth2的配置，
 *     使当前应用成为一个OAuth2资源服务器，能够处理令牌的校验，
 *     并根据令牌的权限范围来控制对资源的访问。
 * </p>
 *
 * @author George
 * @date 2024.06.01 11:57
 */
@Configuration
// 启用资源服务器，这个注解会自动配置Spring Security OAuth2，使当前应用成为一个OAuth2资源服务器
@EnableResourceServer
public class ResouceServerConfig extends ResourceServerConfigurerAdapter {
    /**
     * 资源ID，用于标识当前的资源服务器。
     * 这里需要与 数据库表 oauth_client_details.resource_ids一致，否则请求报错
     */
    private static final String RESOURCE_ID= "res1"; //

    /**
     * 依赖注入，ResourceServerTokenServices用于验证令牌
     */
    @Autowired
    private ResourceServerTokenServices resourceServerTokenServices;

    /**
     * 注意配置类 TokenConfig中定义的bean：TokenStore
     */
    @Autowired
    private TokenStore tokenStore;

    /**
     * 定义令牌服务的Bean
     * @return
     */
    @Bean
    public ResourceServerTokenServices resourceServerTokenServices(){
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl("http://127.0.0.1:30000/oauth/check_token"); // 设置令牌校验的端点URL
        remoteTokenServices.setClientId("c1"); // 设置客户端ID
        remoteTokenServices.setClientSecret("secret"); // 设置客户端密钥
        return remoteTokenServices;
    }

    /**
     * 配置资源服务器的安全属性
     * @param resources configurer for the resource server
     * @throws Exception
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                .resourceId(RESOURCE_ID) // 设置资源ID
//                .tokenServices(resourceServerTokenServices) //设置令牌服务，这里使用的是前面定义的
                .tokenStore(tokenStore) // 设置自定义的TokenStore，解析令牌
                .stateless(true); // 设置为无状态，会话不再使用session
    }

    /**
     * 配置HTTP安全
     * @param http the current http filter configuration
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // 这里要满足数据库表：oauth_client_details.scope 字段的定义，否则请求报错
                .antMatchers("/**").access("#oauth2.hasScope('all') or #oauth2.hasScope('ROLE_ADMIN')") // 配置请求授权，匹配所有URL，并要求OAuth2令牌具有all范围（scope）
                .and()
                .csrf().disable() // 禁用CSRF保护，因为我们使用的是JWT令牌而不是表单登录
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER); // 配置会话管理策略为从不创建会话
    }
}
