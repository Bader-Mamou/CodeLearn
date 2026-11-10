package model;

import java.util.List;

/** Login model: available roles and in-memory authentication. */
public final class LoginModel {

    private final AppState state;

    public LoginModel(AppState state) {
        this.state = state;
    }

    public List<Role> roles() {
        return List.of(Role.values());
    }

    /** Returns a session when the credentials match the selected role. */
    public Session authenticate(String username, String password, Role role) {
        return state.authenticate(username, password, role);
    }
}
