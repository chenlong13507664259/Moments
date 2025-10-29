package com.lititi.exams.web.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 好友关系实体类
 * @author chenlong
 * date 2025-10-29
 */
@Data
public class Friend implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 好友ID
     */
    private Long friendId;

    /**
     * 创建时间
     */
    private Date createTime;
}
