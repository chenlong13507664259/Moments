package com.lititi.exams.web.dao;

import com.lititi.exams.web.entity.Friend;
import com.lititi.exams.commons2.annotation.Master;
import com.lititi.exams.commons2.annotation.Slave;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 好友关系表数据库访问层
 * @author chenlong
 * date 2025-10-29
 */
public interface FriendMapper {

    /**
     * 查询用户的所有好友ID
     * @param userId 用户ID
     * @return 好友ID列表
     */
    @Slave
    List<Long> selectFriendIdsByUserId(long userId);

    /**
     * 查询两人是否为好友关系
     *
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 好友关系对象
     */
    @Slave
    Friend selectByUserAndFriendId(@Param("userId") long userId, @Param("friendId") long friendId);

    /**
     * 新增好友关系
     *
     * @param friend 好友关系对象
     * @return 影响行数
     */
    @Master
    int insert(Friend friend);

    /**
     * 删除好友关系
     *
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 影响行数
     */
    @Master
    int deleteByUserAndFriendId(@Param("userId") long userId, @Param("friendId") long friendId);

}
