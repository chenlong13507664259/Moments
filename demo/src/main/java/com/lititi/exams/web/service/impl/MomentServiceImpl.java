package com.lititi.exams.web.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.lititi.exams.commons2.object.PageParam;
import com.lititi.exams.web.dao.FriendMapper;
import com.lititi.exams.web.dao.MomentMapper;
import com.lititi.exams.web.entity.Moment;
import com.lititi.exams.web.service.MomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        return momentMapper.insert(moment) > 0;
    }

    @Override
    public boolean deleteMoment(long momentId, long userId) {
        return momentMapper.deleteByIdAndUserId(momentId, userId) > 0;
    }

    @Override
    public List<Moment> getFriendMoments(long userId, PageParam pageParam) {
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
        return momentMapper.selectMomentsByUserAndFriends(userId, friendIds, offset, pageSize);
    }
}
