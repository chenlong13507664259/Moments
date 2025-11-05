package com.lititi.exams.web.service.impl;

import com.lititi.exams.commons2.cache.CacheOperator;
import com.lititi.exams.commons2.enumeration.RedisDB;
import com.lititi.exams.commons2.spring.SpringContextHolder;
import com.lititi.exams.web.dao.UserMapper;
import com.lititi.exams.web.entity.User;
import com.lititi.exams.web.service.FriendService;
import com.lititi.exams.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 * @author chenlong
 * date 2025-10-31
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CacheOperator cacheOperator;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 空值缓存的过期时间（防止缓存穿透）
    private static final long EMPTY_CACHE_EXPIRE = 300L; // 5分钟

    // 用户信息缓存过期时间基础值
    private static final long USER_CACHE_EXPIRE_BASE = 1800L; // 30分钟

    // 用户信息缓存过期时间随机增量（防止缓存雪崩）
    private static final long USER_CACHE_EXPIRE_RANDOM = 600L; // 10分钟

    // 分布式锁过期时间（防止缓存击穿）
    private static final long LOCK_EXPIRE_TIME = 10L; // 10秒

    @Override
    public User register(String phone, String nickname, String password) {
        // 检查手机号是否已被注册
        User existingUser = userMapper.selectByPhone(phone);
        if (existingUser != null) {
            throw new RuntimeException("该手机号已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setPhone(phone);
        user.setNickname(nickname);
        user.setAvatar(""); // 默认头像为空

        // 在实际项目中应该对密码进行加密，这里简化处理
        // 可以使用BCryptPasswordEncoder等加密方式
        user.setPassword(password);

        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        // 插入数据库
        userMapper.insert(user);
        // 清除密码信息再返回
        user.setPassword(null);

        // 清除缓存
        clearUserCache(user.getId());
        return user;
    }

    @Override
    public User login(String phone, String password) {
        // 根据手机号查询用户
        User user = getUserByPhone(phone);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        // 实际项目中应该使用BCryptPasswordEncoder匹配密码
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 清除密码信息再返回
        user.setPassword(null);
        return user;
    }

    @Override
    public User getUserByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    public User getUserById(Long id) {
        if (id == null) {
            return null;
        }

        // 构造缓存key
        String userCacheKey = "user:id:" + id;

        System.out.println("正在查询用户ID: " + id + " 的缓存，缓存key为: " + userCacheKey);

        // 先从缓存中获取
        Object cachedUser = cacheOperator.get(userCacheKey, RedisDB.OTHER);
        if (cachedUser != null) {
            // 缓存中有数据
            System.out.println("缓存命中，用户ID: " + id);
            // 检查是否是空值缓存（防止缓存穿透）
            if (cachedUser instanceof String && "NULL".equals(cachedUser)) {
                System.out.println("缓存中是空值占位符，用户ID: " + id + " 不存在");
                return null;
            }
            System.out.println("从缓存中获取到用户信息: " + ((User) cachedUser).getNickname());
            return (User) cachedUser;
        }

        System.out.println("缓存未命中，需要查询数据库，用户ID: " + id);

        // 缓存中没有，使用分布式锁防止缓存击穿
        String lockKey = "lock:user:id:" + id;
        Boolean lockAcquired = false;
        try {
            // 尝试获取分布式锁
            lockAcquired = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_EXPIRE_TIME, TimeUnit.SECONDS);

            if (lockAcquired != null && lockAcquired) {
                System.out.println("获取到分布式锁，开始查询数据库，用户ID: " + id);
                // 获取锁成功，查询数据库
                User user = userMapper.selectById(id);

                // 使用随机过期时间防止缓存雪崩
                long expireTime = USER_CACHE_EXPIRE_BASE + ThreadLocalRandom.current().nextLong(USER_CACHE_EXPIRE_RANDOM);

                if (user != null) {
                    // 数据库中有数据，放入缓存
                    user.setPassword(null); // 清除密码信息
                    cacheOperator.setEx(userCacheKey, user, expireTime, RedisDB.OTHER);
                    System.out.println("将用户信息写入缓存，用户ID: " + id + "，过期时间: " + expireTime + "秒");
                } else {
                    // 数据库中无数据，缓存空值防止缓存穿透
                    cacheOperator.setEx(userCacheKey, "NULL", EMPTY_CACHE_EXPIRE, RedisDB.OTHER);
                    System.out.println("用户不存在，写入空值占位符到缓存，用户ID: " + id + "，过期时间: " + EMPTY_CACHE_EXPIRE + "秒");
                }

                return user;
            } else {
                // 获取锁失败，等待一段时间后重试从缓存获取
                System.out.println("未能获取到分布式锁，等待后重试，用户ID: " + id);
                Thread.sleep(100);
                return getUserById(id); // 递归重试
            }
        } catch (Exception e) {
            // 发生异常时，直接查询数据库（降级处理）
            System.err.println("查询用户时发生异常: " + e.getMessage());
            User user = userMapper.selectById(id);
            // 不放入缓存，防止异常数据污染缓存
            if (user != null) {
                user.setPassword(null);
            }
            return user;
        } finally {
            // 释放锁
            if (lockAcquired != null && lockAcquired) {
                stringRedisTemplate.delete(lockKey);
                System.out.println("释放分布式锁，用户ID: " + id);
            }
        }
    }

    @Override
    public User updateUserProfile(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 查询原用户信息
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新个人信息字段（只更新非空字段）
        if (user.getNickname() != null && !user.getNickname().trim().isEmpty()) {
            existingUser.setNickname(user.getNickname());
        }
        if (user.getAvatar() != null && !user.getAvatar().trim().isEmpty()) {
            existingUser.setAvatar(user.getAvatar());
        }

        // 更新时间
        existingUser.setUpdateTime(new Date());

        // 执行更新
        userMapper.update(existingUser);

        // 清除密码信息再返回
        existingUser.setPassword(null);

        // 清除用户和所有好友的缓存
        clearUserCache(user.getId());
        return existingUser;
    }

    @Override
    public User updateAvatar(Long userId, String avatarUrl) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 查询原用户信息
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新头像
        existingUser.setAvatar(avatarUrl);
        existingUser.setUpdateTime(new Date());

        // 执行更新
        userMapper.update(existingUser);

        // 清除密码信息再返回
        existingUser.setPassword(null);

        // 清除用户和所有好友的缓存
        clearUserCache(userId);
        return existingUser;
    }

    /**
     * 清除用户和其所有好友的缓存
     * @param userId 用户ID
     */
    private void clearUserCache(Long userId) {
        if (userId == null) {
            return;
        }

        // 清除用户自己的缓存
        String userCacheKey = "user:id:" + userId;
        cacheOperator.delete(userCacheKey, RedisDB.OTHER);
        System.out.println("清除用户缓存，用户ID: " + userId);

        // 清除用户所有好友的缓存
        try {
            // 通过SpringContextHolder获取FriendService实例，避免循环依赖
            FriendService friendService = SpringContextHolder.getBean(FriendService.class);
            if (friendService != null) {
                List<Long> friendIds = friendService.getFriendIds(userId);
                if (friendIds != null && !friendIds.isEmpty()) {
                    System.out.println("用户 " + userId + " 有 " + friendIds.size() + " 个好友，将清除他们的缓存");
                    for (Long friendId : friendIds) {
                        String friendCacheKey = "user:id:" + friendId;
                        cacheOperator.delete(friendCacheKey, RedisDB.OTHER);
                        System.out.println("清除好友缓存，好友ID: " + friendId);
                    }
                }
            }
        } catch (Exception e) {
            // 获取FriendService失败时忽略错误，只清除当前用户缓存
            System.err.println("清除好友缓存时发生错误: " + e.getMessage());
        }
    }
}
