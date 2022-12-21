package com.bzk.blog.handler;

import com.alibaba.fastjson.JSON;
import com.bzk.blog.vo.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.bzk.blog.constant.CommonConst.APPLICATION_JSON;

/**
 * 登录失败处理
 */
@Component
public class AuthenticationFailHandlerImpl implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.setContentType(APPLICATION_JSON);
        httpServletResponse.getWriter().write(JSON.toJSONString(Result.fail(e.getMessage())));
    }

}
