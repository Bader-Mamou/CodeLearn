package view;

import model.ActivityInfo;
import model.CourseItem;
import model.LanguageInfo;
import model.LevelInfo;
import model.ProgrammingLanguages;
import model.StatInfo;
import model.StudentOverview;
import model.TeacherQuestion;
import model.TeacherSummary;
import model.Tone;
import view.components.DashboardKit;
import view.components.Sidebar;
import view.components.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/** Teacher workspace: dashboard, course management, levels, tests and follow-up. */
public class TeacherView extends JPanel {

    public interface Listener {
        void onNavigate(String page);

        void onLogout();

        void onSaveCourse(String id, String track, String title, String description);

        void onDeleteCourse(String id);

        void onSaveQuestion(String track, int correctIndex);
    }

    private final JPanel body = new JPanel(new BorderLayout());
    private final Sidebar sidebar;
    private Listener listener;

    public TeacherView() {
        setLayout(new BorderLayout());
        sidebar = new Sidebar("Enseignant",
                new String[]{"Tableau de bord", "Gestion des cours", "Niveaux",
                        "\u00c9valuations", "Suivi des apprenants"},
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
    public void showDashboard(TeacherSummary summary) {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Theme.BG);

        JPanel content = DashboardKit.content();

        content.add(DashboardKit.header(
                "Bonjour, " + summary.name(),
                "Voici l'activit\u00e9 de vos cours et de vos apprenants aujourd'hui.",
                DashboardKit.identity(summary.name(), "Enseignant")));
        content.add(Box.createVerticalStrut(18));

        content.add(DashboardKit.hero(
                "Espace enseignant",
                "Faites progresser vos apprenants",
                "Cr\u00e9ez vos cours, ajustez vos \u00e9valuations et suivez la progression "
                        + "de chaque apprenant depuis un seul espace.",
                new Color(32, 40, 76), new Color(92, 74, 204), "bookOpen",
                List.of(
                        DashboardKit.heroButton("G\u00e9rer mes cours", "\u270E",
                                new Color(61, 50, 158), true,
                                () -> requestNavigate("Gestion des cours")),
                        DashboardKit.heroButton("Suivre les apprenants", "\u2192",
                                Color.WHITE, false,
                                () -> requestNavigate("Suivi des apprenants")))));
        content.add(Box.createVerticalStrut(18));

        content.add(statsRow(summary));
        content.add(Box.createVerticalStrut(18));

        content.add(twoColumns(activityCard(summary), quickActionsCard(), 300, 0.64));
        content.add(Box.createVerticalStrut(18));

        int languageHeight = Math.max(252,
                136 + 47 * Math.max(2, summary.languages().size()));
        content.add(twoColumns(coursesCard(summary), languagesCard(summary),
                languageHeight, 0.64));
        content.add(Box.createVerticalStrut(6));

        page.add(DashboardKit.scroll(content), BorderLayout.CENTER);
        setPage(page);
    }

    private JPanel statsRow(TeacherSummary summary) {
        List<StatInfo> stats = summary.stats();
        JPanel grid = new JPanel(new GridLayout(1, Math.max(1, stats.size()), 16, 0));
        grid.setOpaque(false);
        DashboardKit.fullWidth(grid, 104);

        String[] icons = {"profile", "book", "diamond", "chart"};
        String[] captions = {
                "Comptes suivis",
                "Cours publi\u00e9s",
                "Langages enseign\u00e9s",
                "Progression moyenne"
        };
        for (int i = 0; i < stats.size(); i++) {
            grid.add(DashboardKit.statCard(icons[i % icons.length], stats.get(i),
                    captions[i % captions.length]));
        }
        return grid;
    }

    private JPanel activityCard(TeacherSummary summary) {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(17, 20, 15, 20));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        List<ActivityInfo> activities = summary.activities();
        if (activities.isEmpty()) {
            body.add(DashboardKit.empty("Aucune activit\u00e9 pour le moment."));
        } else {
            int shown = Math.min(2, activities.size());
            for (int i = 0; i < shown; i++) {
                if (i > 0) {
                    body.add(separator());
                }
                body.add(activityRow(activities.get(i)));
            }
        }

        List<StudentOverview> students = summary.students();
        if (!students.isEmpty()) {
            body.add(Box.createVerticalStrut(4));
            body.add(separator());
            body.add(Box.createVerticalStrut(4));
            body.add(subheading("Progression des apprenants",
                    students.size() + " compte" + (students.size() > 1 ? "s" : "")));
            int shown = Math.min(2, students.size());
            for (int i = 0; i < shown; i++) {
                body.add(studentRow(students.get(i)));
            }
        }
        body.add(Box.createVerticalGlue());

        card.add(DashboardKit.cardHeader("Activit\u00e9 r\u00e9cente",
                "Derniers \u00e9v\u00e9nements sur vos cours", null), BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel subheading(String title, String note) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel heading = new JLabel(title);
        heading.setFont(Theme.fontBold(11));
        heading.setForeground(Theme.TEXT);
        row.add(heading, BorderLayout.WEST);
        row.add(DashboardKit.note(note), BorderLayout.EAST);
        return row;
    }

    private JPanel studentRow(StudentOverview student) {
        Color tone = trackColor(student.track() == null ? "" : student.track().toLowerCase());

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        row.setBorder(new EmptyBorder(2, 0, 2, 2));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(Box.createVerticalGlue());

        JLabel name = new JLabel(student.name());
        name.setFont(Theme.fontBold(11));
        name.setForeground(Theme.TEXT);
        name.setAlignmentX(LEFT_ALIGNMENT);

        JLabel track = new JLabel(student.track() + " \u00b7 @" + student.username());
        track.setFont(Theme.fontPlain(9));
        track.setForeground(Theme.MUTED);
        track.setAlignmentX(LEFT_ALIGNMENT);

        text.add(name);
        text.add(Box.createVerticalStrut(1));
        text.add(track);
        text.add(Box.createVerticalGlue());

        JPanel barWrap = new JPanel(new BorderLayout());
        barWrap.setOpaque(false);
        barWrap.setBorder(new EmptyBorder(12, 8, 12, 8));
        barWrap.add(Theme.progressBar(student.progress(), tone,
                new Color(235, 237, 245)), BorderLayout.CENTER);

        JLabel percent = new JLabel(student.progress() + "%");
        percent.setFont(Theme.fontBold(10));
        percent.setForeground(tone);

        row.add(text, BorderLayout.WEST);
        row.add(barWrap, BorderLayout.CENTER);
        row.add(percent, BorderLayout.EAST);
        return row;
    }

    private JPanel quickActionsCard() {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(17, 20, 16, 20));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(DashboardKit.actionButton("\u2795", "Cr\u00e9er un cours",
                Theme.PURPLE, () -> requestNavigate("Gestion des cours")));
        body.add(Box.createVerticalStrut(8));
        body.add(DashboardKit.actionButton("\u270E", "Ajuster les \u00e9valuations",
                Theme.GREEN, () -> requestNavigate("\u00c9valuations")));
        body.add(Box.createVerticalStrut(8));
        body.add(DashboardKit.actionButton("\u25A6", "Voir les niveaux",
                Theme.AMBER, () -> requestNavigate("Niveaux")));
        body.add(Box.createVerticalStrut(8));
        body.add(DashboardKit.actionButton("\u2192", "Suivre les apprenants",
                Theme.PURPLE_BRIGHT, () -> requestNavigate("Suivi des apprenants")));
        body.add(Box.createVerticalGlue());

        JPanel tip = Theme.roundedPanel(12, new Color(249, 248, 255), Theme.BORDER);
        tip.setLayout(new BorderLayout(10, 0));
        tip.setBorder(new EmptyBorder(10, 12, 10, 12));
        tip.setAlignmentX(LEFT_ALIGNMENT);
        tip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        tip.add(DashboardKit.iconBadge("flame", Theme.PURPLE_BRIGHT,
                new Color(238, 235, 255), 30), BorderLayout.WEST);

        JPanel tipText = new JPanel();
        tipText.setOpaque(false);
        tipText.setLayout(new BoxLayout(tipText, BoxLayout.Y_AXIS));
        JLabel tipTitle = new JLabel("Gardez le rythme");
        tipTitle.setFont(Theme.fontBold(11));
        tipTitle.setForeground(Theme.TEXT);
        tipTitle.setAlignmentX(LEFT_ALIGNMENT);
        JLabel tipNote = new JLabel("Relancez vos apprenants inactifs.");
        tipNote.setFont(Theme.fontPlain(10));
        tipNote.setForeground(Theme.MUTED);
        tipNote.setAlignmentX(LEFT_ALIGNMENT);
        tipText.add(tipTitle);
        tipText.add(Box.createVerticalStrut(1));
        tipText.add(tipNote);
        tip.add(tipText, BorderLayout.CENTER);

        card.add(DashboardKit.cardHeader("Actions rapides",
                "Raccourcis de votre espace", null), BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        card.add(tip, BorderLayout.SOUTH);
        return card;
    }

    private JPanel coursesCard(TeacherSummary summary) {
        List<CourseItem> courses = summary.courses();

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(17, 20, 14, 20));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        if (courses.isEmpty()) {
            body.add(DashboardKit.empty(
                    "Aucun cours pour vos langages. Cr\u00e9ez votre premier cours."));
        } else {
            int shown = Math.min(3, courses.size());
            for (int i = 0; i < shown; i++) {
                if (i > 0) {
                    body.add(separator());
                }
                body.add(courseRow(courses.get(i)));
            }
        }
        body.add(Box.createVerticalGlue());

        card.add(DashboardKit.cardHeader("Mes cours",
                courses.size() + " cours dans vos langages",
                link("Tout g\u00e9rer \u2192", () -> requestNavigate("Gestion des cours"))),
                BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel courseRow(CourseItem course) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setBorder(new EmptyBorder(4, 0, 4, 2));

        row.add(DashboardKit.iconBadge("book", trackColor(course.track()),
                Theme.PURPLE_SOFT, 32), BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(Box.createVerticalGlue());

        JLabel title = new JLabel(course.title());
        title.setFont(Theme.fontBold(12));
        title.setForeground(Theme.TEXT);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel description = new JLabel(shorten(course.description(), 74));
        description.setFont(Theme.fontPlain(10));
        description.setForeground(Theme.MUTED);
        description.setAlignmentX(LEFT_ALIGNMENT);

        text.add(title);
        text.add(Box.createVerticalStrut(2));
        text.add(description);
        text.add(Box.createVerticalGlue());

        row.add(text, BorderLayout.CENTER);
        row.add(Theme.badge(ProgrammingLanguages.label(course.track()),
                Theme.PURPLE, Theme.PURPLE_SOFT), BorderLayout.EAST);
        return row;
    }

    private JPanel languagesCard(TeacherSummary summary) {
        List<LanguageInfo> languages = summary.languages();

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(17, 20, 14, 20));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        if (languages.isEmpty()) {
            body.add(DashboardKit.empty("Aucun langage attribu\u00e9."));
        } else {
            for (int i = 0; i < languages.size(); i++) {
                if (i > 0) {
                    body.add(separator());
                }
                int courseCount = 0;
                for (CourseItem course : summary.courses()) {
                    if (course.track().equals(languages.get(i).code())) {
                        courseCount++;
                    }
                }
                body.add(languageRow(languages.get(i), courseCount));
            }
        }
        body.add(Box.createVerticalGlue());

        card.add(DashboardKit.cardHeader("Vos langages",
                "Attribu\u00e9s par l'administration", null), BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        JPanel footer = Theme.roundedPanel(12, new Color(249, 249, 253), Theme.BORDER);
        footer.setLayout(new BorderLayout(10, 0));
        footer.setBorder(new EmptyBorder(8, 12, 8, 12));
        footer.setAlignmentX(LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        footer.add(DashboardKit.iconBadge("lock", Theme.PURPLE,
                Theme.PURPLE_SOFT, 24), BorderLayout.WEST);

        JPanel footerText = new JPanel();
        footerText.setOpaque(false);
        footerText.setLayout(new BoxLayout(footerText, BoxLayout.Y_AXIS));
        JLabel footerTitle = new JLabel("Un langage manquant ?");
        footerTitle.setFont(Theme.fontBold(10));
        footerTitle.setForeground(Theme.TEXT);
        footerTitle.setAlignmentX(LEFT_ALIGNMENT);
        JLabel footerNote = new JLabel("Contactez l'administration.");
        footerNote.setFont(Theme.fontPlain(9));
        footerNote.setForeground(Theme.MUTED);
        footerNote.setAlignmentX(LEFT_ALIGNMENT);
        footerText.add(footerTitle);
        footerText.add(Box.createVerticalStrut(1));
        footerText.add(footerNote);
        footer.add(footerText, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private JPanel languageRow(LanguageInfo language, int courseCount) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        row.setBorder(new EmptyBorder(5, 0, 5, 2));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        Color tone = trackColor(language.code());

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
        row.add(DashboardKit.note(courseCount + " cours"), BorderLayout.CENTER);
        row.add(Theme.badge("Actif", Theme.GREEN, Theme.GREEN_SOFT),
                BorderLayout.EAST);
        return row;
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

    private static String shorten(String text, int max) {
        if (text == null) {
            return "";
        }
        String clean = text.replaceAll("\\s+", " ").trim();
        return clean.length() <= max ? clean : clean.substring(0, max - 1) + "\u2026";
    }

    private static Color trackColor(String code) {
        return switch (code) {
            case "python" -> Theme.GREEN;
            case "javascript" -> Theme.AMBER;
            case "cpp" -> Theme.PURPLE_BRIGHT;
            default -> Theme.PURPLE;
        };
    }

    /* ============================================================== COURSES */
    public void showCourses(List<CourseItem> courses, List<LanguageInfo> languages) {
        JPanel p = Theme.page("Gestion des cours",
                "Cr\u00e9ez, modifiez et supprimez les cours de vos langages.");

        JPanel wrap = new JPanel(new BorderLayout(0, 16));
        wrap.setOpaque(false);

        JButton add = Theme.button("Ajouter un cours", "\u2795");
        add.addActionListener(e -> openCourseDialog(null, languages));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);
        toolbar.add(add);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (courses.isEmpty()) {
            JPanel empty = Theme.card();
            empty.setLayout(new BorderLayout());
            empty.add(muted("Aucun cours pour vos langages. Utilisez \u00ab Ajouter un cours \u00bb."),
                    BorderLayout.CENTER);
            empty.setAlignmentX(LEFT_ALIGNMENT);
            empty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            list.add(empty);
        } else {
            for (int i = 0; i < courses.size(); i++) {
                if (i > 0) {
                    list.add(Box.createVerticalStrut(12));
                }
                list.add(courseCard(courses.get(i), languages));
            }
        }
        list.add(Box.createVerticalGlue());

        wrap.add(toolbar, BorderLayout.NORTH);
        wrap.add(scroll(list), BorderLayout.CENTER);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    private JPanel courseCard(CourseItem course, List<LanguageInfo> languages) {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = new JLabel(course.title());
        title.setFont(Theme.fontBold(15));
        title.setForeground(Theme.TEXT);
        titleRow.add(title);
        titleRow.add(Theme.badge(ProgrammingLanguages.label(course.track()),
                Theme.PURPLE, Theme.PURPLE_SOFT));

        JLabel description = new JLabel("<html><div style='width:480px'>"
                + course.description() + "</div></html>");
        description.setFont(Theme.fontPlain(12));
        description.setForeground(Theme.MUTED);
        description.setAlignmentX(LEFT_ALIGNMENT);

        text.add(titleRow);
        text.add(Box.createVerticalStrut(6));
        text.add(description);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton edit = Theme.secondaryButton("Modifier", "\u270e");
        edit.addActionListener(e -> openCourseDialog(course, languages));
        JButton delete = Theme.dangerButton("Supprimer", "\u274c");
        delete.addActionListener(e -> confirmDeleteCourse(course));

        actions.add(edit);
        actions.add(delete);

        card.add(text, BorderLayout.CENTER);
        card.add(actions, BorderLayout.EAST);
        return card;
    }

    private void openCourseDialog(CourseItem course, List<LanguageInfo> languages) {
        JTextField title = Theme.textField();
        JTextArea description = new JTextArea(4, 32);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(Theme.fontPlain(13));
        description.setForeground(Theme.TEXT);
        description.setBackground(Color.WHITE);
        description.setBorder(new EmptyBorder(8, 10, 8, 10));

        String[] labels = languages.stream().map(LanguageInfo::label).toArray(String[]::new);
        JComboBox<String> track = Theme.comboBox(labels);

        if (course != null) {
            title.setText(course.title());
            description.setText(course.description());
            for (int i = 0; i < languages.size(); i++) {
                if (languages.get(i).code().equals(course.track())) {
                    track.setSelectedIndex(i);
                    break;
                }
            }
        }

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(6, 6, 6, 6));
        form.add(fieldLabel("Langage"));
        form.add(track);
        form.add(Box.createVerticalStrut(10));
        form.add(fieldLabel("Titre du cours"));
        form.add(title);
        form.add(Box.createVerticalStrut(10));
        form.add(fieldLabel("Description"));
        JScrollPane scroll = new JScrollPane(description);
        scroll.setAlignmentX(LEFT_ALIGNMENT);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.setPreferredSize(new Dimension(360, 100));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        form.add(scroll);

        String dialogTitle = course == null ? "Ajouter un cours" : "Modifier le cours";
        int choice = JOptionPane.showConfirmDialog(this, form, dialogTitle,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }
        if (title.getText().trim().isEmpty() || description.getText().trim().isEmpty()) {
            showInfo("Le titre et la description sont obligatoires.");
            return;
        }
        String code = languages.get(Math.max(0, track.getSelectedIndex())).code();
        if (listener != null) {
            listener.onSaveCourse(course == null ? null : course.id(), code,
                    title.getText().trim(), description.getText().trim());
        }
    }

    private void confirmDeleteCourse(CourseItem course) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Supprimer le cours \u00ab " + course.title() + " \u00bb ?",
                "Supprimer un cours", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION && listener != null) {
            listener.onDeleteCourse(course.id());
        }
    }

    /* =============================================================== LEVELS */
    public void showLevels(List<LevelInfo> levels, List<LanguageInfo> languages) {
        JPanel p = Theme.page("Niveaux",
                "Les niveaux disponibles pour chaque langage que vous enseignez.");

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (levels.isEmpty()) {
            JPanel empty = Theme.card();
            empty.setLayout(new BorderLayout());
            empty.add(muted("Aucun niveau : aucun langage ne vous est attribu\u00e9."),
                    BorderLayout.CENTER);
            empty.setAlignmentX(LEFT_ALIGNMENT);
            empty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            list.add(empty);
        }

        for (int i = 0; i < levels.size(); i++) {
            if (i > 0) {
                list.add(Box.createVerticalStrut(10));
            }
            LevelInfo level = levels.get(i);
            JPanel card = Theme.card();
            card.setLayout(new BorderLayout(16, 0));
            card.setBorder(new EmptyBorder(14, 18, 14, 18));
            card.setAlignmentX(LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 82));

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

            JLabel name = new JLabel(level.level() + " \u2014 " + level.name());
            name.setFont(Theme.fontBold(14));
            name.setForeground(Theme.TEXT);
            name.setAlignmentX(LEFT_ALIGNMENT);

            JLabel description = new JLabel(level.description());
            description.setFont(Theme.fontPlain(12));
            description.setForeground(Theme.MUTED);
            description.setAlignmentX(LEFT_ALIGNMENT);

            text.add(name);
            text.add(Box.createVerticalStrut(4));
            text.add(description);

            card.add(text, BorderLayout.CENTER);
            card.add(Theme.badge(level.status(),
                    level.unlocked() ? Theme.GREEN : Theme.AMBER,
                    level.unlocked() ? Theme.GREEN_SOFT : Theme.AMBER_SOFT),
                    BorderLayout.EAST);
            list.add(card);
        }
        list.add(Box.createVerticalGlue());

        p.add(scroll(list), BorderLayout.CENTER);
        setPage(p);
    }

    /* ========================================================== EVALUATIONS */
    public void showEvaluations(List<TeacherQuestion> questions) {
        JPanel p = Theme.page("\u00c9valuations",
                "V\u00e9rifiez les questions de test et ajustez la bonne r\u00e9ponse.");

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (questions.isEmpty()) {
            JPanel empty = Theme.card();
            empty.setLayout(new BorderLayout());
            empty.add(muted("Aucune \u00e9valuation disponible pour vos langages."),
                    BorderLayout.CENTER);
            empty.setAlignmentX(LEFT_ALIGNMENT);
            empty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            list.add(empty);
        }

        for (int i = 0; i < questions.size(); i++) {
            if (i > 0) {
                list.add(Box.createVerticalStrut(12));
            }
            list.add(evaluationCard(questions.get(i)));
        }
        list.add(Box.createVerticalGlue());

        p.add(scroll(list), BorderLayout.CENTER);
        setPage(p);
    }

    private JPanel evaluationCard(TeacherQuestion entry) {
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        head.setOpaque(false);
        head.setAlignmentX(LEFT_ALIGNMENT);
        head.add(Theme.badge(entry.trackLabel(), Theme.PURPLE, Theme.PURPLE_SOFT));

        JLabel prompt = new JLabel(entry.question().prompt());
        prompt.setFont(Theme.fontBold(15));
        prompt.setForeground(Theme.TEXT);
        prompt.setAlignmentX(LEFT_ALIGNMENT);

        JLabel hint = new JLabel(entry.question().hint());
        hint.setFont(Theme.fontPlain(12));
        hint.setForeground(Theme.MUTED);
        hint.setAlignmentX(LEFT_ALIGNMENT);

        card.add(head);
        card.add(Box.createVerticalStrut(8));
        card.add(prompt);
        card.add(Box.createVerticalStrut(4));
        card.add(hint);
        card.add(Box.createVerticalStrut(10));

        List<String> options = entry.question().options();
        for (int i = 0; i < options.size(); i++) {
            boolean correct = i == entry.question().correctIndex();
            JLabel option = new JLabel((correct ? "\u2714  " : "\u2022  ") + options.get(i));
            option.setFont(correct ? Theme.fontBold(13) : Theme.fontPlain(13));
            option.setForeground(correct ? Theme.GREEN : Theme.TEXT_DIM);
            option.setAlignmentX(LEFT_ALIGNMENT);
            option.setBorder(new EmptyBorder(2, 4, 2, 4));
            card.add(option);
        }

        JButton edit = Theme.secondaryButton("Modifier la bonne r\u00e9ponse", "\u270e");
        edit.setAlignmentX(LEFT_ALIGNMENT);
        edit.setMaximumSize(new Dimension(260, 36));
        edit.addActionListener(e -> openQuestionDialog(entry));
        card.add(Box.createVerticalStrut(12));
        card.add(edit);
        return card;
    }

    private void openQuestionDialog(TeacherQuestion entry) {
        List<String> options = entry.question().options();
        JComboBox<String> combo = Theme.comboBox(options.toArray(String[]::new));
        combo.setSelectedIndex(entry.question().correctIndex());

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(6, 6, 6, 6));
        form.add(fieldLabel(entry.question().prompt()));
        form.add(combo);

        int choice = JOptionPane.showConfirmDialog(this, form,
                "\u00c9valuation \u2014 " + entry.trackLabel(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice == JOptionPane.OK_OPTION && listener != null) {
            listener.onSaveQuestion(entry.track(), Math.max(0, combo.getSelectedIndex()));
        }
    }

    /* ============================================================= STUDENTS */
    public void showStudents(List<StudentOverview> students) {
        JPanel p = Theme.page("Suivi des apprenants",
                "Progression des apprenants inscrits dans vos langages.");

        JPanel wrap = new JPanel(new BorderLayout(0, 16));
        wrap.setOpaque(false);

        String[] columns = {"Nom", "Identifiant", "Parcours", "Progression",
                "Derni\u00e8re activit\u00e9"};
        Object[][] rows = new Object[students.size()][columns.length];
        for (int i = 0; i < students.size(); i++) {
            StudentOverview student = students.get(i);
            rows[i][0] = student.name();
            rows[i][1] = student.username();
            rows[i][2] = student.track();
            rows[i][3] = student.progress() + "%";
            rows[i][4] = student.lastActivity();
        }

        JScrollPane table = table(columns, rows);
        table.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        wrap.add(table, BorderLayout.CENTER);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    /* ================================================================ HELPERS */
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private JScrollPane scroll(JComponent content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.fontPlain(13));
        label.setForeground(Theme.MUTED);
        return label;
    }

    private static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.fontBold(12));
        label.setForeground(Theme.MUTED);
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 2, 4, 0));
        return label;
    }

    private JPanel activityRow(ActivityInfo activity) {
        Color tone = Theme.toneForeground(activity.tone());

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        row.setBorder(new EmptyBorder(6, 0, 6, 2));

        row.add(DashboardKit.iconBadge(activityIcon(activity.tone()), tone,
                Theme.toneBackground(activity.tone()), 34), BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setAlignmentX(LEFT_ALIGNMENT);
        text.add(Box.createVerticalGlue());

        JLabel t = new JLabel(activity.title());
        t.setFont(Theme.fontBold(12));
        t.setForeground(Theme.TEXT);
        t.setAlignmentX(LEFT_ALIGNMENT);

        JLabel s = new JLabel(shorten(activity.subtitle(), 66));
        s.setFont(Theme.fontPlain(10));
        s.setForeground(Theme.MUTED);
        s.setAlignmentX(LEFT_ALIGNMENT);

        text.add(t);
        text.add(Box.createVerticalStrut(2));
        text.add(s);
        text.add(Box.createVerticalGlue());

        row.add(text, BorderLayout.CENTER);
        row.add(Theme.badge(activity.badge(), tone,
                Theme.toneBackground(activity.tone())), BorderLayout.EAST);
        return row;
    }

    private static String activityIcon(Tone tone) {
        return switch (tone) {
            case GREEN -> "check";
            case AMBER -> "clock";
            case RED -> "flame";
            default -> "play";
        };
    }

    private JScrollPane table(String[] columns, Object[][] rows) {
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
        table.setFocusable(false);

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

        JScrollPane scroll = new JScrollPane(table);
        scroll.setColumnHeaderView(table.getTableHeader());
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
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
