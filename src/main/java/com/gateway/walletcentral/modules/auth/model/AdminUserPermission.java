package com.gateway.walletcentral.modules.auth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "admin_user_permissions",
    comment = "User-specific permission overrides - persists across sessions",
    indexes = {
        @Index(name = "idx_admin_user_permissions_user", columnList = "admin_user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserPermission {

    @Id
    @Column(name = "admin_user_id", nullable = false, length = 36, comment = "FK to admin_users.id")
    private String adminUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false, comment = "FK to permissions.id")
    private Permission permission;

    @Column(name = "is_granted", nullable = false, comment = "true=grant permission, false=revoke permission")
    @Builder.Default
    private Boolean isGranted = true;
}
