package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Teacher workspace data, backed by the shared {@link AppState}. */
public final class TeacherModel {

    private final AppState state;
    private final Session session;

    public TeacherModel(AppState state, Session session) {
        this.state = state;
        this.session = session;
    }

    public List<String> languages() {
        UserAccount user = state.user(session.username());
        if (user == null || user.languages().isEmpty()) {
            return ProgrammingLanguages.all().stream().map(LanguageInfo::code).toList();
        }
        return user.languages();
    }

    /** Descriptors of the languages taught by the logged-in teacher. */
    public List<LanguageInfo> languageInfos() {
        List<LanguageInfo> infos = new ArrayList<>();
        for (String code : languages()) {
            LanguageInfo info = ProgrammingLanguages.find(code);
            if (info != null) {
                infos.add(info);
            }
        }
        return infos;
    }

    public TestQuestion question(String track) {
        return state.question(track);
    }

    public void saveQuestion(String track, TestQuestion question) {
        state.setQuestion(track, question);
    }

    public TeacherSummary summary() {
        List<StudentOverview> students = students();
        List<CourseItem> courses = courses();
        int success = 0;
        if (!students.isEmpty()) {
            for (StudentOverview student : students) {
                success += student.progress();
            }
            success = success / students.size();
        }
        UserAccount account = state.user(session.username());
        String name = account == null ? session.displayName() : account.name();
        return new TeacherSummary(name,
                List.of(
                        new StatInfo("Apprenants", String.valueOf(students.size()), Tone.PURPLE),
                        new StatInfo("Cours", String.valueOf(courses.size()), Tone.GREEN),
                        new StatInfo("Langages", String.valueOf(languages().size()), Tone.AMBER),
                        new StatInfo("Taux de r\u00e9ussite", success + "%", Tone.PURPLE)),
                activities(), languageInfos(), courses, students);
    }

    public List<CourseItem> courses() {
        return state.coursesFor(languages());
    }

    public String addCourse(String track, String title, String description) {
        if (!languages().contains(track)) {
            return "Vous n'enseignez pas ce langage.";
        }
        return state.addCourse(track, title, description);
    }

    public String updateCourse(String id, String track, String title, String description) {
        if (!languages().contains(track)) {
            return "Vous n'enseignez pas ce langage.";
        }
        return state.updateCourse(id, track, title, description);
    }

    public String deleteCourse(String id) {
        CourseItem course = state.course(id);
        if (course == null) {
            return "Cours introuvable.";
        }
        if (!languages().contains(course.track())) {
            return "Ce cours ne fait pas partie de vos langages.";
        }
        return state.deleteCourse(id);
    }

    public List<TeacherQuestion> questions() {
        List<TeacherQuestion> questions = new ArrayList<>();
        for (String code : languages()) {
            questions.add(new TeacherQuestion(code, ProgrammingLanguages.label(code),
                    state.question(code)));
        }
        return questions;
    }

    public List<StudentOverview> students() {
        List<StudentOverview> result = new ArrayList<>();
        for (UserAccount user : state.students()) {
            if (!languages().contains(user.track())) {
                continue;
            }
            StudentProgress progress = state.progress(user.username());
            List<ActivityInfo> activities = progress.activities();
            String last = activities.isEmpty()
                    ? "\u2014"
                    : activities.get(0).title();
            result.add(new StudentOverview(user.name(), user.username(),
                    ProgrammingLanguages.label(user.track()), progress.percent(), last));
        }
        result.sort(Comparator.comparing(StudentOverview::name));
        return result;
    }

    private List<ActivityInfo> activities() {
        List<ActivityInfo> result = new ArrayList<>();
        for (UserAccount user : state.students()) {
            if (!languages().contains(user.track())) {
                continue;
            }
            for (ActivityInfo activity : state.progress(user.username()).activities()) {
                result.add(new ActivityInfo(activity.title(),
                        user.name() + " \u00b7 " + activity.subtitle(),
                        "", activity.tone()));
                if (result.size() >= 4) {
                    return result;
                }
            }
        }
        if (result.isEmpty()) {
            for (String code : languages()) {
                result.add(new ActivityInfo("Parcours " + ProgrammingLanguages.label(code),
                        "Aucune activit\u00e9 r\u00e9cente", "En attente", Tone.AMBER));
                if (result.size() >= 3) {
                    break;
                }
            }
        }
        return result;
    }

    public List<LevelInfo> levels() {
        StudentTexts t = StudentTexts.forLanguage("fr");
        List<LevelInfo> levels = new ArrayList<>();
        for (String code : languages()) {
            String track = ProgrammingLanguages.label(code);
            levels.add(new LevelInfo(t.l1Tag() + " \u00b7 " + track, t.l1Name(),
                    t.l1Desc().replace("{0}", track), t.l1Status(), true));
            levels.add(new LevelInfo(t.l2Tag() + " \u00b7 " + track, t.l2Name(),
                    t.l2Desc().replace("{0}", track), t.l2Status(), true));
            levels.add(new LevelInfo(t.l3Tag() + " \u00b7 " + track, t.l3Name(),
                    t.l3Desc().replace("{0}", track), t.l3Status(), true));
        }
        return levels;
    }
}
