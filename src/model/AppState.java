package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Application-level model shared by the controllers. */
public final class AppState {

    private final List<UserAccount> users = new ArrayList<>();
    private final List<CourseItem> courses = new ArrayList<>();
    private final Map<String, TestQuestion> questions = new HashMap<>();
    private final Map<String, StudentProgress> progress = new HashMap<>();

    private Session session;

    public AppState() {
        seedUsers();
        seedCourses();
        seedQuestions();
        seedProgress();
    }

    /* --------------------------------------------------------------- session */

    public Session session() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void clearSession() {
        this.session = null;
    }

    /* ----------------------------------------------------------------- users */

    public List<UserAccount> users() {
        return List.copyOf(users);
    }

    public List<UserAccount> students() {
        return users.stream().filter(user -> user.role() == Role.STUDENT).toList();
    }

    public UserAccount user(String username) {
        for (UserAccount user : users) {
            if (user.username().equalsIgnoreCase(username == null ? "" : username)) {
                return user;
            }
        }
        return null;
    }

    public Session authenticate(String username, String password, Role role) {
        UserAccount user = user(username);
        if (user == null || !user.active()) {
            return null;
        }
        if (role != null && user.role() != role) {
            return null;
        }
        if (password == null || !user.password().equals(password)) {
            return null;
        }
        return new Session(user.role(), user.username(), user.name(),
                user.language(), user.track());
    }

    /** Adds an account, returning an error message or {@code null} on success. */
    public String addUser(String name, String username, String password, Role role,
                          String track, List<String> languages) {
        String trimmedName = name == null ? "" : name.trim();
        String trimmedUsername = username == null ? "" : username.trim();
        if (trimmedName.isEmpty()) {
            return "Le nom est obligatoire.";
        }
        if (trimmedUsername.isEmpty()) {
            return "L'identifiant est obligatoire.";
        }
        if (user(trimmedUsername) != null) {
            return "L'identifiant \u00ab " + trimmedUsername + " \u00bb est d\u00e9j\u00e0 utilis\u00e9.";
        }
        if (password == null || password.isBlank()) {
            return "Le mot de passe est obligatoire.";
        }
        if (role == null) {
            return "Le r\u00f4le est obligatoire.";
        }
        if (role == Role.TEACHER && (languages == null || languages.isEmpty())) {
            return "S\u00e9lectionnez au moins un langage enseign\u00e9.";
        }

        UserAccount account = new UserAccount(trimmedUsername, password, trimmedName, role,
                "Actif", "fr", track == null || track.isBlank() ? "java" : track,
                languages == null ? List.of() : languages);
        users.add(account);
        return null;
    }

    /** Updates an existing account; role and username are immutable. */
    public String updateUser(String username, String name, String password,
                             String status, String track, List<String> languages) {
        UserAccount user = user(username);
        if (user == null) {
            return "Utilisateur introuvable.";
        }
        String trimmedName = name == null ? "" : name.trim();
        if (trimmedName.isEmpty()) {
            return "Le nom est obligatoire.";
        }
        if (user.role() == Role.TEACHER && (languages == null || languages.isEmpty())) {
            return "S\u00e9lectionnez au moins un langage enseign\u00e9.";
        }
        user.setName(trimmedName);
        if (password != null && !password.isBlank()) {
            user.setPassword(password);
        }
        user.setStatus(status == null || status.isBlank() ? "Actif" : status);
        if (track != null && !track.isBlank()) {
            user.setTrack(track);
        }
        if (user.role() == Role.TEACHER) {
            user.setLanguages(languages);
        }
        return null;
    }

    public String deleteUser(String username) {
        UserAccount user = user(username);
        if (user == null) {
            return "Utilisateur introuvable.";
        }
        if (user.role() == Role.ADMIN && adminCount() <= 1) {
            return "Impossible de supprimer le dernier administrateur.";
        }
        users.remove(user);
        progress.remove(user.username());
        return null;
    }

    private long adminCount() {
        return users.stream().filter(user -> user.role() == Role.ADMIN).count();
    }

    /** Teachers created by the administrator, with their assigned languages. */
    public List<TeacherAccount> teachers() {
        List<TeacherAccount> teachers = new ArrayList<>();
        for (UserAccount user : users) {
            if (user.role() == Role.TEACHER) {
                teachers.add(new TeacherAccount(user.name(), user.username(), user.languages()));
            }
        }
        return teachers;
    }

    /**
     * Programming languages offered to students: the union of every language
     * assigned to a teacher, in the catalogue order.
     */
    public List<LanguageInfo> availableProgrammingLanguages() {
        Set<String> taught = new LinkedHashSet<>();
        for (UserAccount user : users) {
            if (user.role() == Role.TEACHER) {
                taught.addAll(user.languages());
            }
        }

        List<LanguageInfo> available = new ArrayList<>();
        for (LanguageInfo language : ProgrammingLanguages.all()) {
            if (taught.contains(language.code())) {
                available.add(language);
            }
        }
        return available;
    }

    /* --------------------------------------------------------------- courses */

    public List<CourseItem> courses() {
        return List.copyOf(courses);
    }

    public List<CourseItem> coursesFor(Collection<String> tracks) {
        return courses.stream().filter(course -> tracks.contains(course.track())).toList();
    }

    public CourseItem course(String id) {
        for (CourseItem course : courses) {
            if (course.id().equals(id)) {
                return course;
            }
        }
        return null;
    }

    public String addCourse(String track, String title, String description) {
        String error = validateCourse(track, title, description);
        if (error != null) {
            return error;
        }
        courses.add(new CourseItem(UUID.randomUUID().toString(), track,
                title.trim(), description.trim()));
        return null;
    }

    public String updateCourse(String id, String track, String title, String description) {
        CourseItem existing = course(id);
        if (existing == null) {
            return "Cours introuvable.";
        }
        String error = validateCourse(track, title, description);
        if (error != null) {
            return error;
        }
        int index = courses.indexOf(existing);
        courses.set(index, new CourseItem(id, track, title.trim(), description.trim()));
        return null;
    }

    public String deleteCourse(String id) {
        CourseItem existing = course(id);
        if (existing == null) {
            return "Cours introuvable.";
        }
        courses.remove(existing);
        return null;
    }

    private String validateCourse(String track, String title, String description) {
        if (track == null || ProgrammingLanguages.find(track) == null) {
            return "Choisissez un langage valide.";
        }
        if (title == null || title.isBlank()) {
            return "Le titre du cours est obligatoire.";
        }
        if (description == null || description.isBlank()) {
            return "La description du cours est obligatoire.";
        }
        return null;
    }

    /* ----------------------------------------------------------------- tests */

    public TestQuestion question(String track) {
        return questions.getOrDefault(track, questions.get("java"));
    }

    public void setQuestion(String track, TestQuestion question) {
        questions.put(track, question);
    }

    /* -------------------------------------------------------------- progress */

    public StudentProgress progress(String username) {
        return progress.computeIfAbsent(username, key -> new StudentProgress());
    }

    /* ----------------------------------------------------------------- seeds */

    private void seedUsers() {
        users.add(UserAccount.student("etudiant", "1234", "Badreddine", "fr", "java"));
        users.add(UserAccount.student("amine01", "1234", "Amine", "fr", "python"));
        users.add(UserAccount.teacher("sara02", "1234", "Sara", List.of("java", "python")));
        users.add(UserAccount.teacher("karim03", "1234", "Karim", List.of("javascript")));
        users.add(UserAccount.admin("admin", "1234", "Admin"));
    }

    private void seedCourses() {
        for (LanguageInfo language : ProgrammingLanguages.all()) {
            String code = language.code();
            String label = language.label();
            switch (code) {
                case "python" -> {
                    courses.add(new CourseItem(code + "-1", code, "Introduction \u00e0 " + label,
                            "Variables, types et affichage avec print()."));
                    courses.add(new CourseItem(code + "-2", code, "Conditions et boucles",
                            "if, elif, else, for et while."));
                    courses.add(new CourseItem(code + "-3", code, "Programmation orient\u00e9e objet",
                            "Classes, objets et h\u00e9ritage."));
                }
                case "javascript" -> {
                    courses.add(new CourseItem(code + "-1", code, "Introduction \u00e0 " + label,
                            "Variables, types et console.log()."));
                    courses.add(new CourseItem(code + "-2", code, "Conditions et boucles",
                            "if, switch, for et while."));
                    courses.add(new CourseItem(code + "-3", code, "Objets et prototypes",
                            "Objets, fonctions et cha\u00eenes de prototypes."));
                }
                case "cpp" -> {
                    courses.add(new CourseItem(code + "-1", code, "Introduction \u00e0 " + label,
                            "Variables, types et flux iostream."));
                    courses.add(new CourseItem(code + "-2", code, "Conditions et boucles",
                            "if, switch, for et while."));
                    courses.add(new CourseItem(code + "-3", code, "Classes et h\u00e9ritage",
                            "Classes, objets et h\u00e9ritage en C++."));
                }
                default -> {
                    courses.add(new CourseItem(code + "-1", code, "Introduction \u00e0 " + label,
                            "Variables, types et System.out.println()."));
                    courses.add(new CourseItem(code + "-2", code, "Conditions et boucles",
                            "if, switch, for et while."));
                    courses.add(new CourseItem(code + "-3", code, "Programmation orient\u00e9e objet",
                            "Classes, objets et h\u00e9ritage."));
                }
            }
        }
    }

    private void seedQuestions() {
        questions.put("java", new TestQuestion(
                "Quelle instruction affiche Hello en Java ?",
                "Une seule r\u00e9ponse correcte.",
                List.of("System.out.println(\"Hello\");",
                        "print(\"Hello\");",
                        "console.log(\"Hello\");"), 0));
        questions.put("python", new TestQuestion(
                "Quelle instruction affiche Hello en Python ?",
                "Une seule r\u00e9ponse correcte.",
                List.of("print(\"Hello\")",
                        "System.out.println(\"Hello\");",
                        "console.log(\"Hello\");"), 0));
        questions.put("javascript", new TestQuestion(
                "Quelle instruction affiche Hello en JavaScript ?",
                "Une seule r\u00e9ponse correcte.",
                List.of("console.log(\"Hello\");",
                        "print(\"Hello\")",
                        "System.out.println(\"Hello\");"), 0));
        questions.put("cpp", new TestQuestion(
                "Quelle instruction affiche Hello en C++ ?",
                "Une seule r\u00e9ponse correcte.",
                List.of("std::cout << \"Hello\";",
                        "print(\"Hello\")",
                        "console.log(\"Hello\");"), 0));
    }

    private void seedProgress() {
        StudentProgress student = progress("etudiant");
        student.completeCourse("java|java-1", null);
        student.completeExercise("java|Afficher Bonjour", null);
        student.completeExercise("java|Somme de deux nombres", null);
        student.testScores().add(90);

        StudentProgress other = progress("amine01");
        other.completeCourse("python|python-1", null);
        other.testScores().add(80);
    }
}
