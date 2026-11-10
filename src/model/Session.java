package model;

/**
 * An authenticated session. {@code username} identifies the account in
 * {@link AppState}; iteration convenience constructors keep the demo data
 * helpers simple.
 */
public record Session(Role role, String username, String displayName,
                      String language, String programmingLanguage) {

    public Session(Role role, String displayName, String language,
                   String programmingLanguage) {
        this(role, displayName, displayName, language, programmingLanguage);
    }

    public Session(Role role, String displayName, String language) {
        this(role, displayName, displayName, language, "java");
    }
}
