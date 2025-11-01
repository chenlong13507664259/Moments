package com.lititi.exams.web.service;

import com.lititi.exams.web.entity.Friend;

import java.util.List;

/**
 * 好友关系服务接口
 * @author chenlong
 * date 2025-11-01
 */
public interface FriendService {

    /**
     * 添加好友
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 是否添加成功
     */
    boolean addFriend(Long userId, Long friendId);

    /**
     * 删除好友
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 是否删除成功
     */
    boolean deleteFriend(Long userId, Long friendId);

    /**
     * 获取用户的所有好友ID
     * @param userId 用户ID
     * @return 好友ID列表
     */
    List<Long> getFriendIds(Long userId);

    /**
     * 检查两人是否为好友关系
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 是否为好友
     */
    boolean isFriend(Long userId, Long friendId);

    /**
     * 获取好友关系详情
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 好友关系对象
     */
    Friend getFriendRelation(Long userId, Long friendId);
}