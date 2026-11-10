package model;

import java.util.List;

/**
 * A teacher account created by the administrator, with the programming
 * languages that teacher is allowed to teach.
 */
public record TeacherAccount(String name, String username, List<String> languages) {

    public TeacherAccount {
        languages = List.copyOf(languages);
    }
}
