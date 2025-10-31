package com.lititi.exams.web.handler;

import com.lititi.exams.commons2.log.LttLogger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 全局异常处理器
 * @author chenlong
 * date 2025-10-29
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler implements ErrorController {

    private static final LttLogger logger = new LttLogger(GlobalExceptionHandler.class);

    private static final String PATH = "/error";


    @ExceptionHandler(NoHandlerFoundException.class)
    public void handle404(NoHandlerFoundException ex,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        logger.warn("非法访问: " + request.getRequestURL());

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("非法访问！");
        writer.flush();
    }

    @RequestMapping("/notFound")
    public String handle404(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.warn("非法访问: " + request.getRequestURL());

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("非法访问！");
        writer.flush();
        return null;
    }

    @RequestMapping(PATH)
    public String error(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.warn("非法访问: " + request.getRequestURL());

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("非法访问！");
        writer.flush();
        return null;
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}































