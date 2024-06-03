package com.george.controller;

import com.george.model.WrapperResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author kdyzm
 */
@RestController
@Slf4j
public class TokenController {

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/refresh-token")
    public WrapperResult<String> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        String reqUrl = "http://auth-center/oauth/token?grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=c1&client_secret=secret";
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(reqUrl, null, String.class);
        log.info(stringResponseEntity.getBody());
        return WrapperResult.success(stringResponseEntity.getBody());
    }
}
