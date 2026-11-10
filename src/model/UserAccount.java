package model;

import java.util.List;

/**
 * An application account. Accounts are stored in memory by {@link AppState}
 * and are used by the login screen, the administrator tables and the teacher
 * workspace.
 */
public final class UserAccount {

    private final String username;
    private String password;
    private String name;
    private final Role role;
    private String status;
    private String language;
    private String track;
    private List<String> languages;

    public UserAccount(String username, String password, String name, Role role,
                       String status, String language, String track,
                       List<String> languages) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
        this.status = status;
        this.language = language;
        this.track = track;
        this.languages = List.copyOf(languages);
    }

    public static UserAccount student(String username, String password, String name,
                                      String language, String track) {
        return new UserAccount(username, password, name, Role.STUDENT,
                "Actif", language, track, List.of());
    }

    public static UserAccount teacher(String username, String password, String name,
                                      List<String> languages) {
        return new UserAccount(username, password, name, Role.TEACHER,
                "Actif", "fr", "java", languages);
    }

    public static UserAccount admin(String username, String password, String name) {
        return new UserAccount(username, password, name, Role.ADMIN,
                "Actif", "fr", "java", List.of());
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role role() {
        return role;
    }

    public String status() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean active() {
        return "Actif".equalsIgnoreCase(status);
    }

    public String language() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String track() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public List<String> languages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = List.copyOf(languages);
    }
}
