package com.gateway.walletcentral.modules.auth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "admin_user_roles",
    comment = "Maps admin users to roles - one role per user, persists across sessions"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserRole {

    @Id
    @Column(name = "admin_user_id", nullable = false, length = 36, comment = "FK to admin_users.id")
    private String adminUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, comment = "FK to roles.id")
    private Role role;
}
