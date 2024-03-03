package tech.orbfin.api.gateway.model.user;

public enum Role {
    ADMINISTRATOR,
    MANAGER,
    EDITOR,
    CONTRIBUTOR,
    SUBSCRIBER,
    USER;

    public static Role mapToRoleEnum(String roleString) {
        if (roleString == null) {
            return null;
        }

        String lowercaseRoleString = roleString.toLowerCase();
        for (Role roleEnum : Role.values()) {
            if (lowercaseRoleString.contains(roleEnum.name().toLowerCase())) {
                return roleEnum;
            }
        }
        return null;
    }
}