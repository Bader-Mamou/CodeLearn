package controller;

import model.AdminModel;
import model.AppState;
import model.LanguageInfo;
import model.LoginModel;
import model.ProgrammingLanguages;
import model.Role;
import model.Session;
import model.StudentModel;
import model.TeacherModel;
import model.UserAccount;
import view.AdminView;
import view.LanguageView;
import view.LoginView;
import view.MainFrame;
import view.StudentView;
import view.TeacherView;

import java.util.List;
import java.util.Objects;

/**
 * Top-level MVC controller: session, main frame, onboarding and navigation
 * between the login screen and the role-specific workspaces.
 */
public final class ApplicationController implements LoginController.Listener {

    private final AppState state = new AppState();
    private final MainFrame frame = new MainFrame();
    private final LoginModel loginModel = new LoginModel(state);
    private final LoginController loginController;

    private StudentController studentController;
    private String studentUser;
    private String studentLanguage;
    private String studentTrack;

    private TeacherController teacherController;
    private String teacherUser;

    private AdminController adminController;

    public ApplicationController() {
        LoginView loginView = new LoginView();
        loginController = new LoginController(loginModel, loginView, this);
        frame.addView("login", loginView);
        loginController.start();
    }

    public void start() {
        showLogin();
        frame.setVisible(true);
    }

    @Override
    public void onLogin(String username, String password, Role role) {
        Session session = loginController.authenticate(username, password, role);
        if (session == null) {
            loginController.showError("Identifiants incorrects pour le r\u00f4le s\u00e9lectionn\u00e9.\n"
                    + "Comptes de d\u00e9monstration : etudiant / 1234, sara02 / 1234, admin / 1234.");
            return;
        }
        state.setSession(session);
        switch (role) {
            case STUDENT -> showLanguageSelection();
            case TEACHER -> showTeacher();
            default -> showAdmin();
        }
    }

    private void showLogin() {
        frame.showView("login");
    }

    private void showLanguageSelection() {
        LanguageView view = new LanguageView(StudentModel.availableLanguages());
        view.selectLanguage(state.session().language());
        view.setListener(this::showTrackSelection);
        frame.addView("language", view);
        frame.showView("language");
    }

    /**
     * Second onboarding step: the student picks the programming language to
     * learn. Only languages assigned to at least one teacher are offered.
     */
    private void showTrackSelection(String uiLanguage) {
        List<LanguageInfo> available = state.availableProgrammingLanguages();
        if (available.isEmpty()) {
            finishOnboarding(uiLanguage, "java");
            return;
        }

        boolean english = "en".equals(uiLanguage);
        LanguageView view = new LanguageView(
                available,
                english ? "Which language to learn?" : "Quel langage apprendre ?",
                english ? "These are the languages taught by your teachers."
                        : "Voici les langages enseign\u00e9s par vos enseignants.",
                english ? "Continue" : "Continuer",
                LanguageView::trackColor);
        view.selectLanguage(state.session().programmingLanguage());
        view.setListener(track -> finishOnboarding(uiLanguage, track));
        frame.addView("track", view);
        frame.showView("track");
    }

    private void finishOnboarding(String language, String track) {
        Session current = state.session();
        UserAccount user = state.user(current.username());
        if (user != null) {
            user.setLanguage(language);
            user.setTrack(track);
        }
        state.setSession(new Session(Role.STUDENT, current.username(), current.displayName(),
                language, track));
        showStudent();
    }

    private void showStudent() {
        Session session = state.session();
        String user = session.username();
        String language = session.language();
        String track = session.programmingLanguage();
        if (studentController == null
                || !Objects.equals(user, studentUser)
                || !Objects.equals(language, studentLanguage)
                || !Objects.equals(track, studentTrack)) {
            String trackLabel = ProgrammingLanguages.label(track);
            StudentView view = new StudentView(language, trackLabel);
            studentController = new StudentController(
                    new StudentModel(session, state), view, this::logout, this::reloadStudent);
            frame.addView("student", view);
            studentController.start();
            studentUser = user;
            studentLanguage = language;
            studentTrack = track;
        }
        frame.showView("student");
    }

    /** Rebuilds the student workspace after a profile/language change. */
    private void reloadStudent() {
        Session current = state.session();
        UserAccount user = state.user(current.username());
        if (user != null) {
            state.setSession(new Session(Role.STUDENT, current.username(), current.displayName(),
                    user.language(), user.track()));
        }
        showStudent();
        if (studentController != null) {
            studentController.onNavigate("Mon profil");
        }
    }

    private void showTeacher() {
        String user = state.session().username();
        if (teacherController == null || !Objects.equals(user, teacherUser)) {
            TeacherView view = new TeacherView();
            teacherController = new TeacherController(
                    new TeacherModel(state, state.session()), view, this::logout);
            frame.addView("teacher", view);
            teacherController.start();
            teacherUser = user;
        }
        frame.showView("teacher");
    }

    private void showAdmin() {
        if (adminController == null) {
            AdminView view = new AdminView();
            adminController = new AdminController(
                    new AdminModel(state, state.session()), view, this::logout);
            frame.addView("admin", view);
            adminController.start();
        }
        frame.showView("admin");
    }

    private void logout() {
        state.clearSession();
        showLogin();
    }
}
