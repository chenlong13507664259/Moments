package com.lititi.exams.web.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.lititi.exams.commons2.enumeration.RedirectPageType;
import com.lititi.exams.commons2.exception.LttException;
import com.lititi.exams.commons2.log.LttLogger;
import com.lititi.exams.commons2.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import static com.lititi.exams.commons2.constant.Constant.TOKEN_HEADER;
import static com.lititi.exams.commons2.enumeration.ExceptionCode.SERVER_FAILURE;
import static com.lititi.exams.commons2.result.JsonCodeEnum.NOT_LOGIN;

/**
 * 登录拦截器
 * @author chenlong
 * date 2025-10-29
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final LttLogger logger = new LttLogger(LoginInterceptor.class);

    private static final List<String> excludeUri = new ArrayList<>();
    private static final List<String> needTokenUri = new ArrayList<>();

    static {
        // 登录注册接口不需要token验证
        excludeUri.add("/user/login");
        excludeUri.add("/user/register");

        // 需要token验证的接口
        needTokenUri.add("/content/publish");
        needTokenUri.add("/content/list");
        needTokenUri.add("/user/friend/add");
        needTokenUri.add("/user/friend/delete");
        needTokenUri.add("/user/friend/list");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        String uri = request.getRequestURI();
        String token = request.getHeader(TOKEN_HEADER);

        logger.info("请求URI: " + uri + ", Token: " + token);

        // 检查是否是排除的URI（如登录、注册）
        if (excludeUri.contains(uri)) {
            logger.info("放行无需验证的请求: " + uri);
            return true; // 直接放行
        }

        // 检查是否是需要token的URI
        if (needTokenUri.contains(uri)) {
            // 如果有token，则验证token并放行
            if (!StringUtils.isEmpty(token)) {
                // 验证token有效性
                if (!JwtUtil.validateToken(token)) {
                    logger.warn("Token无效: " + token);
                    returnIllegalAccessResponse(response);
                    return false;
                }

                // 将用户ID存入request属性供后续使用
                Long userId = JwtUtil.parseToken(token);
                if (userId != null) {
                    request.setAttribute("userId", userId);
                    logger.info("Token验证通过，用户ID: " + userId);
                    return true; // token有效，正常放行
                } else {
                    logger.warn("无法解析Token中的用户ID: " + token);
                    returnIllegalAccessResponse(response);
                    return false;
                }
            }

            // 没有token，返回非法访问
            logger.warn("请求缺少Token: " + uri);
            returnIllegalAccessResponse(response);
            return false;
        }

        // 不在允许列表中的接口，返回非法访问
        logger.warn("请求的接口不在允许列表中: " + uri);
        returnIllegalAccessResponse(response);
        return false;
    }

    /**
     * 返回非法访问响应（JSON格式）
     */
    private void returnIllegalAccessResponse(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403状态码

        JSONObject res = new JSONObject();
        res.put("result", 0);
        res.put("message", "非法访问！");
        res.put("redirectPageType", RedirectPageType.NORMAL.getCode());

        try (PrintWriter out = response.getWriter()) {
            out.write(res.toString());
            out.flush();
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
