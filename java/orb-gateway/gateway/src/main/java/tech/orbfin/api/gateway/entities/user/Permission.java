package tech.orbfin.api.gateway.entities.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    MANAGER_READ("management:read"),
    MANAGER_UPDATE("management:update"),
    MANAGER_CREATE("management:create"),
    MANAGER_DELETE("management:delete"),
    EDITOR_READ("editor:read"),
    EDITOR_UPDATE("editor:update"),
    EDITOR_CREATE("editor:create"),
    EDITOR_DELETE("editor:delete"),
    CONTRIBUTOR_READ("contributor:read"),
    CONTRIBUTOR_UPDATE("contributor:update"),
    CONTRIBUTOR_CREATE("contributor:create"),
    SUBSCRIBER_READ("subscriber:read"),
    USER_READ("user:read");

    private final String permission;
}