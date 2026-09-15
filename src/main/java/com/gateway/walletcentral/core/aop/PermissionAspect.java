package com.gateway.walletcentral.core.aop;

import com.gateway.walletcentral.config.SecurityConfig;
import com.gateway.walletcentral.core.annotation.RequirePermission;
import com.gateway.walletcentral.core.exception.BusinessException;
import com.gateway.walletcentral.modules.auth.service.RbacService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Set;

/**
 * AOP aspect that enforces @RequirePermission annotation.
 * Checks user permissions from the authenticated email (set by SecurityConfig filter).
 */
@Aspect
@Component
public class PermissionAspect {

    private static final Logger log = LoggerFactory.getLogger(PermissionAspect.class);

    private final RbacService rbacService;

    public PermissionAspect(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        String email = extractEmail();
        if (email == null) {
            throw new BusinessException("UNAUTHORIZED", "Authentication required");
        }

        String[] requiredPermissions = requirePermission.value();
        Set<String> userPermissions = rbacService.getPermissionsByEmail(email);

        if (requirePermission.any()) {
            // OR logic - user needs ANY of the permissions
            boolean hasAny = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
            if (!hasAny) {
                log.warn("Permission denied for user {}: required ANY of {}",
                        email, Arrays.toString(requiredPermissions));
                throw new BusinessException("FORBIDDEN",
                        "Required permission: " + String.join(" OR ", requiredPermissions));
            }
        } else {
            // AND logic - user needs ALL permissions
            for (String permission : requiredPermissions) {
                if (!userPermissions.contains(permission)) {
                    log.warn("Permission denied for user {}: missing {}",
                            email, permission);
                    throw new BusinessException("FORBIDDEN",
                            "Required permission: " + permission);
                }
            }
        }
    }

    private String extractEmail() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        HttpServletRequest request = attrs.getRequest();
        return (String) request.getAttribute(SecurityConfig.REQUEST_ATTR_EMAIL);
    }
}
