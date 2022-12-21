package com.bzk.blog.handler;

import com.alibaba.fastjson.JSON;

import com.bzk.blog.vo.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.bzk.blog.constant.CommonConst.APPLICATION_JSON;

/**
 * 用户权限处理
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
        httpServletResponse.setContentType(APPLICATION_JSON);
        httpServletResponse.getWriter().write(JSON.toJSONString(Result.fail("权限不足")));
    }

}
