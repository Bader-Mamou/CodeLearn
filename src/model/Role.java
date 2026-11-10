package model;

/** The three demo roles supported by the prototype. */
public enum Role {
    STUDENT("Apprenant", "student", "Badreddine"),
    TEACHER("Enseignant", "teacher", "Sara"),
    ADMIN("Administrateur", "admin", "Admin");

    private final String label;
    private final String key;
    private final String displayName;

    Role(String label, String key, String displayName) {
        this.label = label;
        this.key = key;
        this.displayName = displayName;
    }

    public String label() {
        return label;
    }

    public String key() {
        return key;
    }

    public String displayName() {
        return displayName;
    }

    public static Role fromKey(String key) {
        for (Role role : values()) {
            if (role.key.equals(key)) {
                return role;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return label;
    }
}
