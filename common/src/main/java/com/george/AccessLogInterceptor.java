package com.george;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 自定义拦截器，用于记录每个请求的详细信息，包括
 *      域名、URI、Referer、客户端IP、用户代理、请求参数等。通过这种方式，可以方便地追踪和分析请求日志。
 *
 * @author George
 */
@Component
public class AccessLogInterceptor implements HandlerInterceptor {

    private static final Logger ACCESS_LOG = LoggerFactory.getLogger("ACCESS");

    private static final String SEPARATOR = " ";

    /**
     * 在请求处理之前调用，这里直接返回true，表示请求继续处理。
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        return true;
    }

    /**
     * 在请求处理之后、视图渲染之前调用，这里没有具体实现。
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    /**
     * 在请求完成之后调用，用于记录日志。
     * @param request
     * @param response
     * @param handler
     * @param ex
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 获取请求的域名、URI、Referer、x-auth-token、Installation-ID等信息
        String domain = request.getServerName();
        String uri = request.getRequestURI();
        String refer = request.getHeader("Referer");
        final String token = request.getHeader("x-auth-token");
        final String installation_ID = request.getHeader("Installation-ID");

        // 获取或生成traceId，并放入MDC（Mapped Diagnostic Context）中，以便日志记录器使用。
        String traceId = request.getHeader("X-B3-TraceId");
        if (StringUtils.isEmpty(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        MDC.put("X-B3-TraceId", traceId);

        // 获取客户端IP地址和用户代理。
        String remoteIp = getRemoteAddr(request);
        String userAgent = request.getHeader("User-Agent");
        // 获取请求参数，并将其转换为键值对的Map。
        Map<String, String> paramPair = getRequestParamValueMap(request);

        // 调用printAccesslog方法记录访问日志。
        printAccesslog(traceId, remoteIp, "", "", domain, uri, refer, userAgent, paramPair);
    }

    /**
     * 构建日志字符串，包括时间戳、客户端IP、traceId、域名、URI、Referer和用户代理。
     * @param traceId
     * @param remoteIp
     * @param userId
     * @param userName
     * @param domain
     * @param uri
     * @param refer
     * @param userAgent
     * @param paramPair
     */
    private void printAccesslog(String traceId, String remoteIp, String userId, String userName,
                                String domain, String uri, String refer,
                                String userAgent, Map<String, String> paramPair) {

        StringBuilder sb = new StringBuilder();

        long timestamp = System.currentTimeMillis();
        sb.append(SEPARATOR).append(timestamp);
        sb.append(SEPARATOR).append(remoteIp);
//        sb.append(SEPARATOR).append(userId);
//        sb.append(SEPARATOR).append(userName);
        //RequestId用于定位access log与业务log
        sb.append(SEPARATOR).append(traceId);
        sb.append(SEPARATOR).append(domain);
        sb.append(SEPARATOR).append(uri);
        sb.append(SEPARATOR).append(refer);
        sb.append(SEPARATOR).append(userAgent);
        //将参数map打印成json格式，利于统计分析
        sb.append(SEPARATOR);

        ACCESS_LOG.info(sb.toString());
    }

    /**
     * 获取客户端IP地址的方法
     * @return
     */
    private String getRemoteAddr(HttpServletRequest request) {
        String ip;
        // 从X-Forwarded-For头中解析客户端IP地址。
        @SuppressWarnings("unchecked")
        Enumeration<String> xffs = request.getHeaders("X-Forwarded-For");
        if (xffs.hasMoreElements()) {
            String xff = xffs.nextElement();
            ip = resolveClientIPFromXFF(xff);
            if (isValidIP(ip)) {
                return ip;
            }
        }
        // 检查其他常见头部字段（如Proxy-Client-IP和WL-Proxy-Client-IP）以获取客户端IP地址。
        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIP(ip)) {
            return ip;
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIP(ip)) {
            return ip;
        }
        // 如果上述头部字段都没有有效IP地址，返回request.getRemoteAddr()。
        return request.getRemoteAddr();
    }

    /**
     * 从X-Forwarded-For头部中获取客户端的真实IP。 X-Forwarded-For并不是RFC定义的标准HTTP请求Header
     * ，可以参考http://en.wikipedia.org/wiki/X-Forwarded-For
     *
     * @param xff X-Forwarded-For头部的值
     * @return 如果能够解析到client IP，则返回表示该IP的字符串，否则返回null
     */
    private String resolveClientIPFromXFF(String xff) {
        if (xff == null || xff.isEmpty()) {
            return null;
        }
        String[] ss = xff.split(",");
        for (int i = ss.length - 1; i >= 0; i--) {// x-forward-for链反向遍历
            String ip = ss[i].trim();
            if (isValidIP(ip)) {
                return ip;
            }
        }

        return null;
    }

    private static final Pattern ipPattern = Pattern.compile("([0-9]{1,3}\\.){3}[0-9]{1,3}");

    /**
     * 使用正则表达式验证IP地址是否有效。
     * @param ip
     * @return
     */
    private boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }
        return ipPattern.matcher(ip).matches();
    }

    /**
     * 获取请求参数，将其转换为键值对的Map。
     * @param request
     * @return
     */
    private Map<String, String> getRequestParamValueMap(HttpServletRequest request) {
        Map<String, String> param2value = new HashMap<>();
        Enumeration e = request.getParameterNames();
        String param;

        while(e.hasMoreElements()) {
            param = (String)e.nextElement();
            if(param != null) {
                String value = request.getParameter(param);
                if(value != null) {
                    param2value.put(param, value);
                }
            }
        }

        return param2value;
    }
}
