package com.lititi.exams.web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 动态实体类
 * @author chenlong
 * date 2025-10-29
 */
@Data
public class Moment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 发布者ID
     */
    private Long userId;

    /**
     * 文字内容
     */
    private String content;

    /**
     * 图片URL列表，JSON格式
     */
    private String images;

    /**
     * 定位信息
     */
    private String location;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
