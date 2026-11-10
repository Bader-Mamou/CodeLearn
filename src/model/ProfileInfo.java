package model;

/** Profile information for the student. */
public record ProfileInfo(String name, String role, String path, String currentLevel,
                          String language, String track) {

    public ProfileInfo(String name, String role, String path, String currentLevel) {
        this(name, role, path, currentLevel, "fr", "java");
    }
}
