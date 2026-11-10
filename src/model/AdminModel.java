package model;

import java.util.ArrayList;
import java.util.List;

/** Administrator workspace data, backed by the shared {@link AppState}. */
public final class AdminModel {

    private static final String DEFAULT_TEACHER_PASSWORD = "1234";

    private final AppState state;
    private final Session session;

    public AdminModel(AppState state) {
        this(state, null);
    }

    public AdminModel(AppState state, Session session) {
        this.state = state;
        this.session = session;
    }

    public AdminSummary summary() {
        int teacherCount = 0;
        int studentCount = 0;
        int adminCount = 0;
        for (UserAccount user : state.users()) {
            switch (user.role()) {
                case TEACHER -> teacherCount++;
                case STUDENT -> studentCount++;
                default -> adminCount++;
            }
        }
        return new AdminSummary(name(),
                List.of(
                        new StatInfo("Utilisateurs", String.valueOf(state.users().size()), Tone.PURPLE),
                        new StatInfo("Enseignants", String.valueOf(teacherCount), Tone.GREEN),
                        new StatInfo("Apprenants", String.valueOf(studentCount), Tone.AMBER),
                        new StatInfo("Administrateurs", String.valueOf(adminCount), Tone.RED)),
                users(), systemStatuses(), state.availableProgrammingLanguages());
    }

    private String name() {
        if (session == null) {
            return "Admin";
        }
        UserAccount account = state.user(session.username());
        return account == null ? session.displayName() : account.name();
    }

    public List<UserInfo> users() {
        List<UserInfo> users = new ArrayList<>();
        for (UserAccount account : state.users()) {
            users.add(new UserInfo(account.name(), account.username(),
                    roleLabel(account.role()), account.status()));
        }
        return users;
    }

    private String roleLabel(Role role) {
        return switch (role) {
            case TEACHER -> "Enseignant";
            case ADMIN -> "Administrateur";
            default -> "Apprenant";
        };
    }

    public List<UserAccount> accounts() {
        return state.users();
    }

    public UserAccount account(String username) {
        return state.user(username);
    }

    /** Teachers created by the administrator, with their assigned languages. */
    public List<TeacherAccount> teachers() {
        return state.teachers();
    }

    /** All programming languages the administrator may assign to a teacher. */
    public List<LanguageInfo> programmingLanguages() {
        return ProgrammingLanguages.all();
    }

    public String addUser(String name, String username, String password, Role role,
                          String track, List<String> languages) {
        return state.addUser(name, username, password, role, track, languages);
    }

    public String updateUser(String username, String name, String password, String status,
                             String track, List<String> languages) {
        return state.updateUser(username, name, password, status, track, languages);
    }

    public String deleteUser(String username) {
        return state.deleteUser(username);
    }

    /* ------------------------------------------------------------- teachers */

    public String addTeacher(String name, String username, List<String> languages) {
        return state.addUser(name, username, DEFAULT_TEACHER_PASSWORD, Role.TEACHER,
                "java", languages);
    }

    public String updateTeacher(String username, String name, List<String> languages) {
        UserAccount user = state.user(username);
        if (user == null || user.role() != Role.TEACHER) {
            return "Enseignant introuvable.";
        }
        return state.updateUser(username, name, null, user.status(), null, languages);
    }

    public String deleteTeacher(String username) {
        UserAccount user = state.user(username);
        if (user == null || user.role() != Role.TEACHER) {
            return "Enseignant introuvable.";
        }
        return state.deleteUser(username);
    }

    public String teacherDefaultPassword() {
        return DEFAULT_TEACHER_PASSWORD;
    }

    public List<RoleInfo> roles() {
        return List.of(
                new RoleInfo("Apprenant",
                        "Acc\u00e8s aux cours, exercices, tests et progression.", Tone.PURPLE),
                new RoleInfo("Enseignant",
                        "Gestion des cours, des niveaux, des \u00e9valuations et du suivi.", Tone.GREEN),
                new RoleInfo("Administrateur",
                        "Gestion des utilisateurs, des r\u00f4les et de la supervision.", Tone.AMBER));
    }

    public List<SystemStatus> systemStatuses() {
        long active = state.users().stream().filter(UserAccount::active).count();
        List<String> languages = state.availableProgrammingLanguages().stream()
                .map(LanguageInfo::label).toList();
        return List.of(
                new SystemStatus("Comptes actifs", active + " / " + state.users().size(), Tone.GREEN),
                new SystemStatus("Cours disponibles", String.valueOf(state.courses().size()), Tone.GREEN),
                new SystemStatus("Langages enseign\u00e9s",
                        languages.isEmpty() ? "Aucun" : String.join(", ", languages), Tone.AMBER));
    }
}
