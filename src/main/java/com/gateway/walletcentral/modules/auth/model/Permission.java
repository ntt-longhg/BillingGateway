package com.gateway.walletcentral.modules.auth.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Entity
@Table(
    name = "permissions",
    comment = "Permission catalog - defines all available permissions in the system",
    indexes = {
        @Index(name = "uk_permissions_code", columnList = "code", unique = true),
        @Index(name = "idx_permissions_module", columnList = "module")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(name = "id", updatable = false, nullable = false, comment = "Primary key - UUID v7")
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 100, comment = "Permission code - unique identifier e.g. TENANT_VIEW")
    private String code;

    @Column(name = "module", nullable = false, length = 50, comment = "Module this permission belongs to")
    private String module;

    @Column(name = "description", length = 255, comment = "Permission description")
    private String description;
}
