package com.lititi.exams.commons2.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JWT工具类
 * @author chenlong
 * date 2025-10-31
 */
public class JwtUtil {

    // 令牌过期时间 24小时(毫秒)
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    // 令牌秘钥
    private static final String SECRET_KEY = "lititi_exams_secret_key";

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @return 令牌
     */
    public static String generateToken(Long userId) {
        // 设置过期时间
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);

        // 简化的token生成，实际项目中应使用JWT库如jjwt
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("expire", expireDate.getTime());
        claims.put("uuid", UUID.randomUUID().toString());

        // 实际项目中应使用JWT库生成标准JWT token
        // 这里简化处理，直接用UUID+用户ID作为token
        return userId + "_" + UUID.randomUUID().toString();
    }

    /**
     * 解析JWT令牌
     * @param token 令牌
     * @return 用户ID
     */
    public static Long parseToken(String token) {
        // 简化解析过程，实际项目中应使用JWT库解析
        if (token == null || token.isEmpty()) {
            return null;
        }

        try {
            String[] parts = token.split("_");
            if (parts.length > 0) {
                return Long.valueOf(parts[0]);
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return null;
    }

    /**
     * 验证令牌是否有效
     * @param token 令牌
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        // 简化验证过程，实际项目中应验证签名和过期时间
        return parseToken(token) != null;
    }
}
