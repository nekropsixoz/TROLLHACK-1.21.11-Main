package xks.accounts;

public enum Role {
    USER("User", 0),
    BETA("Beta", 1),
    PREMIUM("Premium", 2),
    DEV("Developer", 3),
    ADMINISTRATOR("Admin", 4);

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

    // Добавляем метод для преобразования строки в Role
    public static Role fromString(String roleName) {
        for (Role role : values()) {
            if (role.name().equalsIgnoreCase(roleName) ||
                    role.getName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        return USER; // Возвращаем USER по умолчанию
    }
}