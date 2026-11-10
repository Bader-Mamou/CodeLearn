package controller;

import model.AdminModel;
import model.Role;
import view.AdminView;

import java.util.List;

/** Controller of the administrator workspace. */
public final class AdminController implements AdminView.Listener {

    private final AdminModel model;
    private final AdminView view;
    private final Runnable logout;

    public AdminController(AdminModel model, AdminView view, Runnable logout) {
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
            case "Utilisateurs":
                view.showUsers(model.accounts(), model.programmingLanguages(),
                        model.programmingLanguages());
                break;
            case "Enseignants":
                view.showTeachers(model.teachers(), model.programmingLanguages());
                break;
            case "R\u00f4les et droits":
                view.showRoles(model.roles());
                break;
            case "Supervision":
                view.showSupervision(model.systemStatuses());
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

    /* ================================================================ USERS */
    @Override
    public void onAddUser(String name, String username, String password, Role role,
                          String track, List<String> languages) {
        String error = model.addUser(name, username, password, role, track, languages);
        if (error != null) {
            view.showInfo(error);
            return;
        }
        view.showInfo("Utilisateur \u00ab " + username.trim() + " \u00bb cr\u00e9\u00e9.");
        onNavigate("Utilisateurs");
    }

    @Override
    public void onEditUser(String username, String name, String password, String status,
                           String track, List<String> languages) {
        String error = model.updateUser(username, name, password, status, track, languages);
        if (error != null) {
            view.showInfo(error);
            return;
        }
        view.showInfo("Utilisateur \u00ab " + username + " \u00bb mis \u00e0 jour.");
        onNavigate("Utilisateurs");
    }

    @Override
    public void onDeleteUser(String username) {
        String error = model.deleteUser(username);
        if (error != null) {
            view.showInfo(error);
            return;
        }
        view.showInfo("Utilisateur \u00ab " + username + " \u00bb supprim\u00e9.");
        onNavigate("Utilisateurs");
    }

    /* ============================================================= TEACHERS */
    @Override
    public void onAddTeacher(String name, String username, List<String> languages) {
        String error = model.addTeacher(name, username, languages);
        if (error != null) {
            view.showInfo(error);
            return;
        }
        view.showInfo("Enseignant \u00ab " + name.trim() + " \u00bb cr\u00e9\u00e9.\n"
                + "Identifiant : " + username.trim()
                + " \u2014 mot de passe : " + model.teacherDefaultPassword());
        onNavigate("Enseignants");
    }

    @Override
    public void onEditTeacher(String username, String name, List<String> languages) {
        String error = model.updateTeacher(username, name, languages);
        if (error != null) {
            view.showInfo(error);
            return;
        }
        view.showInfo("Enseignant \u00ab " + name.trim() + " \u00bb mis \u00e0 jour.");
        onNavigate("Enseignants");
    }

    @Override
    public void onDeleteTeacher(String username) {
        String error = model.deleteTeacher(username);
        if (error != null) {
            view.showInfo(error);
            return;
        }
        view.showInfo("Enseignant \u00ab " + username + " \u00bb supprim\u00e9.");
        onNavigate("Enseignants");
    }
}
