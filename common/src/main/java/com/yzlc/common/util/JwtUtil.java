package com.yzlc.common.util;

import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


public class JwtUtil {
    /**
     * 签名
     *
     * @param subject   userId
     * @param ttlMinute 过期分钟
     * @param key       密钥
     * @return 签名
     */
    public static String sign(String subject, long ttlMinute, String key) {
        SignatureAlgorithm alg = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        Key signKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), alg.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setSubject(subject)
                .signWith(signKey, alg);

        if (ttlMinute > 0) {
            long expMillis = nowMillis + ttlMinute * 1000 * 60;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    /**
     * 解析
     *
     * @param jws 签名
     * @param key 密钥
     * @return 签名信息
     * @throws ExpiredJwtException 签名过期
     */
    public static Claims parseJws(String jws, String key) throws ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(jws).getBody();
    }

    /**
     * 解析
     *
     * @param jws 签名
     * @return 签名信息
     * @throws ExpiredJwtException 签名过期
     */
    public static Claims parseJwt(String jws) throws ExpiredJwtException {
        String jwt = jws.substring(0, jws.lastIndexOf(".") + 1);
        return Jwts.parserBuilder()
                .build()
                .parseClaimsJwt(jwt).getBody();
    }

    public static void main(String[] args) {
        String key = "01234567890123456789012345678901";
        String token = sign("subject", 60 * 24 * 30, key);
        System.out.println(token);

        Claims jwt = parseJwt(token);
        System.out.println("IssuedAt: " + jwt.getIssuedAt());
        System.out.println("Subject: " + jwt.getSubject());
        System.out.println("Expiration: " + jwt.getExpiration());

        Claims jws = parseJws(token, key);
        System.out.println("IssuedAt: " + jws.getIssuedAt());
        System.out.println("Subject: " + jws.getSubject());
        System.out.println("Expiration: " + jws.getExpiration());
    }
}
