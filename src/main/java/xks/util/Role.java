package xks.util;

public enum Role {
    USER("User", 0),
    BETA("Beta", 1),
    PREMIUM("Premium", 2),
    DEV("Developer", 3),
    YT("YouTuber", 4),
    ADMINISTRATOR("Admin", 5),
    OWNER("Owner", 6);

    private final String name;
    private final int hierarchy;

    Role(String name, int hierarchy) {
        this.name = name;
        this.hierarchy = hierarchy;
    }

    public String getName() {
        return name;
    }

    public int getHierarchy() {
        return hierarchy;
    }

    public boolean hasAccess(Role requiredRole) {
        return this.hierarchy >= requiredRole.hierarchy;
    }

    //       Role
    public static Role fromString(String roleName) {
        for (Role role : values()) {
            if (role.name().equalsIgnoreCase(roleName) ||
                    role.getName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        return USER; //  USER  
    }
}