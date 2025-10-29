package com.lititi.exams.web.dao;

import com.lititi.exams.web.entity.User;
import com.lititi.exams.commons2.annotation.Master;
import com.lititi.exams.commons2.annotation.Slave;


/**
 * 用户表数据库访问层
 * @author chenlong
 * date 2025-10-29
 */
public interface UserMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 用户ID
     * @return 用户对象
     */
    @Slave
    User selectById(long id);

    /**
     * 通过手机号查询用户
     *
     * @param phone 手机号
     * @return 用户对象
     */
    @Slave
    User selectByPhone(String phone);

    /**
     * 新增数据
     *
     * @param user 用户对象
     * @return 影响行数
     */
    @Master
    int insert(User user);

    /**
     * 修改数据
     *
     * @param user 用户对象
     * @return 影响行数
     */
    @Master
    int update(User user);

}
