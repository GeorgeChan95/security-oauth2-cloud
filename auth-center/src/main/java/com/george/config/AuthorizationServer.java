package com.george.config;

import com.george.enhancer.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;

/**
 * 解析内容参考：https://blog.kdyzm.cn/post/24
 * @author kdyzm
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 认证管理器，当你选择了资源所有者密码（password）授权类型的时候，请设置这个属性注入一个 AuthenticationManager 对象。
     * 密码验证流程：https://blog.csdn.net/chengqiuming/article/details/103282586
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * token jwt转换工具
     * 用于将OAuth2访问令牌转换为JWT令牌，并进行签名和验证。
     */
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    /**
     * 注入自定义的Token增强器
     */
    @Autowired
    private CustomTokenEnhancer customTokenEnhancer;

    /**
     * 密码编辑器
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置授权服务器（Authorization Server）的Token服务（Token Services）
     * @return
     */
    @Bean
    public AuthorizationServerTokenServices tokenServices(){
        DefaultTokenServices services = new DefaultTokenServices();
        // 设置客户端详情服务
        services.setClientDetailsService(clientDetailsService);
        // 设置Token服务支持刷新Token。刷新Token是用于在访问令牌（Access Token）过期时获取新的访问令牌的机制
        services.setSupportRefreshToken(true);
        // 设置Token存储, 默认使用基于内存存储
        services.setTokenStore(tokenStore);
        // 设置访问令牌的有效期为7200秒（2小时）
        services.setAccessTokenValiditySeconds(7200);
        // 设置刷新令牌的有效期为259200秒（3天）。
        // 刷新令牌用于获取新的访问令牌，有效期通常比访问令牌长，以确保用户在一段时间内无需重新登录即可获取新的访问令牌
        services.setRefreshTokenValiditySeconds(259200);

        /**
         * 无效的时间配置，token的有效期优先选择数据库中客户端的配置，如需修改，修改表oauth_client_details中的配置
         * @see DefaultTokenServices#getAccessTokenValiditySeconds(org.springframework.security.oauth2.provider.OAuth2Request)
         */
//        services.setAccessTokenValiditySeconds(10);//有效期10秒
//        services.setRefreshTokenValiditySeconds(30);//有效期30秒

        // 设置令牌增强器链, 用于将多个TokenEnhancer组合在一起。
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        // 将自定义的Token增强器添加到Token增强器链中
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(customTokenEnhancer, jwtAccessTokenConverter));
        // 将增强器链注入到DefaultTokenServices中，使其在生成令牌时使用JWT转换器。
        services.setTokenEnhancer(tokenEnhancerChain);
        return services;
    }

    @Bean
    public ClientDetailsService clientDetailsService(DataSource dataSource) {
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        clientDetailsService.setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }

    /**
     * 配置客户端的详细信息，能够使用内存或者JDBC来实现客户端详情服务
     *
     * clientId ：（必须的）用来标识客户的Id。
     * secret ：（需要值得信任的客户端）客户端安全码，如果有的话。
     * scope ：用来限制客户端的访问范围，如果为空（默认）的话，那么客户端拥有全部的访问范围。
     * authorizedGrantTypes ：此客户端可以使用的授权类型，默认为空。
     * authorities ：此客户端可以使用的权限（基于Spring Security authorities）。
     *
     * @param clients ClientDetailsServiceConfigurer ：用来配置客户端详情服务（ClientDetailsService），
     *                客户端详情信息在这里进行初始化，你能够把客户端详情信息写死在这里或者是通过数据库来存储调取详情信息。
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);

//        clients.inMemory()
//                .withClient("c1")
//                .secret(new BCryptPasswordEncoder().encode("secret"))//$2a$10$0uhIO.ADUFv7OQ/kuwsC1.o3JYvnevt5y3qX/ji0AUXs4KYGio3q6
//                .resourceIds("r1")
//                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")
//                .scopes("all")
//                .autoApprove(false)
//                .redirectUris("https://www.baidu.com");
    }

    /**
     * 令牌访问断点配置
     *
     * @param endpoints AuthorizationServerEndpointsConfigurer ：用来配置令牌（token）的访问端点和令牌服务(tokenservices)。
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager) // 设置认证管理器, 密码授权模式需要
                .authorizationCodeServices(authorizationCodeServices) // 这个属性是用来设置授权码服务的（即 AuthorizationCodeServices 的实例对象），主要用于 "authorization_code" 授权码类型模式。
                .tokenServices(tokenServices())
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);

        // 自定义授权方法，
        // 当你设置了这个东西（即 TokenGranter 接口实现），那么授权将会交由你来完全掌控，
        // 并且会忽略掉上面的这几个属性，这个属性一般是用作拓展用途的，即标准的四种授权模式已经满足不了你的需求的时候，才会考虑使用这个。
//        endpoints.tokenGranter(null);

        /**
         * 配置端点URL链接
         * 第一个参数： String 类型的，这个端点URL的默认链接。
         * 第二个参数： String 类型的，你要进行替代的URL链接。
         *
         * 框架的默认URL链接如下列表，可以作为这个 pathMapping() 方法的第一个参数：
         *      /oauth/authorize ：授权端点。
         *      /oauth/token ：令牌端点。
         *      /oauth/confirm_access ：用户确认授权提交端点。
         *      /oauth/error ：授权服务错误信息端点。
         *      /oauth/check_token ：用于资源服务访问的令牌解析端点。
         *      /oauth/token_key ：提供公有密匙的端点，如果你使用JWT令牌的话。
         *
         */
        endpoints.pathMapping("/oauth/confirm_access","/custom/confirm_access");
    }

    /**
     * AuthorizationServerSecurityConfigurer ：用来配置令牌端点的安全约束.
     * 参看：https://bingbing.blog.csdn.net/article/details/107076918?spm=1001.2101.3001.6650.3&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-3-107076918-blog-136418297.235%5Ev43%5Epc_blog_bottom_relevance_base4&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-3-107076918-blog-136418297.235%5Ev43%5Epc_blog_bottom_relevance_base4&utm_relevant_index=6
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()") // 指定了访问令牌密钥的端点的安全约束。permitAll() 表示允许所有用户（包括未认证的用户）访问此端点，而无需进行身份验证。, 具体在：org.springframework.security.oauth2.provider.endpoint.TokenEndpoint
                .checkTokenAccess("permitAll()") // 校验token是否有效的断点安全约束。具体定义在，org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint
                .allowFormAuthenticationForClients(); // 这一行允许客户端使用表单身份验证来进行认证。在某些情况下，客户端可能需要使用表单身份验证（例如，使用用户名和密码）向授权服务器进行身份验证，以获取访问令牌或刷新令牌
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource){
        return new JdbcAuthorizationCodeServices(dataSource);
    }


}
