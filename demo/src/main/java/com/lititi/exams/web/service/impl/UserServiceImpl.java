package com.lititi.exams.web.service.impl;

import com.lititi.exams.web.dao.UserMapper;
import com.lititi.exams.web.entity.User;
import com.lititi.exams.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * 用户服务实现类
 * @author chenlong
 * date 2025-10-31
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User register(String phone, String nickname, String password) {
        // 检查手机号是否已被注册
        User existingUser = userMapper.selectByPhone(phone);
        if (existingUser != null) {
            throw new RuntimeException("该手机号已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setPhone(phone);
        user.setNickname(nickname);
        user.setAvatar(""); // 默认头像为空

        // 在实际项目中应该对密码进行加密，这里简化处理
        // 可以使用BCryptPasswordEncoder等加密方式
        user.setPassword(password);

        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        // 插入数据库
        userMapper.insert(user);
        // 清除密码信息再返回
        user.setPassword(null);
        return user;
    }

    @Override
    public User login(String phone, String password) {
        // 根据手机号查询用户
        User user = userMapper.selectByPhone(phone);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        // 实际项目中应该使用BCryptPasswordEncoder匹配密码
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 清除密码信息再返回
        user.setPassword(null);
        return user;
    }

    @Override
    public User getUserByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User updateUserProfile(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 查询原用户信息
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新个人信息字段（只更新非空字段）
        if (user.getNickname() != null && !user.getNickname().trim().isEmpty()) {
            existingUser.setNickname(user.getNickname());
        }
        if (user.getAvatar() != null && !user.getAvatar().trim().isEmpty()) {
            existingUser.setAvatar(user.getAvatar());
        }

        // 更新时间
        existingUser.setUpdateTime(new Date());

        // 执行更新
        userMapper.update(existingUser);

        // 清除密码信息再返回
        existingUser.setPassword(null);
        return existingUser;
    }

    @Override
    public User updateAvatar(Long userId, String avatarUrl) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 查询原用户信息
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新头像
        existingUser.setAvatar(avatarUrl);
        existingUser.setUpdateTime(new Date());

        // 执行更新
        userMapper.update(existingUser);

        // 清除密码信息再返回
        existingUser.setPassword(null);
        return existingUser;
    }
}
