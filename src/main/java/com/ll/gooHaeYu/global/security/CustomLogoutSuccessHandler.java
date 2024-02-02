package com.ll.gooHaeYu.global.security;

import com.ll.gooHaeYu.standard.base.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        response.sendRedirect("/login");
    }
}