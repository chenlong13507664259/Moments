package com.lititi.exams.web.service;

import com.lititi.exams.web.entity.User;

/**
 * 用户服务接口
 * @author chenlong
 * date 2025-10-31
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param phone 手机号
     * @param nickname 昵称
     * @param password 密码
     * @return 注册成功的用户对象
     */
    User register(String phone, String nickname, String password);

    /**
     * 用户登录
     *
     * @param phone 手机号
     * @param password 密码
     * @return 登录成功的用户对象
     */
    User login(String phone, String password);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户对象
     */
    User getUserByPhone(String phone);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象
     */
    User getUserById(Long id);

    /**
     * 更新用户个人信息
     *
     * @param user 用户对象（包含需要更新的个人信息）
     * @return 更新后的用户对象
     */
    User updateUserProfile(User user);

    /**
     * 更新用户头像
     *
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 更新后的用户对象
     */
    User updateAvatar(Long userId, String avatarUrl);
}
