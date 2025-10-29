package com.lititi.exams.web.dao;

import com.lititi.exams.web.entity.Moment;
import com.lititi.exams.commons2.annotation.Master;
import com.lititi.exams.commons2.annotation.Slave;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 动态表数据库访问层
 * @author chenlong
 * date 2025-10-29
 */
public interface MomentMapper {

    /**
     * 查询用户及其好友的动态（分页）
     *
     * @param userId 用户ID
     * @param friendIds 好友ID列表
     * @param offset 偏移量
     * @param limit 数量限制
     * @return 动态列表
     */
    @Slave
    List<Moment> selectMomentsByUserAndFriends(@Param("userId") long userId,
                                               @Param("friendIds") List<Long> friendIds,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    /**
     * 新增动态
     *
     * @param moment 动态对象
     * @return 影响行数
     */
    @Master
    int insert(Moment moment);

    /**
     * 删除动态
     *
     * @param momentId 动态ID
     * @param userId 用户ID
     * @return 影响行数
     */
    @Master
    int deleteByIdAndUserId(@Param("momentId") long momentId, @Param("userId") long userId);

}
