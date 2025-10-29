package com.lititi.exams.web.service;

import com.lititi.exams.commons2.object.PageParam;
import com.lititi.exams.web.entity.Moment;

import java.util.List;

/**
 * 动态服务接口
 * @author chenlong
 * date 2025-10-29
 */
public interface MomentService {

    /**
     * 发布动态
     *
     * @param moment 动态对象
     * @return 是否发布成功
     */
    boolean publishMoment(Moment moment);

    /**
     * 删除动态
     *
     * @param momentId 动态ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteMoment(long momentId, long userId);

    /**
     * 查询朋友圈动态（分页）
     *
     * @param userId 用户ID
     * @param pageParam 分页参数
     * @return 动态列表
     */
    List<Moment> getFriendMoments(long userId, PageParam pageParam);
}
