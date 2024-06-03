package com.george.filter;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author George Chan
 * @date 2024/6/2 15:19
 * <p></p>
 */
@Slf4j
@Component
public class TokenFilter implements GlobalFilter, Ordered {

    private static final String BEAR_HEADER = "Bearer ";

    /**
     * 该值要和auth-server中配置的签名相同
     *
     * com.george.config.TokenConfig#SIGNING_KEY
     */
    private static final String SIGNING_KEY = "auth123";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        //如果没有token，则直接返回401
        if(StringUtils.isEmpty(token)){
            return unAuthorized(exchange);
        }
        // 验证签名并获取payload
        String payLoad;
        try {
            Jwt jwt = JwtHelper.decodeAndVerify(token.replace(BEAR_HEADER, ""), new MacSigner(SIGNING_KEY));
            payLoad = jwt.getClaims();
        } catch (Exception e) {
            log.error("网关验证签名失败，错误信息：", e);
            return unAuthorized(exchange);
        }
        //将PayLoad数据放到header
        ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
        // 将token进行base64编码后，放入请求头中，避免了中文乱码
        builder.header("token-info", Base64.encode(payLoad.getBytes(StandardCharsets.UTF_8))).build();
        //继续执行
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    /**
     * 返回未授权
     * @param exchange
     * @return
     */
    private Mono<Void> unAuthorized(ServerWebExchange exchange){
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * 将该过滤器的优先级设置为最高，因为只要认证不通过，就不能做任何事情
     * @return
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
