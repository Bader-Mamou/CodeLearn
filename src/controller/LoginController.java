package controller;

import model.LoginModel;
import model.Role;
import model.Session;
import view.LoginView;

/** Controller of the login screen: mediates between the login model and view. */
public final class LoginController implements LoginView.Listener {

    public interface Listener {
        void onLogin(String username, String password, Role role);
    }

    private final LoginModel model;
    private final LoginView view;
    private final Listener listener;

    public LoginController(LoginModel model, LoginView view, Listener listener) {
        this.model = model;
        this.view = view;
        this.listener = listener;
    }

    public void start() {
        view.setListener(this);
        view.setRoles(model.roles());
    }

    @Override
    public void onLogin(String username, String password, Role role) {
        listener.onLogin(username, password, role);
    }

    /** Authenticates and returns a session, or {@code null}. */
    public Session authenticate(String username, String password, Role role) {
        return model.authenticate(username, password, role);
    }

    public void showError(String message) {
        view.showError(message);
    }
}
