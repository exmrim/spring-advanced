package org.example.expert.domain.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String requestUri = request.getRequestURI();
        String requestTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("URI: " + requestUri);
        log.info("RequestTime: " + requestTime);

        String roleName = request.getHeader("userRole");
        if (roleName == null) {
            throw new InvalidRequestException("userRole 헤더가 없습니다.");
        }

        UserRole role = UserRole.valueOf(roleName);

        return true;
    }
}
