package com.lititi.exams.web.controller;

import com.lititi.exams.commons2.object.CommonResultObject;
import com.lititi.exams.commons2.utils.JwtUtil;
import com.lititi.exams.web.entity.User;
import com.lititi.exams.web.service.UserService;
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
}
