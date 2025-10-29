package com.lititi.exams.web.controller;

import com.lititi.exams.commons2.object.CommonResultObject;
import com.lititi.exams.commons2.object.PageParam;
import com.lititi.exams.web.entity.Moment;
import com.lititi.exams.web.service.MomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 动态控制器
 * @author chenlong
 * date 2025-10-29
 */
@RestController
@RequestMapping("/content")
public class MomentController extends BaseController {

    @Autowired
    private MomentService momentService;

    /**
     * 发布动态（POST请求）
     * @param moment 动态对象
     * @return 发布结果
     */
    @PostMapping("/publish")
    public CommonResultObject publishMoment(@RequestBody Moment moment) {
        CommonResultObject result = new CommonResultObject();

        try {
            boolean success = momentService.publishMoment(moment);
            if (success) {
                result.setResult(1);
                result.setMessage("动态发布成功");
                result.addObject("details", "您的动态已成功发布至朋友圈");
            } else {
                result.setResult(0);
                result.setMessage("动态发布失败");
                result.addObject("details", "发布失败，请稍后重试");
            }
        } catch (Exception e) {
            result.setResult(0);
            result.setMessage("动态发布异常：" + e.getMessage());
            result.addObject("details", "发布过程中发生异常");
        }

        return result;
    }

    /**
     * 查看朋友圈动态（GET请求）
     * @param userId 用户ID
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 朋友圈动态列表
     */
    @GetMapping("/list")
    public CommonResultObject getFriendMoments(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        CommonResultObject result = new CommonResultObject();
        try {
            PageParam pageParam = new PageParam();
            pageParam.setPageNo(pageNo);
            pageParam.setPageSize(pageSize);

            List<Moment> moments = momentService.getFriendMoments(userId, pageParam);

            // 处理时间格式
            for (Moment moment : moments) {
                // 正确处理时间，确保序列化时使用正确的格式
            }

            result.setResult(1);
            result.setMessage("查询朋友圈动态成功");
            result.addObject("moments", moments);
            result.addObject("pageNo", pageNo);
            result.addObject("pageSize", pageSize);
            result.addObject("details", "已获取最新的朋友圈动态");
        } catch (Exception e) {
            result.setResult(0);
            result.setMessage("查询朋友圈动态异常：" + e.getMessage());
            result.addObject("details", "查询过程中发生异常");
        }

        return result;
    }
}
