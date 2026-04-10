package com.xuexiang.xupdateservice.utils;

import io.jsonwebtoken.Claims;

public class TokenUtilsCli {

    public static void main(String[] args) {
        String loginName = args[0];
        String token = TokenUtils.createJwtToken(loginName);
        Claims claims = TokenUtils.parseJWT(token);
        System.out.println(claims.getId());
    }
}
