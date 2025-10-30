package com.lititi.exams.web.entity;

import java.io.Serializable;
import lombok.Data;



/**
 * (testDemo)实体类
 *
 */
@Data
public class TestDemo implements Serializable {
    private static final long serialVersionUID = -54333970903315306L;

    private Long id;
/**
     * 创建时间毫秒
     */
    private Long createTime;
/**
     * 更新时间毫秒
     */
    private Long updateTime;

}

