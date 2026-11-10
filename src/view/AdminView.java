package view;

import model.AdminSummary;
import model.LanguageInfo;
import model.ProgrammingLanguages;
import model.Role;
import model.RoleInfo;
import model.StatInfo;
import model.SystemStatus;
import model.TeacherAccount;
import model.Tone;
import model.UserAccount;
import model.UserInfo;
import view.components.DashboardKit;
import view.components.Sidebar;
import view.components.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Administrator workspace: users, teachers, roles and supervision. */
public class AdminView extends JPanel {

    public interface Listener {
        void onNavigate(String page);

        void onLogout();

        void onAddUser(String name, String username, String password, Role role,
                       String track, List<String> languages);

        void onEditUser(String username, String name, String password, String status,
                        String track, List<String> languages);

        void onDeleteUser(String username);

        void onAddTeacher(String name, String username, List<String> languages);

        void onEditTeacher(String username, String name, List<String> languages);

        void onDeleteTeacher(String username);
    }

    private final JPanel body = new JPanel(new BorderLayout());
    private final Sidebar sidebar;
    private Listener listener;

    public AdminView() {
        setLayout(new BorderLayout());
        sidebar = new Sidebar("Administrateur",
                new String[]{"Tableau de bord", "Utilisateurs", "Enseignants",
                        "R\u00f4les et droits", "Supervision"},
                this::requestNavigate, this::requestLogout);
        add(sidebar, BorderLayout.WEST);

        body.setBackground(Theme.BG);
        add(body, BorderLayout.CENTER);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setSelectedPage(String page) {
        sidebar.setSelected(page);
    }

    /* ============================================================ DASHBOARD */
    public void showDashboard(AdminSummary summary) {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Theme.BG);

        JPanel content = DashboardKit.content();

        content.add(DashboardKit.header(
                "Bonjour, " + summary.name(),
                "Voici la sant\u00e9 de la plateforme et les derniers comptes cr\u00e9\u00e9s.",
                DashboardKit.identity(summary.name(), "Administrateur")));
        content.add(Box.createVerticalStrut(18));

        content.add(DashboardKit.hero(
                "Administration",
                "Pilotez CodeLearn en un coup d'\u0153il",
                "G\u00e9rez les comptes, attribuez les langages aux enseignants et gardez "
                        + "la plateforme sous contr\u00f4le depuis un espace unique.",
                new Color(24, 32, 56), new Color(70, 69, 176), "gear",
                List.of(
                        DashboardKit.heroButton("G\u00e9rer les utilisateurs", "\u2699",
                                new Color(48, 46, 145), true,
                                () -> requestNavigate("Utilisateurs")),
                        DashboardKit.heroButton("Voir les enseignants", "\u2192",
                                Color.WHITE, false,
                                () -> requestNavigate("Enseignants")))));
        content.add(Box.createVerticalStrut(18));

        content.add(statsRow(summary));
        content.add(Box.createVerticalStrut(18));

        content.add(twoColumns(usersCard(summary), statusCard(summary), 300, 0.66));
        content.add(Box.createVerticalStrut(18));

        int languageHeight = 132 + 43 * Math.max(3, summary.languages().size());
        content.add(twoColumns(languagesCard(summary), quickActionsCard(),
                languageHeight, 0.66));
        content.add(Box.createVerticalStrut(6));

        page.add(DashboardKit.scroll(content), BorderLayout.CENTER);
        setPage(page);
    }

    private JPanel statsRow(AdminSummary summary) {
        List<StatInfo> stats = summary.stats();
        JPanel grid = new JPanel(new GridLayout(1, Math.max(1, stats.size()), 16, 0));
        grid.setOpaque(false);
        DashboardKit.fullWidth(grid, 104);

        String[] icons = {"profile", "book", "progress", "gear"};
        String[] captions = {
                "Tous les comptes",
                "Comptes enseignants",
                "Comptes apprenants",
                "Acc\u00e8s complet"
        };
        for (int i = 0; i < stats.size(); i++) {
            grid.add(DashboardKit.statCard(icons[i % icons.length], stats.get(i),
                    captions[i % captions.length]));
        }
        return grid;
    }

    private JPanel usersCard(AdminSummary summary) {
        List<UserInfo> users = summary.recentUsers();

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(17, 20, 14, 20));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        if (users.isEmpty()) {
            body.add(DashboardKit.empty("Aucun utilisateur pour le moment."));
        } else {
            int shown = Math.min(5, users.size());
            for (int i = 0; i < shown; i++) {
                if (i > 0) {
                    body.add(separator());
                }
                body.add(adminUserRow(users.get(i)));
            }
        }
        body.add(Box.createVerticalGlue());

        card.add(DashboardKit.cardHeader("Derniers inscrits",
                users.size() + " comptes sur la plateforme",
                link("Tout voir \u2192", () -> requestNavigate("Utilisateurs"))),
                BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel adminUserRow(UserInfo user) {
        Color tone = roleColor(user.role());

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.setBorder(new EmptyBorder(3, 0, 3, 2));

        row.add(DashboardKit.iconBadge("profile", tone,
                Theme.toneBackground(toneFor(user.role())), 30), BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(Box.createVerticalGlue());

        JLabel name = new JLabel(user.name());
        name.setFont(Theme.fontBold(12));
        name.setForeground(Theme.TEXT);
        name.setAlignmentX(LEFT_ALIGNMENT);

        JLabel username = new JLabel("@" + user.username());
        username.setFont(Theme.fontPlain(10));
        username.setForeground(Theme.MUTED);
        username.setAlignmentX(LEFT_ALIGNMENT);

        text.add(name);
        text.add(Box.createVerticalStrut(1));
        text.add(username);
        text.add(Box.createVerticalGlue());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JLabel status = new JLabel(user.status());
        status.setFont(Theme.fontPlain(10));
        status.setForeground("Actif".equalsIgnoreCase(user.status())
                ? Theme.GREEN : Theme.RED);

        JLabel role = Theme.badge(user.role(), tone,
                Theme.toneBackground(toneFor(user.role())));

        right.add(status);
        right.add(role);

        row.add(text, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JPanel statusCard(AdminSummary summary) {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(17, 20, 16, 20));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        List<SystemStatus> statuses = summary.statuses();
        if (statuses.isEmpty()) {
            body.add(DashboardKit.empty("Aucun statut disponible."));
        } else {
            for (int i = 0; i < statuses.size(); i++) {
                if (i > 0) {
                    body.add(separator());
                }
                body.add(statusRow(statuses.get(i)));
            }
        }
        body.add(Box.createVerticalGlue());

        JPanel footer = Theme.roundedPanel(12, Theme.GREEN_SOFT, new Color(200, 238, 220));
        footer.setLayout(new BorderLayout(10, 0));
        footer.setBorder(new EmptyBorder(9, 12, 9, 12));
        footer.setAlignmentX(LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        footer.add(DashboardKit.iconBadge("check", Theme.GREEN,
                new Color(206, 242, 224), 26), BorderLayout.WEST);

        JPanel footerText = new JPanel();
        footerText.setOpaque(false);
        footerText.setLayout(new BoxLayout(footerText, BoxLayout.Y_AXIS));
        JLabel footerTitle = new JLabel("Plateforme op\u00e9rationnelle");
        footerTitle.setFont(Theme.fontBold(11));
        footerTitle.setForeground(new Color(6, 118, 82));
        footerTitle.setAlignmentX(LEFT_ALIGNMENT);
        JLabel footerNote = new JLabel("Tous les services r\u00e9pondent.");
        footerNote.setFont(Theme.fontPlain(9));
        footerNote.setForeground(new Color(16, 145, 105));
        footerNote.setAlignmentX(LEFT_ALIGNMENT);
        footerText.add(footerTitle);
        footerText.add(Box.createVerticalStrut(1));
        footerText.add(footerNote);
        footer.add(footerText, BorderLayout.CENTER);

        card.add(DashboardKit.cardHeader("\u00c9tat de la plateforme",
                "Services et configuration", null), BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private JPanel languagesCard(AdminSummary summary) {
        List<LanguageInfo> languages = summary.languages();

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(17, 20, 14, 20));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        if (languages.isEmpty()) {
            body.add(DashboardKit.empty(
                    "Aucun langage enseign\u00e9. Attribuez-en \u00e0 un enseignant."));
        } else {
            for (int i = 0; i < languages.size(); i++) {
                if (i > 0) {
                    body.add(separator());
                }
                body.add(languageRow(languages.get(i)));
            }
        }
        body.add(Box.createVerticalGlue());

        card.add(DashboardKit.cardHeader("Langages enseign\u00e9s",
                languages.size() + " langages propos\u00e9s aux apprenants",
                link("G\u00e9rer \u2192", () -> requestNavigate("Enseignants"))),
                BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        JPanel footer = Theme.roundedPanel(12, new Color(249, 249, 253), Theme.BORDER);
        footer.setLayout(new BorderLayout(10, 0));
        footer.setBorder(new EmptyBorder(8, 12, 8, 12));
        footer.setAlignmentX(LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        footer.add(DashboardKit.iconBadge("check", Theme.GREEN,
                Theme.GREEN_SOFT, 22), BorderLayout.WEST);
        JLabel footerNote = new JLabel(
                "Seuls ces langages sont propos\u00e9s aux apprenants.");
        footerNote.setFont(Theme.fontPlain(9));
        footerNote.setForeground(Theme.MUTED);
        footer.add(footerNote, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private JPanel languageRow(LanguageInfo language) {
        Color tone = languageColor(language.code());

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.setBorder(new EmptyBorder(4, 0, 4, 2));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JComponent dot = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(tone);
                g2.fillOval(0, 4, 9, 9);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(11, 18));
        dot.setMinimumSize(new Dimension(11, 18));
        dot.setMaximumSize(new Dimension(11, 18));

        JLabel label = new JLabel(language.label());
        label.setFont(Theme.fontBold(12));
        label.setForeground(Theme.TEXT);

        left.add(dot);
        left.add(label);
        row.add(left, BorderLayout.WEST);
        row.add(DashboardKit.note("Propos\u00e9 aux apprenants"), BorderLayout.CENTER);
        row.add(Theme.badge("Enseign\u00e9", Theme.GREEN, Theme.GREEN_SOFT),
                BorderLayout.EAST);
        return row;
    }

    private JPanel quickActionsCard() {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(17, 20, 16, 20));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(DashboardKit.actionButton("\u2795", "Ajouter un utilisateur",
                Theme.PURPLE, () -> requestNavigate("Utilisateurs")));
        body.add(Box.createVerticalStrut(8));
        body.add(DashboardKit.actionButton("\u270E", "Cr\u00e9er un enseignant",
                Theme.GREEN, () -> requestNavigate("Enseignants")));
        body.add(Box.createVerticalStrut(8));
        body.add(DashboardKit.actionButton("\u25A6", "Ouvrir la supervision",
                Theme.AMBER, () -> requestNavigate("Supervision")));
        body.add(Box.createVerticalGlue());

        card.add(DashboardKit.cardHeader("Actions rapides",
                "Les gestes courants de l'administration", null), BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel twoColumns(JComponent left, JComponent right, int height,
                              double leftWeight) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        DashboardKit.fullWidth(row, height);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.gridy = 0;

        constraints.gridx = 0;
        constraints.weightx = leftWeight;
        constraints.insets = new Insets(0, 0, 0, 9);
        row.add(left, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1 - leftWeight;
        constraints.insets = new Insets(0, 9, 0, 0);
        row.add(right, constraints);
        return row;
    }

    private JLabel link(String text, Runnable action) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.fontBold(11));
        label.setForeground(Theme.PURPLE_LINK);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });
        return label;
    }

    private JComponent separator() {
        JComponent line = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(236, 238, 246));
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        line.setOpaque(false);
        line.setAlignmentX(LEFT_ALIGNMENT);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(10, 1));
        return line;
    }

    private static Color roleColor(String role) {
        if (role == null) {
            return Theme.PURPLE;
        }
        return switch (role) {
            case "Enseignant" -> Theme.GREEN;
            case "Administrateur" -> Theme.PURPLE_BRIGHT;
            default -> Theme.AMBER;
        };
    }

    private static Tone toneFor(String role) {
        if (role == null) {
            return Tone.PURPLE;
        }
        return switch (role) {
            case "Enseignant" -> Tone.GREEN;
            case "Administrateur" -> Tone.PURPLE;
            default -> Tone.AMBER;
        };
    }

    private static Color languageColor(String code) {
        return switch (code) {
            case "python" -> Theme.GREEN;
            case "javascript" -> Theme.AMBER;
            case "cpp" -> Theme.PURPLE_BRIGHT;
            default -> Theme.PURPLE;
        };
    }

    /* ================================================================ USERS */
    public void showUsers(List<UserAccount> accounts, List<LanguageInfo> languages,
                          List<LanguageInfo> tracks) {
        JPanel p = Theme.page("Utilisateurs",
                "Ajoutez, modifiez ou supprimez les comptes de la plateforme.");

        JPanel wrap = new JPanel(new BorderLayout(0, 16));
        wrap.setOpaque(false);

        JTable table = table(new String[]{"Nom", "Identifiant", "R\u00f4le", "Statut"},
                userRows(accounts));
        JScrollPane scroll = scroll(table);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton add = Theme.button("Ajouter un utilisateur", "\u2795");
        add.addActionListener(e -> openUserDialog(null, languages, tracks));
        JButton edit = Theme.secondaryButton("Modifier", "\u270e");
        edit.addActionListener(e -> {
            UserAccount account = selectedAccount(table, accounts);
            if (account != null) {
                openUserDialog(account, languages, tracks);
            }
        });
        JButton delete = Theme.dangerButton("Supprimer", "\u274c");
        delete.addActionListener(e -> {
            UserAccount account = selectedAccount(table, accounts);
            if (account == null) {
                return;
            }
            int choice = JOptionPane.showConfirmDialog(this,
                    "Supprimer le compte \u00ab " + account.username() + " \u00bb ?",
                    "Supprimer un utilisateur", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION && listener != null) {
                listener.onDeleteUser(account.username());
            }
        });

        actions.add(add);
        actions.add(edit);
        actions.add(delete);

        wrap.add(scroll, BorderLayout.CENTER);
        wrap.add(actions, BorderLayout.SOUTH);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    private Object[][] userRows(List<UserAccount> accounts) {
        Object[][] rows = new Object[accounts.size()][4];
        for (int i = 0; i < accounts.size(); i++) {
            UserAccount account = accounts.get(i);
            rows[i][0] = account.name();
            rows[i][1] = account.username();
            rows[i][2] = account.role().label();
            rows[i][3] = account.status();
        }
        return rows;
    }

    private UserAccount selectedAccount(JTable table, List<UserAccount> accounts) {
        int row = table.getSelectedRow();
        if (row < 0 || row >= accounts.size()) {
            showInfo("S\u00e9lectionnez d'abord un utilisateur dans le tableau.");
            return null;
        }
        return accounts.get(row);
    }

    /** Create ({@code account == null}) or edit a user account. */
    private void openUserDialog(UserAccount account, List<LanguageInfo> languages,
                                List<LanguageInfo> tracks) {
        JTextField name = Theme.textField();
        JTextField username = Theme.textField();
        JPasswordField password = Theme.passwordField();
        JComboBox<Role> role = Theme.comboBox(Role.values());
        JComboBox<String> status = Theme.comboBox(new String[]{"Actif", "Suspendu"});
        String[] trackLabels = tracks.stream().map(LanguageInfo::label).toArray(String[]::new);
        JComboBox<String> track = Theme.comboBox(trackLabels);

        List<JCheckBox> boxes = new ArrayList<>();
        JPanel checks = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        checks.setOpaque(false);
        checks.setAlignmentX(LEFT_ALIGNMENT);
        for (LanguageInfo language : languages) {
            JCheckBox box = new JCheckBox(language.label());
            box.setOpaque(false);
            box.setFont(Theme.fontPlain(13));
            box.setForeground(Theme.TEXT);
            boxes.add(box);
            checks.add(box);
        }

        if (account == null) {
            role.setSelectedItem(Role.STUDENT);
        } else {
            name.setText(account.name());
            username.setText(account.username());
            username.setEnabled(false);
            role.setSelectedItem(account.role());
            role.setEnabled(false);
            status.setSelectedItem(account.status());
            selectTrack(track, tracks, account.track());
            for (int i = 0; i < languages.size(); i++) {
                boxes.get(i).setSelected(account.languages().contains(languages.get(i).code()));
            }
        }

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(6, 6, 6, 6));
        form.add(fieldLabel("Nom complet"));
        form.add(name);
        form.add(Box.createVerticalStrut(8));
        form.add(fieldLabel(account == null ? "Identifiant" : "Identifiant (non modifiable)"));
        form.add(username);
        form.add(Box.createVerticalStrut(8));
        form.add(fieldLabel(account == null
                ? "Mot de passe" : "Nouveau mot de passe (laisser vide pour conserver)"));
        form.add(password);
        form.add(Box.createVerticalStrut(8));
        form.add(fieldLabel(account == null ? "R\u00f4le" : "R\u00f4le (non modifiable)"));
        form.add(role);
        form.add(Box.createVerticalStrut(8));
        form.add(fieldLabel("Statut"));
        form.add(status);
        form.add(Box.createVerticalStrut(8));
        form.add(fieldLabel("Parcours (apprenant)"));
        form.add(track);
        form.add(Box.createVerticalStrut(10));
        form.add(fieldLabel("Langages enseign\u00e9s (enseignant)"));
        form.add(checks);

        String title = account == null ? "Ajouter un utilisateur" : "Modifier l'utilisateur";
        int choice = JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String trimmedName = name.getText().trim();
        String trimmedUsername = username.getText().trim();
        String chosenPassword = new String(password.getPassword());
        Role chosenRole = (Role) role.getSelectedItem();
        if (trimmedName.isEmpty() || trimmedUsername.isEmpty()) {
            showInfo("Le nom et l'identifiant sont obligatoires.");
            return;
        }
        if (account == null && chosenPassword.isBlank()) {
            showInfo("Le mot de passe est obligatoire.");
            return;
        }

        List<String> selected = new ArrayList<>();
        for (int i = 0; i < boxes.size(); i++) {
            if (boxes.get(i).isSelected()) {
                selected.add(languages.get(i).code());
            }
        }
        if (chosenRole == Role.TEACHER && selected.isEmpty()) {
            showInfo("S\u00e9lectionnez au moins un langage enseign\u00e9.");
            return;
        }

        String trackCode = tracks.get(Math.max(0, track.getSelectedIndex())).code();
        String selectedStatus = (String) status.getSelectedItem();
        if (listener == null) {
            return;
        }
        if (account == null) {
            listener.onAddUser(trimmedName, trimmedUsername, chosenPassword,
                    chosenRole, trackCode, selected);
        } else {
            listener.onEditUser(account.username(), trimmedName, chosenPassword,
                    selectedStatus, trackCode, selected);
        }
    }

    private void selectTrack(JComboBox<String> combo, List<LanguageInfo> tracks, String code) {
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).code().equals(code)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    /* ============================================================= TEACHERS */
    /** Teachers page: list teacher accounts and the languages they teach. */
    public void showTeachers(List<TeacherAccount> teachers, List<LanguageInfo> languages) {
        JPanel p = Theme.page("Enseignants",
                "Cr\u00e9ez les enseignants et attribuez les langages qu'ils enseignent.");

        JPanel wrap = new JPanel(new BorderLayout(0, 16));
        wrap.setOpaque(false);

        JTable table = table(
                new String[]{"Nom", "Identifiant", "Langages enseign\u00e9s", "Statut"},
                teacherRows(teachers));
        JScrollPane scroll = scroll(table);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton create = Theme.button("Cr\u00e9er un enseignant", "\u2795");
        create.addActionListener(e -> openTeacherDialog(null, languages));
        JButton edit = Theme.secondaryButton("Modifier", "\u270e");
        edit.addActionListener(e -> {
            TeacherAccount teacher = selectedTeacher(table, teachers);
            if (teacher != null) {
                openTeacherDialog(teacher, languages);
            }
        });
        JButton delete = Theme.dangerButton("Supprimer", "\u274c");
        delete.addActionListener(e -> {
            TeacherAccount teacher = selectedTeacher(table, teachers);
            if (teacher == null) {
                return;
            }
            int choice = JOptionPane.showConfirmDialog(this,
                    "Supprimer l'enseignant \u00ab " + teacher.username() + " \u00bb ?",
                    "Supprimer un enseignant", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION && listener != null) {
                listener.onDeleteTeacher(teacher.username());
            }
        });

        actions.add(create);
        actions.add(edit);
        actions.add(delete);

        wrap.add(scroll, BorderLayout.CENTER);
        wrap.add(actions, BorderLayout.SOUTH);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    private Object[][] teacherRows(List<TeacherAccount> teachers) {
        Object[][] rows = new Object[teachers.size()][4];
        for (int i = 0; i < teachers.size(); i++) {
            TeacherAccount teacher = teachers.get(i);
            StringBuilder labels = new StringBuilder();
            for (String code : teacher.languages()) {
                if (labels.length() > 0) {
                    labels.append(", ");
                }
                labels.append(ProgrammingLanguages.label(code));
            }
            rows[i][0] = teacher.name();
            rows[i][1] = teacher.username();
            rows[i][2] = labels.toString();
            rows[i][3] = "Actif";
        }
        return rows;
    }

    private TeacherAccount selectedTeacher(JTable table, List<TeacherAccount> teachers) {
        int row = table.getSelectedRow();
        if (row < 0 || row >= teachers.size()) {
            showInfo("S\u00e9lectionnez d'abord un enseignant dans le tableau.");
            return null;
        }
        return teachers.get(row);
    }

    /** Create ({@code teacher == null}) or edit a teacher account. */
    private void openTeacherDialog(TeacherAccount teacher, List<LanguageInfo> languages) {
        JTextField name = Theme.textField();
        JTextField username = Theme.textField();

        JPanel checks = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        checks.setOpaque(false);
        checks.setAlignmentX(LEFT_ALIGNMENT);
        List<JCheckBox> boxes = new ArrayList<>();
        for (LanguageInfo language : languages) {
            JCheckBox box = new JCheckBox(language.label());
            box.setOpaque(false);
            box.setFont(Theme.fontPlain(13));
            box.setForeground(Theme.TEXT);
            boxes.add(box);
            checks.add(box);
        }

        if (teacher == null) {
            username.setText("");
        } else {
            name.setText(teacher.name());
            username.setText(teacher.username());
            username.setEnabled(false);
            for (int i = 0; i < languages.size(); i++) {
                boxes.get(i).setSelected(teacher.languages().contains(languages.get(i).code()));
            }
        }

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(6, 6, 6, 6));
        form.add(fieldLabel("Nom complet"));
        form.add(name);
        form.add(Box.createVerticalStrut(10));
        form.add(fieldLabel(teacher == null
                ? "Identifiant" : "Identifiant (non modifiable)"));
        form.add(username);
        form.add(Box.createVerticalStrut(12));
        form.add(fieldLabel("Langages enseign\u00e9s"));
        form.add(checks);
        if (teacher == null) {
            JLabel hint = new JLabel("Mot de passe initial : 1234");
            hint.setFont(Theme.fontPlain(11));
            hint.setForeground(Theme.MUTED);
            hint.setAlignmentX(LEFT_ALIGNMENT);
            form.add(Box.createVerticalStrut(8));
            form.add(hint);
        }

        String title = teacher == null ? "Cr\u00e9er un enseignant" : "Modifier l'enseignant";
        int choice = JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String trimmedName = name.getText().trim();
        String trimmedUsername = username.getText().trim();
        if (trimmedName.isEmpty() || trimmedUsername.isEmpty()) {
            showInfo("Le nom et l'identifiant sont obligatoires.");
            return;
        }

        List<String> selected = new ArrayList<>();
        for (int i = 0; i < boxes.size(); i++) {
            if (boxes.get(i).isSelected()) {
                selected.add(languages.get(i).code());
            }
        }
        if (selected.isEmpty()) {
            showInfo("S\u00e9lectionnez au moins un langage enseign\u00e9.");
            return;
        }

        if (listener == null) {
            return;
        }
        if (teacher == null) {
            listener.onAddTeacher(trimmedName, trimmedUsername, selected);
        } else {
            listener.onEditTeacher(teacher.username(), trimmedName, selected);
        }
    }

    /* ================================================================ ROLES */
    public void showRoles(List<RoleInfo> roles) {
        JPanel p = Theme.page("R\u00f4les et droits",
                "Droits associ\u00e9s aux r\u00f4les de la plateforme.");

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        for (int i = 0; i < roles.size(); i++) {
            if (i > 0) {
                card.add(Box.createVerticalStrut(14));
            }
            card.add(roleRow(roles.get(i)));
        }

        wrap.add(card, BorderLayout.NORTH);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    /* ========================================================== SUPERVISION */
    public void showSupervision(List<SystemStatus> statuses) {
        JPanel p = Theme.page("Supervision",
                "\u00c9tat de la plateforme en temps r\u00e9el.");

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("\u00c9tat du syst\u00e8me");
        title.setFont(Theme.fontBold(16));
        title.setForeground(Theme.TEXT);
        title.setAlignmentX(LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(14));

        for (int i = 0; i < statuses.size(); i++) {
            if (i > 0) {
                card.add(Box.createVerticalStrut(10));
            }
            card.add(statusRow(statuses.get(i)));
        }

        wrap.add(card, BorderLayout.NORTH);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    /* ================================================================ HELPERS */
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.fontBold(12));
        label.setForeground(Theme.MUTED);
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 2, 4, 0));
        return label;
    }

    private JScrollPane userTable(List<UserInfo> users) {
        Object[][] rows = new Object[users.size()][4];
        for (int i = 0; i < users.size(); i++) {
            UserInfo user = users.get(i);
            rows[i][0] = user.name();
            rows[i][1] = user.username();
            rows[i][2] = user.role();
            rows[i][3] = user.status();
        }
        return scroll(table(new String[]{"Nom", "Identifiant", "R\u00f4le", "Statut"}, rows));
    }

    private JTable table(String[] columns, Object[][] rows) {
        JTable table = new JTable(rows, columns);
        table.setRowHeight(40);
        table.setFont(Theme.fontPlain(13));
        table.setForeground(Theme.TEXT);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(Theme.PURPLE_SOFT);
        table.setSelectionForeground(Theme.TEXT);
        table.setShowVerticalLines(false);
        table.setGridColor(Theme.BORDER);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        DefaultTableCellRenderer cells = new DefaultTableCellRenderer();
        cells.setBorder(new EmptyBorder(0, 14, 0, 14));
        cells.setHorizontalAlignment(SwingConstants.LEFT);
        table.setDefaultRenderer(Object.class, cells);

        DefaultTableCellRenderer header = new DefaultTableCellRenderer();
        header.setOpaque(true);
        header.setBackground(Theme.NAVY_SOFT);
        header.setForeground(Color.WHITE);
        header.setFont(Theme.fontBold(11));
        header.setBorder(new EmptyBorder(12, 14, 12, 14));
        header.setHorizontalAlignment(SwingConstants.LEFT);
        table.getTableHeader().setDefaultRenderer(header);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        return table;
    }

    private JScrollPane scroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setColumnHeaderView(table.getTableHeader());
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel roleRow(RoleInfo role) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setBorder(new EmptyBorder(8, 0, 8, 0));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setAlignmentX(LEFT_ALIGNMENT);

        JLabel t = new JLabel(role.name());
        t.setFont(Theme.fontBold(14));
        t.setForeground(Theme.TEXT);
        t.setAlignmentX(LEFT_ALIGNMENT);

        JLabel d = new JLabel(role.description());
        d.setFont(Theme.fontPlain(12));
        d.setForeground(Theme.MUTED);
        d.setAlignmentX(LEFT_ALIGNMENT);

        text.add(t);
        text.add(Box.createVerticalStrut(3));
        text.add(d);

        row.add(text, BorderLayout.CENTER);
        row.add(Theme.badge("Actif", Theme.toneForeground(role.tone()),
                Theme.toneBackground(role.tone())), BorderLayout.EAST);
        return row;
    }

    private JPanel statusRow(SystemStatus status) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        row.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel l = new JLabel(status.label());
        l.setFont(Theme.fontPlain(13));
        l.setForeground(Theme.TEXT);

        row.add(l, BorderLayout.WEST);
        row.add(Theme.badge(status.status(), Theme.toneForeground(status.tone()),
                Theme.toneBackground(status.tone())), BorderLayout.EAST);
        return row;
    }

    private void setPage(JPanel page) {
        body.removeAll();
        body.add(page, BorderLayout.CENTER);
        body.revalidate();
        body.repaint();
    }

    private void requestNavigate(String page) {
        if (listener != null) {
            listener.onNavigate(page);
        }
    }

    private void requestLogout() {
        if (listener != null) {
            listener.onLogout();
        }
    }
}
