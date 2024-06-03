package com.george.intercepter;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.george.dto.UserDetailsExpand;

/**
 * <p>
 *     使用阿里的 TransmittableThreadLocal，解决线程间数据传递问题
 * </p>
 *
 * @author George
 * @date 2024.06.03 15:55
 */
public class AuthContextHolder {
    private TransmittableThreadLocal threadLocal = new TransmittableThreadLocal();
    private static final AuthContextHolder instance = new AuthContextHolder();

    private AuthContextHolder() {
    }

    public static AuthContextHolder getInstance() {
        return instance;
    }

    public void setContext(UserDetailsExpand t) {
        this.threadLocal.set(t);
    }

    public UserDetailsExpand getContext() {
        return (UserDetailsExpand)this.threadLocal.get();
    }

    public void clear() {
        this.threadLocal.remove();
    }
}
