package com.lititi.exams.web.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.lititi.exams.commons2.enumeration.RedirectPageType;
import com.lititi.exams.commons2.exception.LttException;
import com.lititi.exams.commons2.log.LttLogger;
import com.lititi.exams.commons2.utils.JwtUtil;
import org.apache.commons.collections4.CollectionUtils;
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

    private static final List<String> allExcludeUri = new ArrayList<>();// 不进行任何判断，直接放行
    private static final List<String> needTokenUri = new ArrayList<>();// 请求头需要带token，才能放行

    static {
        // 不进行任何判断，直接放行（两个指定的链接）
        allExcludeUri.add("/content/publish");
        allExcludeUri.add("/content/list");

        // 添加登录注册相关接口
        allExcludeUri.add("/user/login");
        allExcludeUri.add("/user/register");

        // 添加好友相关接口到需要token验证的列表
        needTokenUri.add("/friend/add");
        needTokenUri.add("/friend/delete");
        needTokenUri.add("/friend/list");

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String token = request.getHeader(TOKEN_HEADER);

        if(StringUtils.isEmpty(uri)){
            returnIllegalAccessResponse(response);
            return false;
        }

        // 检查是否是允许的两个指定链接
        if (isAllowedRequest(request)) {
            logger.info("放行指定链接: " + method + " " + uri);
            return true;
        }

        // 对于其他请求，检查是否以/content开头
        if(!uri.toLowerCase().startsWith("/content")){
            returnIllegalAccessResponse(response);
            return false;
        }

        // 不进行任何判断，直接放行（主要针对两个指定链接）
        if (checkUri(uri, allExcludeUri)) {
            return true;
        }

        // 需要token验证的URI
        if(checkUri(uri, needTokenUri)){
            if(StringUtils.isEmpty(token)){
                returnNotLoginJSONObject(request, response);
                return false;
            }

            // 验证token有效性
            if(!JwtUtil.validateToken(token)) {
                returnNotLoginJSONObject(request, response);
                return false;
            }

            // 可以将用户ID存入request属性供后续使用
            Long userId = JwtUtil.parseToken(token);
            request.setAttribute("userId", userId);
        }

        // 其他所有请求都返回非法访问
        returnIllegalAccessResponse(response);
        return false;
    }

    /**
     * 检查是否是允许的两个指定请求
     */
    private boolean isAllowedRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString();

        // 检查 /content/publish (只允许POST方法)
        if ("/content/publish".equals(uri)) {
            return "POST".equalsIgnoreCase(method);
        }

        // 检查 /content/list (只需要userId参数)
        if ("/content/list".equals(uri)) {
            return hasUserIdParam(queryString);
        }

        // 检查登录注册接口
        if ("/user/login".equals(uri) || "/user/register".equals(uri)) {
            return "POST".equalsIgnoreCase(method);
        }

        return false;
    }

    /**
     * 检查 /content/list 请求是否包含userId参数
     */
    private boolean hasUserIdParam(String queryString) {
        if (StringUtils.isBlank(queryString)) {
            return false;
        }

        // 解析查询参数
        String[] params = queryString.split("&");

        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];

                if ("userId".equalsIgnoreCase(key) && StringUtils.isNotBlank(value)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 返回非法访问响应
     */
    private void returnIllegalAccessResponse(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        try (PrintWriter out = response.getWriter()) {
            out.write("非法访问！");
            out.flush();
        }
    }

    private boolean checkUri(String uri, List<String> uriList) {
        if (StringUtils.isEmpty(uri) || CollectionUtils.isEmpty(uriList)) {
            return false;
        }

        String url1 = uri.toLowerCase();
        for (String excludeUrl : uriList) {
            if (!StringUtils.isEmpty(excludeUrl) && url1.startsWith(excludeUrl.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void returnNotLoginJSONObject(HttpServletRequest request, HttpServletResponse response) {
        // 清空session
        request.getSession().invalidate();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject res = new JSONObject();
        res.put("redirectPageType", RedirectPageType.LOGIN.getCode());
        res.put("result", 0);
        res.put("message", NOT_LOGIN.getMessage());
        try (PrintWriter out = response.getWriter()) {
            out.append(res.toString());
        } catch (IOException e) {
            throw new LttException(SERVER_FAILURE);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
