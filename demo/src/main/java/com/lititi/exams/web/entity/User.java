package com.lititi.exams.web.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户实体类
 * @author chenlong
 * date 2025-10-29
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
