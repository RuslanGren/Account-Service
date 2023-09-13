package com.example.accountservice.handlers;


import com.example.accountservice.service.LogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private LogService logService;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        logService.accessDeniedLog(request.getRemoteUser(), request.getRequestURI());
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
    }
}
