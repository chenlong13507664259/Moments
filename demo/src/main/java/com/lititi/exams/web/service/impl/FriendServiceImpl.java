package com.lititi.exams.web.service.impl;

import com.lititi.exams.web.dao.FriendMapper;
import com.lititi.exams.web.entity.Friend;
import com.lititi.exams.web.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 好友关系服务实现类
 * @author chenlong
 * date 2025-11-01
 */
@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendMapper friendMapper;

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        // 参数校验
        if (userId == null || friendId == null) {
            throw new IllegalArgumentException("用户ID和好友ID不能为空");
        }

        // 不能添加自己为好友
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("不能添加自己为好友");
        }

        // 检查是否已经是好友
        Friend existingFriend = friendMapper.selectByUserAndFriendId(userId, friendId);
        if (existingFriend != null) {
            throw new IllegalStateException("已经是好友关系");
        }

        // 创建好友关系
        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        friend.setCreateTime(new Date());

        int result = friendMapper.insert(friend);
        return result > 0;
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        // 参数校验
        if (userId == null || friendId == null) {
            throw new IllegalArgumentException("用户ID和好友ID不能为空");
        }

        // 检查好友关系是否存在
        Friend friendRelation = friendMapper.selectByUserAndFriendId(userId, friendId);
        if (friendRelation == null) {
            throw new IllegalStateException("好友关系不存在");
        }

        // 删除好友关系（双向删除）
        int result1 = friendMapper.deleteByUserAndFriendId(userId, friendId);
        int result2 = friendMapper.deleteByUserAndFriendId(friendId, userId);

        return result1 > 0 || result2 > 0;
    }

    @Override
    public List<Long> getFriendIds(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return friendMapper.selectFriendIdsByUserId(userId);
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            return false;
        }
        Friend friend = friendMapper.selectByUserAndFriendId(userId, friendId);
        return friend != null;
    }

    @Override
    public Friend getFriendRelation(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            return null;
        }
        return friendMapper.selectByUserAndFriendId(userId, friendId);
    }
}