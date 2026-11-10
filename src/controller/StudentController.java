package controller;

import model.CourseInfo;
import model.CourseItem;
import model.ExerciseInfo;
import model.LanguageInfo;
import model.ProgrammingLanguages;
import model.StudentModel;
import model.TestResult;
import view.StudentView;

import java.util.List;

/** Controller of the student dashboard: mediates between the student model and view. */
public final class StudentController implements StudentView.Listener {

    private final StudentModel model;
    private final StudentView view;
    private final Runnable logout;
    private final Runnable reload;

    public StudentController(StudentModel model, StudentView view, Runnable logout,
                             Runnable reload) {
        this.model = model;
        this.view = view;
        this.logout = logout;
        this.reload = reload;
    }

    public void start() {
        view.setListener(this);
        onNavigate("Accueil");
    }

    @Override
    public void onNavigate(String page) {
        view.setSelectedPage(page);
        switch (page) {
            case "Mes cours":
                view.showCourses(model.courses());
                break;
            case "Exercices":
                view.showExercises(model.exercises());
                break;
            case "Tests":
                view.showTest(model.testQuestion());
                break;
            case "Ma progression":
                view.showProgress(model.progressInfo());
                break;
            case "Mon profil":
                List<LanguageInfo> tracks = model.state().availableProgrammingLanguages();
                if (tracks.isEmpty()) {
                    tracks = ProgrammingLanguages.all();
                }
                view.showProfile(model.profile(), StudentModel.availableLanguages(), tracks);
                break;
            default:
                view.showHome(model.summary());
                break;
        }
    }

    @Override
    public void onLogout() {
        logout.run();
    }

    @Override
    public void onOpenCourse(CourseInfo course) {
        CourseItem item = course == null ? null : model.state().course(course.id());
        if (item == null) {
            view.showInfo("Ce cours n'est plus disponible.");
            onNavigate("Mes cours");
            return;
        }
        view.showCourseDetail(course, model.courseCompleted(item));
    }

    @Override
    public void onCompleteCourse(CourseInfo course) {
        CourseItem item = course == null ? null : model.state().course(course.id());
        if (item == null) {
            view.showInfo("Ce cours n'est plus disponible.");
            onNavigate("Mes cours");
            return;
        }
        boolean added = model.completeCourse(item);
        view.showInfo(added
                ? "Cours termin\u00e9 : \u00ab " + item.title() + " \u00bb. Votre progression a \u00e9t\u00e9 mise \u00e0 jour."
                : "Vous avez d\u00e9j\u00e0 termin\u00e9 ce cours.");
        onNavigate("Mes cours");
    }

    @Override
    public void onOpenExercise(ExerciseInfo exercise) {
        view.showExerciseDetail(exercise);
    }

    @Override
    public void onSubmitExercise(ExerciseInfo exercise, String code) {
        TestResult result = model.submitExercise(exercise, code);
        view.showExerciseResult(exercise, result);
    }

    @Override
    public void onSubmitTest(int selectedOption) {
        TestResult result = model.submitTest(selectedOption);
        view.showTestResult(result, model.testQuestion());
    }

    @Override
    public void onSaveProfile(String name, String language, String track) {
        model.updateProfile(name, language, track);
        view.showInfo("Profil mis \u00e0 jour.");
        reload.run();
    }
}
