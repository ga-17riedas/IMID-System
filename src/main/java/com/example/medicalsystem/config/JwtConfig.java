package com.example.medicalsystem.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT配置类
 * 
 * 负责JWT(JSON Web Token)的生成、验证和解析等操作。
 * 提供了令牌的创建、有效性验证、用户名和角色提取等功能。
 * 使用配置文件中定义的密钥和过期时间进行令牌处理。
 * JWT用于实现无状态的用户认证，前后端分离架构中的身份验证。
 * 
 * @author Medical System Team
 * @version 1.0
 */
@Component
public class JwtConfig {
    
    /**
     * 日志记录器
     * 用于记录JWT处理过程中的信息和错误
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    /**
     * JWT密钥
     * 从配置文件中注入，用于签名和验证JWT
     */
    @Value("${app.jwtSecret}")
    private String secret;

    /**
     * JWT过期时间（毫秒）
     * 从配置文件中注入，定义令牌的有效期
     */
    @Value("${app.jwtExpirationInMs}")
    private int expirationInMs;

    /**
     * 生成JWT令牌
     * 
     * 根据用户名和角色创建JWT令牌，包含身份信息和过期时间。
     * 在令牌中嵌入角色信息，用于后续的权限控制。
     * 
     * @param username 用户名
     * @param role 用户角色
     * @return 生成的JWT令牌字符串
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    /**
     * 创建令牌
     * 
     * 使用JJWT库创建JWT令牌，设置声明、主题、颁发时间和过期时间。
     * 使用HS512算法和配置的密钥进行签名。
     * 
     * @param claims 令牌中包含的声明（额外信息）
     * @param subject 令牌的主题（通常是用户名）
     * @return 创建的JWT令牌字符串
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 验证令牌有效性
     * 
     * 检查JWT令牌是否有效，包括签名验证和过期检查。
     * 自动处理"Bearer "前缀，适应HTTP Authorization头的格式。
     * 在验证过程中捕获可能的异常，确保不会因令牌无效而导致系统崩溃。
     * 
     * @param token 要验证的JWT令牌
     * @return 令牌是否有效
     */
    public Boolean validateToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String secret = this.secret;
            if (secret == null || secret.trim().isEmpty()) {
                logger.error("JWT secret is not configured properly");
                return false;
            }
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中提取用户名
     * 
     * 解析JWT令牌，提取其中的subject声明（用户名）。
     * 自动处理"Bearer "前缀，适应HTTP Authorization头的格式。
     * 
     * @param token JWT令牌
     * @return 令牌中包含的用户名
     */
    public String getUsernameFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从令牌中提取角色
     * 
     * 解析JWT令牌，提取其中的role声明（用户角色）。
     * 自动处理"Bearer "前缀，适应HTTP Authorization头的格式。
     * 在提取过程中捕获可能的异常，确保不会因令牌格式错误而导致系统崩溃。
     * 
     * @param token JWT令牌
     * @return 令牌中包含的用户角色，如果提取失败则返回null
     */
    public String getRoleFromToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
            
            return claims.get("role", String.class);
        } catch (Exception e) {
            logger.error("Error getting role from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从令牌中提取特定声明
     * 
     * 使用函数式接口实现灵活的声明提取，可以根据需要提取不同类型的声明。
     * 
     * @param token JWT令牌
     * @param claimsResolver 声明解析函数
     * @param <T> 返回值类型
     * @return 解析后的声明值
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 获取令牌中的所有声明
     * 
     * 解析JWT令牌，获取其中包含的所有声明（Claims）。
     * 
     * @param token JWT令牌
     * @return 令牌中的所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
} 