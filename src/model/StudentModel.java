package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Learning data for the student workspace, backed by {@link AppState}. */
public final class StudentModel {
    private final Session session;
    private final AppState state;

    public StudentModel(Session session) {
        this(session, new AppState());
    }

    public StudentModel(Session session, AppState state) {
        this.session = session;
        this.state = state;
    }

    /** Interface languages offered to the student at first login. */
    public static List<LanguageInfo> availableLanguages() {
        return List.of(
                new LanguageInfo("fr", "Fran\u00e7ais"),
                new LanguageInfo("en", "English"));
    }

    /** Programming language chosen by the student (defaults to Java). */
    public String trackCode() {
        String code = session.programmingLanguage();
        return code == null || code.isBlank() ? "java" : code;
    }

    /** Display name of the chosen programming language. */
    public String trackLabel() {
        return ProgrammingLanguages.label(trackCode());
    }

    public String userName() {
        UserAccount user = state.user(session.username());
        return user == null ? session.displayName() : user.name();
    }

    public StudentProgress progress() {
        return state.progress(session.username());
    }

    public StudentSummary summary() {
        Map<String, String> t = texts(session.language());
        String track = trackLabel();
        StudentProgress progress = progress();
        return new StudentSummary(
                t.get("greeting") + ", " + userName() + " !",
                t.get("subtitle"),
                fill(t.get("eyebrow"), track),
                fill(t.get("heroTitle"), track),
                t.get("heroSubtitle"),
                currentLevel(t),
                progress.percent(),
                streak(),
                levels(),
                List.of(
                        new StatInfo(t.get("stat.average"), progress.averageScore() + "%", Tone.PURPLE),
                        new StatInfo(t.get("stat.tests"),
                                progress.passedTests() + " / " + progress.testScores().size(), Tone.GREEN),
                        new StatInfo(t.get("stat.time"), progress.totalTime(), Tone.PURPLE)),
                activities(t));
    }

    private String currentLevel(Map<String, String> t) {
        return switch (progress().levelNumber()) {
            case 3 -> t.get("level3");
            case 2 -> t.get("level2");
            default -> t.get("level1");
        };
    }

    private Map<String, String> texts(String language) {
        return switch (language) {
            case "en" -> Map.ofEntries(
                    Map.entry("greeting", "Hello"),
                    Map.entry("subtitle", "Continue your learning journey."),
                    Map.entry("eyebrow", "{0} COURSE"),
                    Map.entry("heroTitle", "Master {0}"),
                    Map.entry("heroSubtitle", "A complete platform to learn programming level by level, "
                            + "with courses, exercises and tests."),
                    Map.entry("level1", "Beginner"),
                    Map.entry("level2", "Intermediate"),
                    Map.entry("level3", "Advanced"),
                    Map.entry("stat.average", "Average score"),
                    Map.entry("stat.tests", "Tests passed"),
                    Map.entry("stat.time", "Total time"),
                    Map.entry("streak.title", "Learning streak"),
                    Map.entry("streak.subtitle", "Keep up the momentum!"),
                    Map.entry("streak.week", "THIS WEEK"),
                    Map.entry("activity.test", "Level 1 test: 90%"),
                    Map.entry("activity.exercise", "{0} exercise: Loops"),
                    Map.entry("activity.course", "Course: Variables"),
                    Map.entry("time.yesterday", "Yesterday"),
                    Map.entry("time.dayBefore", "Day before"),
                    Map.entry("time.now", "Just now"),
                    Map.entry("streak.record", "Record: {0} days"));
            default -> Map.ofEntries(
                    Map.entry("greeting", "Bonjour"),
                    Map.entry("subtitle", "Continuez votre parcours d'apprentissage."),
                    Map.entry("eyebrow", "PARCOURS {0}"),
                    Map.entry("heroTitle", "Ma\u00eetrisez {0}"),
                    Map.entry("heroSubtitle", "Une plateforme compl\u00e8te pour apprendre la programmation en niveau, "
                            + "avec des cours, des exercices et des tests."),
                    Map.entry("level1", "D\u00e9butant"),
                    Map.entry("level2", "Interm\u00e9diaire"),
                    Map.entry("level3", "Avanc\u00e9"),
                    Map.entry("stat.average", "Score moyen"),
                    Map.entry("stat.tests", "Tests r\u00e9ussis"),
                    Map.entry("stat.time", "Temps total"),
                    Map.entry("streak.title", "S\u00e9rie d'apprentissage"),
                    Map.entry("streak.subtitle", "Continuez sur votre lanc\u00e9e !"),
                    Map.entry("streak.week", "CETTE SEMAINE"),
                    Map.entry("activity.test", "Test du Niveau 1 r\u00e9ussi avec 90%"),
                    Map.entry("activity.exercise", "Exercice {0} : Les boucles"),
                    Map.entry("activity.course", "Cours : Les variables"),
                    Map.entry("time.yesterday", "Hier"),
                    Map.entry("time.dayBefore", "Avant-hier"),
                    Map.entry("time.now", "\u00c0 l'instant"),
                    Map.entry("streak.record", "Record : {0} jours"));
        };
    }

    private boolean english() {
        return "en".equals(session.language());
    }

    private String fill(String text, String track) {
        return text == null ? "" : text.replace("{0}", track);
    }

    private String now() {
        return texts(session.language()).get("time.now");
    }

    private String doneLabel() {
        return english() ? "Completed" : "Termin\u00e9";
    }

    private String openLabel() {
        return english() ? "Open" : "Ouvert";
    }

    private StreakInfo streak() {
        Map<String, String> t = texts(session.language());
        return new StreakInfo(t.get("streak.title"), 12, 21,
                t.get("streak.subtitle"), t.get("streak.week"),
                List.of(
                        new StreakDay("L", true, false),
                        new StreakDay("M", true, false),
                        new StreakDay("M", true, false),
                        new StreakDay("J", true, false),
                        new StreakDay("V", true, true),
                        new StreakDay("S", false, false),
                        new StreakDay("D", false, false)));
    }

    private List<ActivityInfo> activities(Map<String, String> t) {
        List<ActivityInfo> recorded = progress().activities();
        if (!recorded.isEmpty()) {
            return recorded;
        }
        String track = trackLabel();
        return List.of(
                new ActivityInfo(t.get("activity.test"),
                        t.get("time.yesterday") + ", 14:30", "", Tone.GREEN),
                new ActivityInfo(fill(t.get("activity.exercise"), track),
                        t.get("time.yesterday") + ", 13:15", "", Tone.GREEN),
                new ActivityInfo(t.get("activity.course"),
                        t.get("time.dayBefore"), "", Tone.GREEN));
    }

    public List<LevelInfo> levels() {
        StudentTexts t = StudentTexts.forLanguage(session.language());
        String track = trackLabel();
        int level = progress().levelNumber();
        return List.of(
                new LevelInfo(t.l1Tag(), t.l1Name(), fill(t.l1Desc(), track), t.l1Status(), true),
                new LevelInfo(t.l2Tag(), t.l2Name(), fill(t.l2Desc(), track),
                        level >= 2 ? t.l1Status() : t.l2Status(), level >= 2),
                new LevelInfo(t.l3Tag(), t.l3Name(), fill(t.l3Desc(), track),
                        level >= 3 ? t.l1Status() : t.l3Status(), level >= 3));
    }

    public List<CourseInfo> courses() {
        List<CourseInfo> result = new ArrayList<>();
        StudentProgress progress = progress();
        int index = 0;
        for (CourseItem item : state.courses()) {
            if (!item.track().equals(trackCode())) {
                continue;
            }
            boolean done = progress.completedCourses().contains(courseKey(item));
            boolean first = index == 0;
            result.add(new CourseInfo(item.id(), item.title(), item.description(),
                    done ? doneLabel() : openLabel(),
                    done || first ? Tone.GREEN : Tone.AMBER));
            index++;
        }
        return result;
    }

    public boolean courseCompleted(CourseItem item) {
        return progress().completedCourses().contains(courseKey(item));
    }

    public String courseKey(CourseItem item) {
        return trackCode() + "|" + item.id();
    }

    public boolean completeCourse(CourseItem item) {
        String title = english() ? "Course completed: " + item.title()
                : "Cours termin\u00e9 : " + item.title();
        return progress().completeCourse(courseKey(item),
                new ActivityInfo(title, now(), "", Tone.GREEN));
    }

    public List<ExerciseInfo> exercises() {
        StudentProgress progress = progress();
        return List.of(
                new ExerciseInfo("Afficher Bonjour",
                        "\u00c9crire un programme qui affiche \u00ab Bonjour \u00bb dans la console.",
                        "Facile", Tone.GREEN, progress.completedExercises().contains(exerciseKey("Afficher Bonjour"))),
                new ExerciseInfo("Somme de deux nombres",
                        "Lire deux entiers saisis par l'utilisateur et afficher leur somme.",
                        "Facile", Tone.GREEN, progress.completedExercises().contains(exerciseKey("Somme de deux nombres"))));
    }

    private String exerciseKey(String title) {
        return trackCode() + "|" + title;
    }

    public TestResult submitExercise(ExerciseInfo exercise, String code) {
        if (code == null || code.isBlank()) {
            return new TestResult(false, 0, "\u00c9crivez votre solution avant de la v\u00e9rifier.");
        }
        String expected = switch (trackCode()) {
            case "python" -> "print";
            case "javascript" -> "console.log";
            case "cpp" -> "cout";
            default -> "system.out.println";
        };
        String display = switch (trackCode()) {
            case "python" -> "print()";
            case "javascript" -> "console.log()";
            case "cpp" -> "std::cout";
            default -> "System.out.println()";
        };
        boolean correct = code.toLowerCase().contains(expected);
        if (correct) {
            String title = english() ? "Exercise completed: " + exercise.title()
                    : "Exercice r\u00e9ussi : " + exercise.title();
            progress().completeExercise(exerciseKey(exercise.title()),
                    new ActivityInfo(title, now(), "", Tone.GREEN));
            return new TestResult(true, 100,
                    "Bravo ! Votre solution contient l'instruction attendue (" + display + ").");
        }
        return new TestResult(false, 0,
                "Votre solution ne contient pas encore l'instruction attendue : " + display + ".");
    }

    public TestQuestion testQuestion() {
        return state.question(trackCode());
    }

    public TestResult submitTest(int selected) {
        TestQuestion question = testQuestion();
        if (selected < 0 || selected >= question.options().size()) {
            return new TestResult(false, 0, "S\u00e9lectionnez une r\u00e9ponse avant de valider.");
        }
        boolean correct = selected == question.correctIndex();
        int score = correct ? 100 : 40;
        String title = correct
                ? (english() ? "Test passed: " + score + "%" : "Test r\u00e9ussi : " + score + "%")
                : (english() ? "Test attempt: " + score + "%" : "Test tent\u00e9 : " + score + "%");
        progress().addTestScore(score, new ActivityInfo(title, now(), "", Tone.GREEN));
        String message = correct
                ? "Bonne r\u00e9ponse ! Score : " + score + "%."
                : "R\u00e9ponse incorrecte. La bonne r\u00e9ponse \u00e9tait : "
                + question.options().get(question.correctIndex()) + ".";
        return new TestResult(correct, score, message);
    }

    public ProgressInfo progressInfo() {
        StudentProgress progress = progress();
        int percent = progress.percent();
        int total = 0;
        int done = 0;
        for (CourseItem item : state.courses()) {
            if (item.track().equals(trackCode())) {
                total++;
                if (progress.completedCourses().contains(courseKey(item))) {
                    done++;
                }
            }
        }
        String note = "Niveau " + progress.levelNumber() + " sur 3 \u00b7 "
                + done + " cours termin\u00e9" + (done > 1 ? "s" : "") + " sur " + total;
        return new ProgressInfo(percent, percent + "%", note);
    }

    public ProfileInfo profile() {
        StudentTexts t = StudentTexts.forLanguage(session.language());
        String levelName = switch (progress().levelNumber()) {
            case 3 -> t.l3Name();
            case 2 -> t.l2Name();
            default -> t.l1Name();
        };
        return new ProfileInfo(userName(), "Apprenant", trackLabel(),
                "Niveau " + progress().levelNumber() + " \u2014 " + levelName,
                session.language(), trackCode());
    }

    /** Saves the profile fields edited from the student profile page. */
    public void updateProfile(String name, String language, String track) {
        UserAccount user = state.user(session.username());
        if (user == null) {
            return;
        }
        if (name != null && !name.isBlank()) {
            user.setName(name.trim());
        }
        if (language != null && !language.isBlank()) {
            user.setLanguage(language);
        }
        if (track != null && !track.isBlank()) {
            user.setTrack(track);
        }
    }

    public Session session() {
        return session;
    }

    public AppState state() {
        return state;
    }
}
