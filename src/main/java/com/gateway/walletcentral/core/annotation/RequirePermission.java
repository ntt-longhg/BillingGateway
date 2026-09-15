package com.gateway.walletcentral.core.annotation;

import java.lang.annotation.*;

/**
 * Annotation to check user permission before method execution.
 * Applied on controller methods to enforce RBAC.
 *
 * Example: @RequirePermission("TENANT_VIEW")
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * Permission code(s) required. If multiple, user must have ALL.
     */
    String[] value();

    /**
     * If true, user needs ANY of the listed permissions (OR logic).
     * Default is ALL (AND logic).
     */
    boolean any() default false;
}
