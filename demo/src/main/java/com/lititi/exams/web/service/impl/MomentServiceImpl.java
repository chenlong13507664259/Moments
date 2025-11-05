package com.lititi.exams.web.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.lititi.exams.commons2.cache.CacheOperator;
import com.lititi.exams.commons2.enumeration.RedisDB;
import com.lititi.exams.commons2.object.PageParam;
import com.lititi.exams.commons2.spring.SpringContextHolder;
import com.lititi.exams.web.dao.FriendMapper;
import com.lititi.exams.web.dao.MomentMapper;
import com.lititi.exams.web.entity.Moment;
import com.lititi.exams.web.service.FriendService;
import com.lititi.exams.web.service.MomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 动态服务实现类
 * @author chenlong
 * date 2025-10-29
 */
@Service
public class MomentServiceImpl implements MomentService {

    @Autowired
    private MomentMapper momentMapper;

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private CacheOperator cacheOperator;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 空值缓存的过期时间（防止缓存穿透）
    private static final long EMPTY_CACHE_EXPIRE = 300L; // 5分钟

    // 朋友圈动态缓存过期时间基础值
    private static final long MOMENT_CACHE_EXPIRE_BASE = 600L; // 10分钟

    // 朋友圈动态缓存过期时间随机增量（防止缓存雪崩）
    private static final long MOMENT_CACHE_EXPIRE_RANDOM = 300L; // 5分钟

    // 分布式锁过期时间（防止缓存击穿）
    private static final long LOCK_EXPIRE_TIME = 10L; // 10秒

    @Override
    public boolean publishMoment(Moment moment) {
        // 检查文字内容长度不超过500字符
        if (moment.getContent() != null && moment.getContent().length() > 500) {
            throw new IllegalArgumentException("文字内容不能超过500个字符");
        }

        // 检查图片数量不超过9张
        if (moment.getImages() != null && !moment.getImages().isEmpty()) {
            try {
                JSONArray imagesArray = JSONArray.parseArray(moment.getImages());
                if (imagesArray.size() > 9) {
                    throw new IllegalArgumentException("图片数量不能超过9张");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("图片数据格式错误");
            }
        }

        // 设置创建和更新时间
        Date currentTime = new Date();
        moment.setCreateTime(currentTime);
        moment.setUpdateTime(currentTime);

        // 插入动态
        boolean result = momentMapper.insert(moment) > 0;

        System.out.println("发布朋友圈动态: " + (result ? "成功" : "失败") + "，用户ID: " + moment.getUserId());

        // 清除该用户的朋友圈缓存
        if (result) {
            clearUserAndFriendsCache(moment.getUserId());
        }

        return result;
    }

    @Override
    public boolean deleteMoment(long momentId, long userId) {
        boolean result = momentMapper.deleteByIdAndUserId(momentId, userId) > 0;

        System.out.println("删除朋友圈动态: " + (result ? "成功" : "失败") + "，动态ID: " + momentId + "，用户ID: " + userId);

        // 清除该用户的朋友圈缓存
        if (result) {
            clearUserAndFriendsCache(userId);
        }

        return result;
    }

    @Override
    public List<Moment> getFriendMoments(long userId, PageParam pageParam) {
        // 构造缓存key
        String cacheKey = "moments:user:" + userId + ":page:" + pageParam.getPageNo() + ":size:" + pageParam.getPageSize();

        System.out.println("正在查询用户朋友圈缓存，用户ID: " + userId + "，页码: " + pageParam.getPageNo() + "，缓存key为: " + cacheKey);

        // 先从缓存中获取
        Object cachedMoments = cacheOperator.get(cacheKey, RedisDB.OTHER);
        if (cachedMoments != null) {
            // 缓存中有数据
            System.out.println("朋友圈缓存命中，用户ID: " + userId + "，页码: " + pageParam.getPageNo());
            // 检查是否是空值缓存（防止缓存穿透）
            if (cachedMoments instanceof String && "NULL".equals(cachedMoments)) {
                System.out.println("朋友圈缓存中是空值占位符，用户ID: " + userId + " 没有朋友圈数据");
                return new ArrayList<>();
            }
            List<Moment> moments = (List<Moment>) cachedMoments;
            System.out.println("从缓存中获取到朋友圈数据，共 " + moments.size() + " 条，用户ID: " + userId);
            return moments;
        }

        System.out.println("朋友圈缓存未命中，需要查询数据库，用户ID: " + userId + "，页码: " + pageParam.getPageNo());

        // 缓存中没有，使用分布式锁防止缓存击穿
        String lockKey = "lock:moments:user:" + userId + ":page:" + pageParam.getPageNo() + ":size:" + pageParam.getPageSize();
        Boolean lockAcquired = false;
        try {
            // 尝试获取分布式锁
            lockAcquired = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_EXPIRE_TIME, TimeUnit.SECONDS);

            if (lockAcquired != null && lockAcquired) {
                System.out.println("获取到朋友圈分布式锁，开始查询数据库，用户ID: " + userId + "，页码: " + pageParam.getPageNo());
                // 获取锁成功，查询数据库
                // 获取好友ID列表
                List<Long> friendIds = friendMapper.selectFriendIdsByUserId(userId);

                // 如果没有好友，只显示自己的动态
                if (friendIds == null || friendIds.isEmpty()) {
                    friendIds = new ArrayList<>();
                }

                // 添加自己的ID到列表中，以便也能看到自己的动态
                friendIds.add(userId);

                // 分页参数
                int pageNo = (pageParam.getPageNo() != null && pageParam.getPageNo() > 0) ? pageParam.getPageNo() : 1;
                int pageSize = (pageParam.getPageSize() != null && pageParam.getPageSize() > 0) ? pageParam.getPageSize() : 10;
                int offset = (pageNo - 1) * pageSize;

                // 查询动态
                List<Moment> moments = momentMapper.selectMomentsByUserAndFriends(userId, friendIds, offset, pageSize);

                // 使用随机过期时间防止缓存雪崩
                long expireTime = MOMENT_CACHE_EXPIRE_BASE + ThreadLocalRandom.current().nextLong(MOMENT_CACHE_EXPIRE_RANDOM);

                if (moments != null && !moments.isEmpty()) {
                    // 数据库中有数据，放入缓存
                    cacheOperator.setEx(cacheKey, moments, expireTime, RedisDB.OTHER);
                    System.out.println("将朋友圈数据写入缓存，用户ID: " + userId + "，共 " + moments.size() + " 条，过期时间: " + expireTime + "秒");
                } else {
                    // 数据库中无数据或为空列表，缓存空值防止缓存穿透
                    cacheOperator.setEx(cacheKey, "NULL", EMPTY_CACHE_EXPIRE, RedisDB.OTHER);
                    System.out.println("朋友圈数据为空，写入空值占位符到缓存，用户ID: " + userId + "，过期时间: " + EMPTY_CACHE_EXPIRE + "秒");
                    // 返回空列表而不是null
                    moments = new ArrayList<>();
                }

                return moments;
            } else {
                // 获取锁失败，等待一段时间后重试从缓存获取
                System.out.println("未能获取到朋友圈分布式锁，等待后重试，用户ID: " + userId);
                Thread.sleep(100);
                return getFriendMoments(userId, pageParam); // 递归重试
            }
        } catch (Exception e) {
            // 发生异常时，直接查询数据库（降级处理）
            System.err.println("查询朋友圈时发生异常: " + e.getMessage());
            List<Long> friendIds = friendMapper.selectFriendIdsByUserId(userId);
            if (friendIds == null || friendIds.isEmpty()) {
                friendIds = new ArrayList<>();
            }
            friendIds.add(userId);

            int pageNo = (pageParam.getPageNo() != null && pageParam.getPageNo() > 0) ? pageParam.getPageNo() : 1;
            int pageSize = (pageParam.getPageSize() != null && pageParam.getPageSize() > 0) ? pageParam.getPageSize() : 10;
            int offset = (pageNo - 1) * pageSize;

            List<Moment> moments = momentMapper.selectMomentsByUserAndFriends(userId, friendIds, offset, pageSize);
            if (moments == null) {
                moments = new ArrayList<>();
            }
            return moments;
        } finally {
            // 释放锁
            if (lockAcquired != null && lockAcquired) {
                stringRedisTemplate.delete(lockKey);
                System.out.println("释放朋友圈分布式锁，用户ID: " + userId);
            }
        }
    }

    /**
     * 清除用户和其好友的缓存
     * @param userId 用户ID
     */
    private void clearUserAndFriendsCache(long userId) {
        // 清除该用户的朋友圈缓存
        clearUserMomentsCache(userId);

        try {
            // 通过SpringContextHolder获取FriendService实例，避免循环依赖
            FriendService friendService = SpringContextHolder.getBean(FriendService.class);
            if (friendService != null) {
                List<Long> friendIds = friendService.getFriendIds(userId);
                if (friendIds != null && !friendIds.isEmpty()) {
                    System.out.println("用户 " + userId + " 有 " + friendIds.size() + " 个好友，将清除他们的朋友圈缓存");
                    for (Long friendId : friendIds) {
                        // 清除每个好友的朋友圈缓存
                        clearUserMomentsCache(friendId);
                    }
                }
            }
        } catch (Exception e) {
            // 获取FriendService失败时忽略错误
            System.err.println("清除好友朋友圈缓存时发生错误: " + e.getMessage());
        }
    }

    /**
     * 清除用户的朋友圈缓存
     * @param userId 用户ID
     */
    private void clearUserMomentsCache(long userId) {
        // 这里采用一种简单的策略：清除用户最近可能访问的几页朋友圈缓存
        // 在实际项目中可以根据具体情况调整清除策略
        System.out.println("开始清除用户朋友圈缓存，用户ID: " + userId);
        int clearedCount = 0;
        for (int pageNo = 1; pageNo <= 10; pageNo++) {
            for (int pageSize : new int[]{10, 20, 50}) {
                String cacheKey = "moments:user:" + userId + ":page:" + pageNo + ":size:" + pageSize;
                cacheOperator.delete(cacheKey, RedisDB.OTHER);
                clearedCount++;
            }
        }
        System.out.println("清除用户朋友圈缓存完成，用户ID: " + userId + "，共清除 " + clearedCount + " 个缓存项");
    }
}
