package com.lititi.exams.web.controller;

import com.lititi.exams.commons2.object.CommonResultObject;
import com.lititi.exams.commons2.utils.JwtUtil;
import com.lititi.exams.web.entity.User;
import com.lititi.exams.web.service.UserService;
import com.lititi.exams.web.service.FriendService;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器（登录注册）
 * @author chenlong
 * date 2025-10-31
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    /**
     * 用户注册
     * @param phone 手机号
     * @param nickname 昵称
     * @param password 密码
     * @return 注册结果
     */
    @PostMapping("/register")
    public CommonResultObject register(@RequestParam String phone,
                                       @RequestParam String nickname,
                                       @RequestParam String password) {
        CommonResultObject result = new CommonResultObject();

        try {
            // 参数校验
            if (phone == null || phone.trim().isEmpty()) {
                result.setResult(0);
                result.setMessage("手机号不能为空");
                return result;
            }

            if (nickname == null || nickname.trim().isEmpty()) {
                result.setResult(0);
                result.setMessage("昵称不能为空");
                return result;
            }

            if (password == null || password.trim().isEmpty()) {
                result.setResult(0);
                result.setMessage("密码不能为空");
                return result;
            }

            // 执行注册
            User user = userService.register(phone, nickname, password);

            result.setResult(1);
            result.setMessage("注册成功");
            result.addObject("user", user);
        } catch (Exception e) {
            result.setResult(0);
            result.setMessage("注册失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 用户登录
     * @param phone 手机号
     * @param password 密码
     * @return 登录结果和token
     */
    @PostMapping("/login")
    public CommonResultObject login(@RequestParam String phone,
                                    @RequestParam String password) {
        CommonResultObject result = new CommonResultObject();

        try {
            // 参数校验
            if (phone == null || phone.trim().isEmpty()) {
                result.setResult(0);
                result.setMessage("手机号不能为空");
                return result;
            }

            if (password == null || password.trim().isEmpty()) {
                result.setResult(0);
                result.setMessage("密码不能为空");
                return result;
            }

            // 执行登录
            User user = userService.login(phone, password);

            // 生成token
            String token = JwtUtil.generateToken(user.getId());

            result.setResult(1);
            result.setMessage("登录成功");
            result.addObject("user", user);
            result.addObject("token", token);
        } catch (Exception e) {
            result.setResult(0);
            result.setMessage("登录失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 添加好友
     * @param friendId 好友ID
     * @param request HTTP请求对象
     * @return 添加结果
     */
    @PostMapping("/friend/add")
    public CommonResultObject addFriend(@RequestParam Long friendId,
                                       HttpServletRequest request) {
        CommonResultObject result = new CommonResultObject();

        try {
            // 从请求中获取当前用户ID
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                result.setResult(0);
                result.setMessage("用户未登录");
                return result;
            }

            // 参数校验
            if (friendId == null) {
                result.setResult(0);
                result.setMessage("好友ID不能为空");
                return result;
            }

            // 调用服务层添加好友
            boolean success = friendService.addFriend(userId, friendId);

            if (success) {
                result.setResult(1);
                result.setMessage("添加好友成功");
            } else {
                result.setResult(0);
                result.setMessage("添加好友失败");
            }

        } catch (IllegalArgumentException e) {
            result.setResult(0);
            result.setMessage("参数错误: " + e.getMessage());
        } catch (IllegalStateException e) {
            result.setResult(0);
            result.setMessage("操作失败: " + e.getMessage());
        } catch (Exception e) {
            result.setResult(0);
            result.setMessage("添加好友失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 删除好友
     * @param friendId 好友ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    @PostMapping("/friend/delete")
    public CommonResultObject deleteFriend(@RequestParam Long friendId,
                                          HttpServletRequest request) {
        CommonResultObject result = new CommonResultObject();

        try {
            // 从请求中获取当前用户ID
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                result.setResult(0);
                result.setMessage("用户未登录");
                return result;
            }

            // 参数校验
            if (friendId == null) {
                result.setResult(0);
                result.setMessage("好友ID不能为空");
                return result;
            }

            // 调用服务层删除好友
            boolean success = friendService.deleteFriend(userId, friendId);

            if (success) {
                result.setResult(1);
                result.setMessage("删除好友成功");
            } else {
                result.setResult(0);
                result.setMessage("删除好友失败");
            }

        } catch (IllegalArgumentException e) {
            result.setResult(0);
            result.setMessage("参数错误: " + e.getMessage());
        } catch (IllegalStateException e) {
            result.setResult(0);
            result.setMessage("操作失败: " + e.getMessage());
        } catch (Exception e) {
            result.setResult(0);
            result.setMessage("删除好友失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 获取好友列表
     * @param request HTTP请求对象
     * @return 好友列表
     */
    @GetMapping("/friend/list")
    public CommonResultObject getFriendList(HttpServletRequest request) {
        CommonResultObject result = new CommonResultObject();

        try {
            // 从请求中获取当前用户ID
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                result.setResult(0);
                result.setMessage("用户未登录");
                return result;
            }

            // 调用服务层获取好友列表
            List<Long> friendIds = friendService.getFriendIds(userId);

            result.setResult(1);
            result.setMessage("获取好友列表成功");
            result.addObject("friendIds", friendIds);
            result.addObject("friendCount", friendIds.size());

        } catch (Exception e) {
            result.setResult(0);
            result.setMessage("获取好友列表失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 获取用户个人信息
     * @param request HTTP请求对象
     * @return 用户个人信息
     */
    @GetMapping("/profile")
    public CommonResultObject getUserProfile(HttpServletRequest request) {
        CommonResultObject result = new CommonResultObject();

        try {
            // 从请求中获取当前用户ID
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                result.setResult(0);
                result.setMessage("用户未登录");
                return result;
            }

            // 调用服务层获取用户信息
            User user = userService.getUserById(userId);
            if (user != null) {
                // 清除密码信息
                user.setPassword(null);
                result.setResult(1);
                result.setMessage("获取用户信息成功");
                result.addObject("user", user);
            } else {
                result.setResult(0);
                result.setMessage("用户不存在");
            }

        } catch (Exception e) {
            result.setResult(0);
            result.setMessage("获取用户信息失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 更新用户个人信息
     * @param user 用户对象（包含需要更新的个人信息）
     * @param request HTTP请求对象
     * @return 更新结果
     */
    @PostMapping("/profile/update")
    public CommonResultObject updateUserProfile(@RequestBody User user, HttpServletRequest request) {
        CommonResultObject result = new CommonResultObject();

        try {
            // 从请求中获取当前用户ID
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                result.setResult(0);
                result.setMessage("用户未登录");
                return result;
            }

            // 设置用户ID
            user.setId(userId);

            // 调用服务层更新用户信息
            User updatedUser = userService.updateUserProfile(user);

            result.setResult(1);
            result.setMessage("更新个人信息成功");
            result.addObject("user", updatedUser);

        } catch (IllegalArgumentException e) {
            result.setResult(0);
            result.setMessage("参数错误: " + e.getMessage());
        } catch (Exception e) {
            result.setResult(0);
            result.setMessage("更新个人信息失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 更新用户头像
     * @param avatarUrl 头像URL
     * @param request HTTP请求对象
     * @return 更新结果
     */
    @PostMapping("/avatar/update")
    public CommonResultObject updateAvatar(@RequestParam String avatarUrl, HttpServletRequest request) {
        CommonResultObject result = new CommonResultObject();

        try {
            // 从请求中获取当前用户ID
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                result.setResult(0);
                result.setMessage("用户未登录");
                return result;
            }

            // 参数校验
            if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
                result.setResult(0);
                result.setMessage("头像URL不能为空");
                return result;
            }

            // 调用服务层更新头像
            User updatedUser = userService.updateAvatar(userId, avatarUrl);

            result.setResult(1);
            result.setMessage("更新头像成功");
            result.addObject("user", updatedUser);

        } catch (IllegalArgumentException e) {
            result.setResult(0);
            result.setMessage("参数错误: " + e.getMessage());
        } catch (Exception e) {
            result.setResult(0);
            result.setMessage("更新头像失败: " + e.getMessage());
        }

        return result;
    }
}
