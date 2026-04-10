package com.xuexiang.xupdateservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * token生成工具
 * @author xuexiang
 * @since 2018/8/6 下午4:27
 */
public final class TokenUtils {

    /**
     * 签名秘钥
     */
    public static final String SECRET = "xuexiangjys";
    private static final SecretKey SIGNING_KEY = buildSigningKey();

    /**
     * 生成token
     *
     * @param id 一般传入userName
     * @return
     */
    public static String createJwtToken(String id) {
        String issuer = "www.github.com";
        String subject = "xuexiangjys@163.com";
        long ttlMillis = 60 * 60 * 1000; //有效期一小时
        return createJwtToken(id, issuer, subject, ttlMillis);
    }

    /**
     * 生成Token
     *
     * @param id        编号
     * @param issuer    该JWT的签发者，是否使用是可选的
     * @param subject   该JWT所面向的用户，是否使用是可选的；
     * @param ttlMillis 签发时间 （有效时间，过期会报错）
     * @return token String
     */
    public static String createJwtToken(String id, String issuer, String subject, long ttlMillis) {
        // 生成签发时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .id(id)
                .issuedAt(now)
                .subject(subject)
                .issuer(issuer)
                .signWith(SIGNING_KEY, Jwts.SIG.HS256);

        // if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();

    }

    // Sample method to validate and read the JWT
    public static Claims parseJWT(String jwt) {
        return Jwts.parser()
                .verifyWith(SIGNING_KEY)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }


    /**
     * 从HttpServletRequest中解析出token
     *
     * @param request
     * @return
     */
    public static String parseToken(HttpServletRequest request) {
        String accessToken = request.getHeader("X-Token");
        if (StringUtils.isEmpty(accessToken)) {
            accessToken = request.getParameter("token");
        }
        return accessToken;
    }

    public static void main(String[] args) {
        System.out.println(TokenUtils.createJwtToken("11111"));
    }

    // Preserve the configured secret string while deriving an HS256-compliant key for newer JJWT versions.
    private static SecretKey buildSigningKey() {
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256")
                    .digest(SECRET.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to initialize jwt signing key", e);
        }
    }

}
