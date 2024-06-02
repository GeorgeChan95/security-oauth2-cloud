package com.george.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kdyzm
 */
@RestController
@Slf4j
public class OrderController {

    @GetMapping("/r1")
    @PreAuthorize("hasAnyAuthority('p1')")
    public String r1(){
        String userName = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("当前接口操作用户，用户名: {}", userName);
        return "访问资源r1";
    }
}
