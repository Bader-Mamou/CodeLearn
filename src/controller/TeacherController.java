package controller;

import model.ProgrammingLanguages;
import model.TestQuestion;
import model.TeacherModel;
import view.TeacherView;

import java.util.List;

/** Controller of the teacher workspace. */
public final class TeacherController implements TeacherView.Listener {

    private final TeacherModel model;
    private final TeacherView view;
    private final Runnable logout;

    public TeacherController(TeacherModel model, TeacherView view, Runnable logout) {
        this.model = model;
        this.view = view;
        this.logout = logout;
    }

    public void start() {
        view.setListener(this);
        onNavigate("Tableau de bord");
    }

    @Override
    public void onNavigate(String page) {
        view.setSelectedPage(page);
        switch (page) {
            case "Gestion des cours":
                view.showCourses(model.courses(), model.languageInfos());
                break;
            case "Niveaux":
                view.showLevels(model.levels(), model.languageInfos());
                break;
            case "\u00c9valuations":
                view.showEvaluations(model.questions());
                break;
            case "Suivi des apprenants":
                view.showStudents(model.students());
                break;
            default:
                view.showDashboard(model.summary());
                break;
        }
    }

    @Override
    public void onLogout() {
        logout.run();
    }

    @Override
    public void onSaveCourse(String id, String track, String title, String description) {
        String error = id == null
                ? model.addCourse(track, title, description)
                : model.updateCourse(id, track, title, description);
        if (error != null) {
            view.showInfo(error);
            return;
        }
        view.showInfo(id == null
                ? "Cours \u00ab " + title.trim() + " \u00bb cr\u00e9\u00e9."
                : "Cours \u00ab " + title.trim() + " \u00bb mis \u00e0 jour.");
        onNavigate("Gestion des cours");
    }

    @Override
    public void onDeleteCourse(String id) {
        String error = model.deleteCourse(id);
        if (error != null) {
            view.showInfo(error);
            return;
        }
        view.showInfo("Cours supprim\u00e9.");
        onNavigate("Gestion des cours");
    }

    @Override
    public void onSaveQuestion(String track, int correctIndex) {
        TestQuestion current = model.question(track);
        if (current == null) {
            view.showInfo("\u00c9valuation introuvable.");
            return;
        }
        if (correctIndex < 0 || correctIndex >= current.options().size()) {
            view.showInfo("S\u00e9lectionnez une r\u00e9ponse valide.");
            return;
        }
        model.saveQuestion(track, new TestQuestion(current.prompt(), current.hint(),
                current.options(), correctIndex));
        view.showInfo("Bonne r\u00e9ponse mise \u00e0 jour pour "
                + ProgrammingLanguages.label(track) + ".");
        onNavigate("\u00c9valuations");
    }
}
