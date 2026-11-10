package view;

import model.ActivityInfo;
import model.CourseInfo;
import model.ExerciseInfo;
import model.LanguageInfo;
import model.LevelInfo;
import model.ProfileInfo;
import model.ProgressInfo;
import model.StatInfo;
import model.StreakDay;
import model.StreakInfo;
import model.StudentSummary;
import model.StudentTexts;
import model.TestQuestion;
import model.TestResult;
import view.components.Icons;
import view.components.Sidebar;
import view.components.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.text.AttributedString;
import java.util.List;

/**
 * Student dashboard view — styled after the CodeLearn mockup: dark sidebar,
 * greeting header, hero banner, learning path cards and a results/activity row.
 */
public class StudentView extends JPanel {

    public interface Listener {
        void onNavigate(String page);

        void onLogout();

        void onOpenCourse(CourseInfo course);

        void onCompleteCourse(CourseInfo course);

        void onOpenExercise(ExerciseInfo exercise);

        void onSubmitExercise(ExerciseInfo exercise, String code);

        void onSubmitTest(int selectedOption);

        void onSaveProfile(String name, String language, String track);
    }

    private static final Color HERO_PURPLE = new Color(78, 40, 156);
    private static final Color HERO_BLUE = new Color(35, 70, 170);
    private static final Color MINT_FILL = new Color(239, 251, 247);
    private static final Color MINT_LINE = new Color(190, 235, 218);
    private static final Color LAVENDER_FILL = new Color(248, 246, 255);
    private static final Color LAVENDER_LINE = new Color(224, 216, 255);
    private static final Color NEUTRAL_FILL = new Color(247, 246, 251);
    private static final Color NEUTRAL_LINE = new Color(230, 226, 242);
    private static final Color PANEL_FILL = new Color(247, 247, 253);
    private static final Color GREEN_STRONG = new Color(7, 155, 84);
    private static final Color STREAK_ORANGE = new Color(232, 118, 20);
    private static final Color STREAK_FILL = new Color(255, 249, 238);
    private static final Color STREAK_LINE = new Color(249, 218, 171);
    private static final Color STREAK_SOFT = new Color(255, 234, 201);

    private final JPanel body = new JPanel(new BorderLayout());
    private final Sidebar sidebar;
    private final StudentTexts texts;
    private final String trackLabel;
    private Listener listener;

    public StudentView() {
        this("fr");
    }

    public StudentView(String language) {
        this(language, "Java");
    }

    public StudentView(String language, String trackLabel) {
        this.texts = StudentTexts.forLanguage(language);
        this.trackLabel = trackLabel == null || trackLabel.isBlank() ? "Java" : trackLabel;
        setLayout(new BorderLayout());
        sidebar = new Sidebar(texts.roleLabel(),
                new String[]{"Accueil", "Mes cours", "Exercices", "Tests",
                        "Ma progression", "Mon profil"},
                new String[]{texts.navHome(), texts.navCourses(), texts.navExercises(),
                        texts.navTests(), texts.navProgress(), texts.navProfile()},
                texts.logout(), texts.brandTagline(),
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

    /* ============================================================ HOME */
    public void showHome(StudentSummary summary) {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Color.WHITE);
        page.setBorder(new EmptyBorder(12, 30, 18, 30));

        JPanel content = new ScrollablePanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(header(summary));
        content.add(Box.createVerticalStrut(12));
        content.add(hero(summary));
        content.add(Box.createVerticalStrut(18));
        content.add(sectionHeader());
        content.add(Box.createVerticalStrut(12));
        content.add(levelCards(summary));
        content.add(Box.createVerticalStrut(12));
        content.add(streakCard(summary));
        content.add(Box.createVerticalStrut(14));
        content.add(bottomRow(summary));
        content.add(Box.createVerticalStrut(4));

        JScrollPane scroll = new JScrollPane(content,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(22);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(9, 0));
        scroll.getVerticalScrollBar().setUI(new ThinScrollBarUI());
        scroll.getVerticalScrollBar().setOpaque(false);

        page.add(scroll, BorderLayout.CENTER);
        setPage(page);
    }

    /* ------------------------------------------------------------- header */
    private JPanel header(StudentSummary summary) {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);
        header.setAlignmentX(LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        header.setPreferredSize(new Dimension(10, 84));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JPanel greetingRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        greetingRow.setOpaque(false);
        greetingRow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel greeting = new JLabel(summary.greeting());
        greeting.setFont(Theme.fontBold(30));
        greeting.setForeground(Theme.TEXT);
        greetingRow.add(greeting);
        greetingRow.add(Box.createHorizontalStrut(9));
        greetingRow.add(Theme.emoji("\uD83D\uDC4B", 25));

        JLabel subtitle = new JLabel(summary.subtitle());
        subtitle.setFont(Theme.fontPlain(14));
        subtitle.setForeground(Theme.MUTED);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        left.add(greetingRow);
        left.add(Box.createVerticalStrut(3));
        left.add(subtitle);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));

        LevelPill pill = new LevelPill(summary.currentLevel(), texts.levelCurrent());
        JComponent divider = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(214, 216, 232));
                g2.fillRect(getWidth() / 2, 4, 1, getHeight() - 8);
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setPreferredSize(new Dimension(1, 46));
        divider.setMinimumSize(new Dimension(1, 46));
        divider.setMaximumSize(new Dimension(1, 46));

        ProgressRing ring = new ProgressRing(summary.globalProgress());

        JPanel progressLabels = new JPanel();
        progressLabels.setOpaque(false);
        progressLabels.setLayout(new BoxLayout(progressLabels, BoxLayout.Y_AXIS));
        JLabel line1 = new JLabel(texts.progression());
        line1.setFont(Theme.fontBold(14));
        line1.setForeground(Theme.TEXT);
        JLabel line2 = new JLabel(texts.global());
        line2.setFont(Theme.fontBold(14));
        line2.setForeground(Theme.TEXT);
        progressLabels.add(line1);
        progressLabels.add(Box.createVerticalStrut(1));
        progressLabels.add(line2);

        right.add(pill);
        right.add(Box.createHorizontalStrut(18));
        right.add(divider);
        right.add(Box.createHorizontalStrut(18));
        right.add(ring);
        right.add(Box.createHorizontalStrut(10));
        right.add(progressLabels);
        for (Component component : right.getComponents()) {
            if (component instanceof JComponent) {
                ((JComponent) component).setAlignmentY(Component.CENTER_ALIGNMENT);
            }
        }

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    /* ---------------------------------------------------------------- hero */
    private JPanel hero(StudentSummary summary) {
        HeroBanner hero = new HeroBanner(trackLabel);
        hero.setOpaque(false);
        hero.setLayout(new BorderLayout(18, 0));
        hero.setBorder(new EmptyBorder(20, 34, 20, 24));
        hero.setAlignmentX(LEFT_ALIGNMENT);
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 224));
        hero.setPreferredSize(new Dimension(10, 224));

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));

        JLabel title1 = new JLabel(summary.heroTitle());
        title1.setFont(Theme.fontBold(34));
        title1.setForeground(Color.WHITE);
        title1.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title2 = new JLabel(texts.heroStep());
        title2.setFont(Theme.fontBold(34));
        title2.setForeground(Color.WHITE);
        title2.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("<html><div style='width:470px'>"
                + summary.heroSubtitle() + "</div></html>");
        subtitle.setFont(Theme.fontPlain(14));
        subtitle.setForeground(new Color(225, 226, 250));
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        Theme.RoundedButton cta = new Theme.RoundedButton(
                texts.startNow(), new Color(124, 58, 237),
                new Color(140, 78, 250), new Color(108, 43, 218),
                Color.WHITE, 20, "\u25B6", false);
        cta.setFont(Theme.fontBold(14));
        cta.setBorder(new EmptyBorder(10, 18, 10, 18));
        cta.setAlignmentX(LEFT_ALIGNMENT);
        cta.setMaximumSize(new Dimension(218, 42));
        cta.setPreferredSize(new Dimension(218, 42));
        cta.addActionListener(e -> requestNavigate("Mes cours"));

        copy.add(Box.createVerticalGlue());
        copy.add(title1);
        copy.add(title2);
        copy.add(Box.createVerticalStrut(4));
        copy.add(subtitle);
        copy.add(Box.createVerticalStrut(12));
        copy.add(cta);
        copy.add(Box.createVerticalGlue());

        hero.add(copy, BorderLayout.CENTER);
        hero.add(new TrackLogo(trackLabel, trackAccent(trackLabel)), BorderLayout.EAST);
        return hero;
    }

    /* ------------------------------------------------------ section title */
    private JPanel sectionHeader() {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.setPreferredSize(new Dimension(10, 28));

        JPanel title = new JPanel();
        title.setOpaque(false);
        title.setLayout(new BoxLayout(title, BoxLayout.X_AXIS));
        JComponent book = Icons.view("book", 22, Theme.PURPLE_BRIGHT);
        JLabel label = new JLabel(texts.sectionTitle());
        label.setFont(Theme.fontBold(20));
        label.setForeground(Theme.TEXT);
        title.add(book);
        title.add(Box.createHorizontalStrut(10));
        title.add(label);

        JComponent line = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(226, 228, 240));
                g.fillRect(0, getHeight() / 2, getWidth(), 1);
            }
        };
        line.setOpaque(false);

        JLabel link = new JLabel(texts.seeAll());
        link.setFont(Theme.fontBold(13));
        link.setForeground(Theme.PURPLE_LINK);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestNavigate("Mes cours");
            }
        });

        row.add(title, BorderLayout.WEST);
        row.add(line, BorderLayout.CENTER);
        row.add(link, BorderLayout.EAST);
        return row;
    }

    /* --------------------------------------------------------- level cards */
    private JPanel levelCards(StudentSummary summary) {
        List<LevelInfo> levels = summary.levels();
        JPanel grid = new JPanel(new GridLayout(1, Math.max(1, levels.size()), 16, 0));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 236));
        grid.setPreferredSize(new Dimension(10, 236));

        for (int i = 0; i < levels.size(); i++) {
            grid.add(new LevelCard(levels.get(i), i));
        }
        return grid;
    }

    private class LevelCard extends JPanel {
        private final LevelInfo level;
        private final int index;
        private final Color fill;
        private final Color line;

        LevelCard(LevelInfo level, int index) {
            this.level = level;
            this.index = index;
            this.fill = index == 0 ? MINT_FILL : index == 1 ? LAVENDER_FILL : NEUTRAL_FILL;
            this.line = index == 0 ? MINT_LINE : index == 1 ? LAVENDER_LINE : NEUTRAL_LINE;

            setOpaque(false);
            setLayout(new BorderLayout(0, 8));
            setBorder(new EmptyBorder(16, 20, 16, 20));

            JPanel top = new JPanel(new BorderLayout(8, 0));
            top.setOpaque(false);
            top.setPreferredSize(new Dimension(10, 74));

            top.add(new IconBadge(levelIcon(index), badgeFill(index)), BorderLayout.WEST);

            JPanel heading = new JPanel();
            heading.setOpaque(false);
            heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
            heading.add(Box.createVerticalGlue());
            JLabel eyebrow = new JLabel(level.level());
            eyebrow.setFont(Theme.fontPlain(12));
            eyebrow.setForeground(Theme.MUTED);
            eyebrow.setAlignmentX(LEFT_ALIGNMENT);
            JLabel name = new JLabel(level.name());
            name.setFont(Theme.fontBold(18));
            name.setForeground(Theme.TEXT);
            name.setAlignmentX(LEFT_ALIGNMENT);
            heading.add(eyebrow);
            heading.add(Box.createVerticalStrut(3));
            heading.add(name);
            heading.add(Box.createVerticalGlue());
            top.add(heading, BorderLayout.CENTER);

            JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            statusWrap.setOpaque(false);
            statusWrap.add(level.unlocked()
                    ? Icons.view("check", 22, GREEN_STRONG)
                    : Icons.view("lock", 21, new Color(100, 116, 139)));
            top.add(statusWrap, BorderLayout.EAST);

            WrappedText description = new WrappedText(level.description(),
                    Theme.fontPlain(13), Theme.TEXT_DIM);

            int percent = level.unlocked() ? 100 : 0;
            JPanel progressRow = new JPanel(new BorderLayout(10, 0));
            progressRow.setOpaque(false);
            JComponent bar = Theme.progressBar(percent,
                    level.unlocked() ? GREEN_STRONG : new Color(167, 156, 205),
                    index == 0 ? new Color(210, 239, 226) : new Color(231, 227, 242));
            bar.setPreferredSize(new Dimension(10, 8));
            JLabel pct = new JLabel(percent + "%");
            pct.setFont(Theme.fontBold(12));
            pct.setForeground(level.unlocked() ? new Color(7, 120, 70) : Theme.TEXT_DIM);
            progressRow.add(bar, BorderLayout.CENTER);
            progressRow.add(pct, BorderLayout.EAST);

            Theme.RoundedButton button;
            if (level.unlocked()) {
                button = new Theme.RoundedButton(texts.openCourse(),
                        GREEN_STRONG, new Color(5, 140, 75), new Color(4, 120, 65),
                        Color.WHITE, 18);
                button.addActionListener(e -> requestNavigate("Mes cours"));
            } else {
                button = new Theme.RoundedButton(texts.locked(),
                        new Color(232, 226, 250), new Color(232, 226, 250),
                        new Color(232, 226, 250), new Color(122, 108, 160), 18);
                button.setDisabledPalette(new Color(232, 226, 250), new Color(122, 108, 160));
                button.setEnabled(false);
            }
            button.setFont(Theme.fontBold(12));
            button.setBorder(new EmptyBorder(8, 16, 8, 16));
            button.setPreferredSize(new Dimension(196, 36));
            button.setMaximumSize(new Dimension(196, 36));

            JPanel buttonWrap = new JPanel(new GridBagLayout());
            buttonWrap.setOpaque(false);
            buttonWrap.add(button);

            JPanel bottom = new JPanel(new BorderLayout(0, 10));
            bottom.setOpaque(false);
            bottom.add(progressRow, BorderLayout.NORTH);
            bottom.add(buttonWrap, BorderLayout.CENTER);

            add(top, BorderLayout.NORTH);
            add(description, BorderLayout.CENTER);
            add(bottom, BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(new Color(0, 0, 0, 16));
            g2.fillRoundRect(3, 4, w - 1, h - 2, 18, 18);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w - 1, h - 4, 18, 18);
            g2.setColor(line);
            g2.drawRoundRect(0, 0, w - 1, h - 4, 18, 18);

            if (index == 2) {
                Color[] colors = {
                        new Color(167, 139, 250, 48),
                        new Color(59, 130, 246, 38),
                        new Color(16, 185, 129, 32)
                };
                int[][] dots = {{26, 52, 4}, {64, 128, 3}, {92, 174, 4},
                        {138, 60, 3}, {176, 112, 4}, {206, 46, 3},
                        {242, 150, 4}, {272, 88, 3}, {300, 190, 4},
                        {60, 208, 3}, {184, 214, 3}};
                for (int i = 0; i < dots.length; i++) {
                    g2.setColor(colors[i % colors.length]);
                    g2.fillOval(dots[i][0], dots[i][1], dots[i][2], dots[i][2]);
                }
            }
            g2.dispose();
        }
    }

    private static JComponent levelIcon(int index) {
        if (index == 0) {
            return Theme.emoji("\uD83D\uDE80", 31);
        }
        if (index == 1) {
            JLabel gear = new JLabel("\u2699");
            gear.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 42));
            gear.setForeground(new Color(45, 98, 214));
            return gear;
        }
        return Icons.view("diamond", 38, new Color(59, 130, 246));
    }

    private static Color badgeFill(int index) {
        if (index == 0) {
            return new Color(205, 239, 247);
        }
        if (index == 1) {
            return new Color(233, 227, 255);
        }
        return new Color(228, 232, 245);
    }

    /** Wraps a paragraph precisely to the current component width. */
    private static class WrappedText extends JComponent {
        private final String text;
        private final Font font;
        private final Color color;

        WrappedText(String text, Font font, Color color) {
            this.text = text == null ? "" : text;
            this.font = font;
            this.color = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(font);
            g2.setColor(color);

            AttributedString attributed = new AttributedString(text);
            attributed.addAttribute(TextAttribute.FONT, font);
            LineBreakMeasurer measurer = new LineBreakMeasurer(
                    attributed.getIterator(), g2.getFontRenderContext());
            float width = Math.max(20, getWidth());
            float baseline = g2.getFontMetrics(font).getAscent();
            while (measurer.getPosition() < text.length()) {
                TextLayout layout = measurer.nextLayout(width);
                if (layout == null) {
                    break;
                }
                baseline += layout.getAscent();
                if (baseline - layout.getAscent() > getHeight()) {
                    break;
                }
                layout.draw(g2, 0, baseline);
                baseline += layout.getDescent() + layout.getLeading();
            }
            g2.dispose();
        }
    }

    /** Soft circular background around a level icon. */
    private static class IconBadge extends JPanel {        private final Color fill;

        IconBadge(JComponent content, Color fill) {
            this.fill = fill;
            setOpaque(false);
            setLayout(new GridBagLayout());
            setPreferredSize(new Dimension(68, 68));
            setMinimumSize(new Dimension(68, 68));
            setMaximumSize(new Dimension(68, 68));
            add(content);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.setColor(new Color(255, 255, 255, 105));
            g2.fillOval(5, 5, getWidth() - 11, getHeight() / 2);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /* ---------------------------------------------------------- streak */
    private JPanel streakCard(StudentSummary summary) {
        StreakInfo streak = summary.streak();
        JPanel card = Theme.roundedPanel(16, STREAK_FILL, STREAK_LINE);
        card.setLayout(new BorderLayout(18, 0));
        card.setBorder(new EmptyBorder(8, 16, 8, 18));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        card.setPreferredSize(new Dimension(10, 64));

        JPanel identity = new JPanel(new BorderLayout(12, 0));
        identity.setOpaque(false);
        identity.add(flameBadge(), BorderLayout.WEST);

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        copy.add(Box.createVerticalGlue());

        JLabel title = new JLabel(streak.title());
        title.setFont(Theme.fontBold(15));
        title.setForeground(Theme.TEXT);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel message = new JLabel(streak.message());
        message.setFont(Theme.fontPlain(11));
        message.setForeground(Theme.MUTED);
        message.setAlignmentX(LEFT_ALIGNMENT);

        copy.add(title);
        copy.add(Box.createVerticalStrut(2));
        copy.add(message);
        copy.add(Box.createVerticalGlue());
        identity.add(copy, BorderLayout.CENTER);

        JPanel week = new JPanel();
        week.setOpaque(false);
        week.setLayout(new BoxLayout(week, BoxLayout.Y_AXIS));
        JLabel caption = new JLabel(streak.weekCaption());
        caption.setFont(Theme.fontBold(10));
        caption.setForeground(Theme.MUTED);
        caption.setAlignmentX(LEFT_ALIGNMENT);

        JPanel days = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        days.setOpaque(false);
        days.setAlignmentX(LEFT_ALIGNMENT);
        for (StreakDay day : streak.week()) {
            days.add(new DayChip(day));
        }
        week.add(Box.createVerticalGlue());
        week.add(caption);
        week.add(Box.createVerticalStrut(5));
        week.add(days);
        week.add(Box.createVerticalGlue());

        JPanel count = new JPanel();
        count.setOpaque(false);
        count.setLayout(new BoxLayout(count, BoxLayout.Y_AXIS));
        count.setPreferredSize(new Dimension(124, 44));
        count.setMinimumSize(new Dimension(124, 44));
        count.add(Box.createVerticalGlue());

        JLabel current = new JLabel(streak.currentDays() + " " + texts.daysUnit());
        current.setFont(Theme.fontBold(25));
        current.setForeground(STREAK_ORANGE);
        current.setAlignmentX(RIGHT_ALIGNMENT);
        current.setMaximumSize(new Dimension(124, 31));

        JLabel best = new JLabel(texts.recordLabel() + " : "
                + streak.bestDays() + " " + texts.daysUnit());
        best.setFont(Theme.fontPlain(11));
        best.setForeground(Theme.MUTED);
        best.setAlignmentX(RIGHT_ALIGNMENT);
        best.setPreferredSize(new Dimension(124, 15));
        best.setMinimumSize(new Dimension(124, 15));
        best.setMaximumSize(new Dimension(124, 15));

        count.add(current);
        count.add(Box.createVerticalStrut(1));
        count.add(best);
        count.add(Box.createVerticalGlue());

        card.add(identity, BorderLayout.WEST);
        card.add(week, BorderLayout.CENTER);
        card.add(count, BorderLayout.EAST);
        card.setToolTipText(streak.currentDays() + " " + texts.daysUnit()
                + " \u2014 " + texts.recordLabel().toLowerCase()
                + " : " + streak.bestDays() + " " + texts.daysUnit());
        return card;
    }

    private static JComponent flameBadge() {
        JComponent badge = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(42, Math.min(getWidth(), getHeight()));
                int x = (getWidth() - d) / 2;
                int y = (getHeight() - d) / 2;
                g2.setColor(STREAK_SOFT);
                g2.fillOval(x, y, d, d);
                g2.setColor(STREAK_ORANGE);
                g2.fillOval(x + 3, y + 3, d - 6, d - 6);
                int icon = Math.round(d * 0.50f);
                Icons.paint(g, "flame",
                        x + (d - icon) / 2, y + (d - icon) / 2,
                        icon, Color.WHITE);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(42, 42));
        badge.setMinimumSize(new Dimension(42, 42));
        badge.setMaximumSize(new Dimension(42, 42));
        return badge;
    }

    private static class DayChip extends JComponent {
        private final StreakDay day;

        DayChip(StreakDay day) {
            this.day = day;
            setOpaque(false);
            setPreferredSize(new Dimension(28, 28));
            setMinimumSize(new Dimension(28, 28));
            setMaximumSize(new Dimension(28, 28));
            setToolTipText(day.label() + (day.completed()
                    ? " \u2014 activit\u00e9 termin\u00e9e"
                    : " \u2014 pas encore d'activit\u00e9"));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int d = Math.min(getWidth(), getHeight()) - 2;
            int x = (getWidth() - d) / 2;
            int y = (getHeight() - d) / 2;

            g2.setColor(day.completed() ? STREAK_ORANGE : STREAK_SOFT);
            g2.fillOval(x, y, d, d);
            if (day.today()) {
                g2.setColor(new Color(178, 77, 0));
                g2.setStroke(new BasicStroke(1.7f));
                g2.drawOval(x + 1, y + 1, d - 2, d - 2);
            }

            String label = day.label();
            g2.setFont(Theme.fontBold(11));
            FontMetrics metrics = g2.getFontMetrics();
            int tx = x + (d - metrics.stringWidth(label)) / 2;
            int ty = y + (d - metrics.getAscent()) / 2 + metrics.getAscent() - 2;
            g2.setColor(day.completed() ? Color.WHITE : new Color(176, 117, 55));
            g2.drawString(label, tx, ty);
            g2.dispose();
        }
    }

    /* ------------------------------------------------------ bottom row */
    private JPanel bottomRow(StudentSummary summary) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 172));
        row.setPreferredSize(new Dimension(10, 172));

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;

        c.gridx = 0;
        c.weightx = 0.60;
        c.insets = new Insets(0, 0, 0, 16);
        row.add(resultsCard(summary), c);

        c.gridx = 1;
        c.weightx = 0.40;
        c.insets = new Insets(0, 0, 0, 0);
        row.add(activitiesCard(summary), c);
        return row;
    }

    private JPanel resultsCard(StudentSummary summary) {
        JPanel card = Theme.roundedPanel(16, PANEL_FILL, Theme.BORDER);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(16, 20, 14, 16));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.add(Icons.view("chart", 18, Theme.PURPLE_BRIGHT));
        header.add(Box.createHorizontalStrut(8));
        JLabel title = new JLabel(texts.resultsTitle());
        title.setFont(Theme.fontBold(16));
        title.setForeground(Theme.TEXT);
        header.add(title);

        JPanel stats = new JPanel(new GridLayout(1,
                Math.max(1, summary.stats().size()), 0, 0));
        stats.setOpaque(false);
        List<StatInfo> items = summary.stats();
        for (int i = 0; i < items.size(); i++) {
            stats.add(new StatColumn(items.get(i), i));
        }

        card.add(header, BorderLayout.NORTH);
        card.add(stats, BorderLayout.CENTER);
        return card;
    }

    private static class StatColumn extends JPanel {
        private final boolean first;

        StatColumn(StatInfo stat, int index) {
            this.first = index == 0;
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(2, index == 0 ? 0 : 18, 0, 8));

            JLabel label = new JLabel(stat.label());
            label.setFont(Theme.fontPlain(12));
            label.setForeground(Theme.MUTED);
            label.setAlignmentX(LEFT_ALIGNMENT);

            JPanel valueRow = new JPanel();
            valueRow.setOpaque(false);
            valueRow.setLayout(new BoxLayout(valueRow, BoxLayout.X_AXIS));
            valueRow.setAlignmentX(LEFT_ALIGNMENT);
            boolean clock = stat.label() != null
                    && stat.label().toLowerCase().contains("temps");
            if (clock) {
                JComponent icon = Icons.view("clock", 19, Theme.PURPLE);
                valueRow.add(icon);
                valueRow.add(Box.createHorizontalStrut(7));
            }
            JLabel value = new JLabel(stat.value());
            value.setFont(Theme.fontBold(clock ? 24 : 29));
            value.setForeground(Theme.toneForeground(stat.tone()));
            valueRow.add(value);

            add(label);
            add(Box.createVerticalStrut(8));
            add(valueRow);
            add(Box.createVerticalGlue());
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!first) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(230, 232, 242));
                g2.fillRect(0, 6, 1, getHeight() - 12);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private JPanel activitiesCard(StudentSummary summary) {
        JPanel card = Theme.roundedPanel(16, PANEL_FILL, Theme.BORDER);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(16, 20, 14, 20));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.add(Icons.view("trophy", 19, new Color(245, 158, 11)));
        header.add(Box.createHorizontalStrut(8));
        JLabel title = new JLabel(texts.activitiesTitle());
        title.setFont(Theme.fontBold(16));
        title.setForeground(Theme.TEXT);
        header.add(title);

        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        List<ActivityInfo> activities = summary.activities();
        for (int i = 0; i < activities.size(); i++) {
            if (i > 0) {
                rows.add(Box.createVerticalStrut(8));
            }
            rows.add(activityRow(activities.get(i)));
        }
        rows.add(Box.createVerticalGlue());

        card.add(header, BorderLayout.NORTH);
        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    private JPanel activityRow(ActivityInfo activity) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.setPreferredSize(new Dimension(10, 28));

        JComponent check = Icons.view("check", 19, GREEN_STRONG);
        JLabel title = new JLabel(activity.title());
        title.setFont(Theme.fontPlain(13));
        title.setForeground(Theme.TEXT);
        JLabel time = new JLabel(activity.subtitle());
        time.setFont(Theme.fontPlain(12));
        time.setForeground(Theme.MUTED);

        row.add(check, BorderLayout.WEST);
        row.add(title, BorderLayout.CENTER);
        row.add(time, BorderLayout.EAST);
        return row;
    }

    /* ======================================================== COURSES */
    public void showCourses(List<CourseInfo> courses) {
        JPanel p = Theme.page("Mes cours", "Contenus disponibles pour votre niveau.");
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (int i = 0; i < courses.size(); i++) {
            if (i > 0) {
                list.add(Box.createVerticalStrut(12));
            }
            list.add(courseCard(courses.get(i)));
        }
        list.add(Box.createVerticalGlue());

        p.add(list, BorderLayout.CENTER);
        setPage(p);
    }

    public void showExercises(List<ExerciseInfo> exercises) {
        JPanel p = Theme.page("Exercices",
                "Entra\u00eenez-vous avec des activit\u00e9s guid\u00e9es.");
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (int i = 0; i < exercises.size(); i++) {
            if (i > 0) {
                list.add(Box.createVerticalStrut(12));
            }
            list.add(exerciseCard(exercises.get(i)));
        }
        list.add(Box.createVerticalGlue());

        p.add(list, BorderLayout.CENTER);
        setPage(p);
    }

    public void showTest(TestQuestion question) {
        JPanel p = Theme.page("Test de validation",
                "QCM de validation de votre parcours.");

        JPanel q = Theme.card();
        q.setLayout(new BoxLayout(q, BoxLayout.Y_AXIS));

        JLabel qTitle = new JLabel(question.prompt());
        qTitle.setFont(Theme.fontBold(16));
        qTitle.setForeground(Theme.TEXT);
        qTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel qSub = new JLabel(question.hint());
        qSub.setFont(Theme.fontPlain(13));
        qSub.setForeground(Theme.MUTED);
        qSub.setAlignmentX(LEFT_ALIGNMENT);

        q.add(qTitle);
        q.add(Box.createVerticalStrut(6));
        q.add(qSub);
        q.add(Box.createVerticalStrut(20));

        ButtonGroup group = new ButtonGroup();
        int[] chosen = {-1};
        List<String> options = question.options();
        for (int i = 0; i < options.size(); i++) {
            int index = i;
            JRadioButton r = new JRadioButton(options.get(i));
            r.setOpaque(false);
            r.setFont(Theme.fontPlain(14));
            r.setForeground(Theme.TEXT);
            r.setFocusPainted(false);
            r.setAlignmentX(LEFT_ALIGNMENT);
            r.setBorder(new EmptyBorder(6, 8, 6, 8));
            r.addActionListener(e -> chosen[0] = index);
            group.add(r);
            q.add(r);
            q.add(Box.createVerticalStrut(8));
        }

        JButton submit = Theme.button("Valider mes r\u00e9ponses", "\u2705");
        submit.setAlignmentX(LEFT_ALIGNMENT);
        submit.setMaximumSize(new Dimension(220, 44));
        submit.addActionListener(e -> {
            if (listener != null) {
                listener.onSubmitTest(chosen[0]);
            }
        });

        q.add(Box.createVerticalStrut(24));
        q.add(submit);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(q, BorderLayout.NORTH);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    public void showProgress(ProgressInfo progress) {
        JPanel p = Theme.page("Ma progression", "Vue synth\u00e9tique de votre parcours.");

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JPanel c = Theme.card();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        heading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel t = new JLabel("Progression du parcours");
        t.setFont(Theme.fontBold(15));
        t.setForeground(Theme.TEXT);

        JLabel percent = new JLabel(progress.label());
        percent.setFont(Theme.fontBold(16));
        percent.setForeground(Theme.PURPLE);

        heading.add(t, BorderLayout.WEST);
        heading.add(percent, BorderLayout.EAST);

        JComponent bar = Theme.progressBar(progress.percent(), Theme.PURPLE,
                new Color(234, 236, 245));
        bar.setPreferredSize(new Dimension(10, 10));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        bar.setAlignmentX(LEFT_ALIGNMENT);

        JLabel note = new JLabel(progress.note());
        note.setFont(Theme.fontPlain(13));
        note.setForeground(Theme.MUTED);
        note.setAlignmentX(LEFT_ALIGNMENT);

        c.add(heading);
        c.add(Box.createVerticalStrut(16));
        c.add(bar);
        c.add(Box.createVerticalStrut(14));
        c.add(note);

        wrap.add(c, BorderLayout.NORTH);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    public void showProfile(ProfileInfo profile, List<LanguageInfo> languages,
                            List<LanguageInfo> tracks) {
        JPanel p = Theme.page("Mon profil", "Vos informations personnelles.");

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JPanel c = Theme.card();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

        String initial = profile.name() == null || profile.name().isEmpty()
                ? "?" : profile.name().substring(0, 1).toUpperCase();
        JLabel avatar = new JLabel(initial, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PURPLE);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setOpaque(false);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(Theme.fontBold(26));
        avatar.setPreferredSize(new Dimension(64, 64));
        avatar.setMaximumSize(new Dimension(64, 64));
        avatar.setAlignmentX(LEFT_ALIGNMENT);

        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        names.setAlignmentX(LEFT_ALIGNMENT);

        JLabel name = new JLabel(profile.name());
        name.setFont(Theme.fontBold(20));
        name.setForeground(Theme.TEXT);
        name.setAlignmentX(LEFT_ALIGNMENT);

        JLabel role = new JLabel(profile.role() + " \u00b7 Parcours " + profile.path());
        role.setFont(Theme.fontPlain(13));
        role.setForeground(Theme.MUTED);
        role.setAlignmentX(LEFT_ALIGNMENT);

        names.add(Box.createVerticalGlue());
        names.add(name);
        names.add(Box.createVerticalStrut(4));
        names.add(role);
        names.add(Box.createVerticalGlue());

        JPanel header = new JPanel(new BorderLayout(18, 0));
        header.setOpaque(false);
        header.add(avatar, BorderLayout.WEST);
        header.add(names, BorderLayout.CENTER);
        header.setAlignmentX(LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        JPanel grid = new JPanel(new GridLayout(0, 2, 24, 14));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setPreferredSize(new Dimension(580, 140));
        grid.setMaximumSize(new Dimension(580, 140));
        grid.add(muted("Nom"));
        grid.add(value(profile.name()));
        grid.add(muted("R\u00f4le"));
        grid.add(value(profile.role()));
        grid.add(muted("Parcours"));
        grid.add(value(profile.path()));
        grid.add(muted("Niveau actuel"));
        grid.add(value(profile.currentLevel()));

        JButton edit = Theme.secondaryButton("Modifier mon profil", "\u270e");
        edit.setAlignmentX(LEFT_ALIGNMENT);
        edit.setMaximumSize(new Dimension(220, 44));
        edit.addActionListener(e -> openProfileDialog(profile, languages, tracks));

        c.add(header);
        c.add(Box.createVerticalStrut(22));
        c.add(grid);
        c.add(Box.createVerticalStrut(24));
        c.add(edit);

        wrap.add(c, BorderLayout.NORTH);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /* ------------------------------------------------------ course detail */
    public void showCourseDetail(CourseInfo course, boolean completed) {
        JPanel p = Theme.page(course.title(), "Cours de votre parcours.");
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel desc = new JLabel("<html><div style='width:620px'>"
                + course.description() + "</div></html>");
        desc.setFont(Theme.fontPlain(14));
        desc.setForeground(Theme.TEXT_DIM);
        desc.setAlignmentX(LEFT_ALIGNMENT);
        card.add(desc);
        card.add(Box.createVerticalStrut(18));

        String[][] lessons = {
                {"1", "D\u00e9couverte et installation"},
                {"2", "Variables et types de donn\u00e9es"},
                {"3", "Exercices guid\u00e9s et validation"}
        };
        for (String[] lesson : lessons) {
            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setOpaque(false);
            row.setAlignmentX(LEFT_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            JLabel number = Theme.badge(lesson[0], Theme.PURPLE, Theme.PURPLE_SOFT);
            JLabel title = new JLabel(lesson[1]);
            title.setFont(Theme.fontPlain(14));
            title.setForeground(Theme.TEXT);
            row.add(number, BorderLayout.WEST);
            row.add(title, BorderLayout.CENTER);
            card.add(row);
            card.add(Box.createVerticalStrut(8));
        }

        card.add(Box.createVerticalStrut(14));
        if (completed) {
            JLabel done = Theme.badge("Cours d\u00e9j\u00e0 termin\u00e9", Theme.GREEN, Theme.GREEN_SOFT);
            done.setAlignmentX(LEFT_ALIGNMENT);
            card.add(done);
        } else {
            JButton finish = Theme.button("Terminer ce cours", "\u2705");
            finish.setAlignmentX(LEFT_ALIGNMENT);
            finish.setMaximumSize(new Dimension(230, 44));
            finish.addActionListener(e -> {
                if (listener != null) {
                    listener.onCompleteCourse(course);
                }
            });
            card.add(finish);
        }

        JButton back = Theme.secondaryButton("Retour aux cours", "\u2190");
        back.setAlignmentX(LEFT_ALIGNMENT);
        back.setMaximumSize(new Dimension(200, 40));
        back.addActionListener(e -> requestNavigate("Mes cours"));
        card.add(Box.createVerticalStrut(10));
        card.add(back);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card, BorderLayout.NORTH);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    /* ---------------------------------------------------- exercise detail */
    public void showExerciseDetail(ExerciseInfo exercise) {
        JPanel p = Theme.page(exercise.title(), "R\u00e9alisez l'exercice puis v\u00e9rifiez votre solution.");
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel desc = new JLabel("<html><div style='width:620px'>"
                + exercise.description() + "</div></html>");
        desc.setFont(Theme.fontPlain(14));
        desc.setForeground(Theme.TEXT_DIM);
        desc.setAlignmentX(LEFT_ALIGNMENT);

        JTextArea editor = new JTextArea(9, 60);
        editor.setFont(new Font("Consolas", Font.PLAIN, 13));
        editor.setForeground(Theme.TEXT);
        editor.setBackground(new Color(248, 249, 253));
        editor.setCaretColor(Theme.PURPLE);
        editor.setBorder(new EmptyBorder(12, 14, 12, 14));
        JScrollPane scroll = new JScrollPane(editor);
        scroll.setAlignmentX(LEFT_ALIGNMENT);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));

        JButton check = Theme.button("V\u00e9rifier ma solution", "\u2705");
        check.setAlignmentX(LEFT_ALIGNMENT);
        check.setMaximumSize(new Dimension(230, 44));
        check.addActionListener(e -> {
            if (listener != null) {
                listener.onSubmitExercise(exercise, editor.getText());
            }
        });

        JButton back = Theme.secondaryButton("Retour aux exercices", "\u2190");
        back.setAlignmentX(LEFT_ALIGNMENT);
        back.setMaximumSize(new Dimension(220, 40));
        back.addActionListener(e -> requestNavigate("Exercices"));

        card.add(desc);
        card.add(Box.createVerticalStrut(16));
        card.add(scroll);
        card.add(Box.createVerticalStrut(16));
        card.add(check);
        card.add(Box.createVerticalStrut(10));
        card.add(back);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card, BorderLayout.NORTH);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    public void showExerciseResult(ExerciseInfo exercise, TestResult result) {
        JPanel p = Theme.page(exercise.title(),
                result.correct() ? "Exercice valid\u00e9." : "Solution \u00e0 am\u00e9liorer.");
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(result.correct() ? "Bravo !" : "Pas encore tout \u00e0 fait");
        title.setFont(Theme.fontBold(22));
        title.setForeground(result.correct() ? Theme.GREEN : Theme.AMBER);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel message = new JLabel("<html><div style='width:620px'>"
                + result.message() + "</div></html>");
        message.setFont(Theme.fontPlain(14));
        message.setForeground(Theme.TEXT_DIM);
        message.setAlignmentX(LEFT_ALIGNMENT);

        JButton retry = Theme.secondaryButton(
                result.correct() ? "Revoir l'exercice" : "R\u00e9essayer", "\u21bb");
        retry.setAlignmentX(LEFT_ALIGNMENT);
        retry.setMaximumSize(new Dimension(220, 40));
        retry.addActionListener(e -> showExerciseDetail(exercise));

        JButton back = Theme.button("Retour aux exercices", "\u2190");
        back.setAlignmentX(LEFT_ALIGNMENT);
        back.setMaximumSize(new Dimension(230, 44));
        back.addActionListener(e -> requestNavigate("Exercices"));

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(message);
        card.add(Box.createVerticalStrut(20));
        card.add(retry);
        card.add(Box.createVerticalStrut(10));
        card.add(back);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card, BorderLayout.NORTH);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    public void showTestResult(TestResult result, TestQuestion question) {
        JPanel p = Theme.page("R\u00e9sultat du test",
                result.correct() ? "Test r\u00e9ussi." : "Test \u00e0 retravailler.");
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel score = new JLabel(result.score() + "%");
        score.setFont(Theme.fontBold(46));
        score.setForeground(result.correct() ? Theme.GREEN : Theme.AMBER);
        score.setAlignmentX(LEFT_ALIGNMENT);

        JLabel message = new JLabel("<html><div style='width:620px'>"
                + result.message() + "</div></html>");
        message.setFont(Theme.fontPlain(14));
        message.setForeground(Theme.TEXT_DIM);
        message.setAlignmentX(LEFT_ALIGNMENT);

        JButton again = Theme.button("Refaire le test", "\u21bb");
        again.setAlignmentX(LEFT_ALIGNMENT);
        again.setMaximumSize(new Dimension(200, 44));
        again.addActionListener(e -> showTest(question));

        JButton back = Theme.secondaryButton("Retour \u00e0 l'accueil", "\u2190");
        back.setAlignmentX(LEFT_ALIGNMENT);
        back.setMaximumSize(new Dimension(220, 40));
        back.addActionListener(e -> requestNavigate("Accueil"));

        card.add(score);
        card.add(Box.createVerticalStrut(10));
        card.add(message);
        card.add(Box.createVerticalStrut(20));
        card.add(again);
        card.add(Box.createVerticalStrut(10));
        card.add(back);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card, BorderLayout.NORTH);
        p.add(wrap, BorderLayout.CENTER);
        setPage(p);
    }

    /* --------------------------------------------------------- profile edit */
    private void openProfileDialog(ProfileInfo profile, List<LanguageInfo> languages,
                                   List<LanguageInfo> tracks) {
        if (languages.isEmpty() || tracks.isEmpty()) {
            showInfo("Aucun langage disponible pour le moment.");
            return;
        }

        JTextField name = Theme.textField();
        name.setText(profile.name());

        String[] languageLabels = languages.stream().map(LanguageInfo::label)
                .toArray(String[]::new);
        String[] trackLabels = tracks.stream().map(LanguageInfo::label)
                .toArray(String[]::new);
        JComboBox<String> language = Theme.comboBox(languageLabels);
        JComboBox<String> track = Theme.comboBox(trackLabels);
        selectCode(language, languages, profile.language());
        selectCode(track, tracks, profile.track());

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(6, 6, 6, 6));
        form.add(dialogLabel("Nom"));
        form.add(name);
        form.add(Box.createVerticalStrut(10));
        form.add(dialogLabel("Langue de l'interface"));
        form.add(language);
        form.add(Box.createVerticalStrut(10));
        form.add(dialogLabel("Langage de programmation"));
        form.add(track);

        int choice = JOptionPane.showConfirmDialog(this, form, "Modifier mon profil",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }
        if (name.getText().trim().isEmpty()) {
            showInfo("Le nom est obligatoire.");
            return;
        }
        String languageCode = languages.get(Math.max(0, language.getSelectedIndex())).code();
        String trackCode = tracks.get(Math.max(0, track.getSelectedIndex())).code();
        if (listener != null) {
            listener.onSaveProfile(name.getText().trim(), languageCode, trackCode);
        }
    }

    private static JLabel dialogLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.fontBold(12));
        label.setForeground(Theme.MUTED);
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 2, 4, 0));
        return label;
    }

    private static void selectCode(JComboBox<String> combo, List<LanguageInfo> options,
                                   String code) {
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).code().equals(code)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    /* ============================================================== cards */
    private JPanel courseCard(CourseInfo course) {
        JPanel c = Theme.card();
        c.setLayout(new BorderLayout(18, 10));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setAlignmentX(LEFT_ALIGNMENT);

        JLabel t = new JLabel(course.title());
        t.setFont(Theme.fontBold(15));
        t.setForeground(Theme.TEXT);
        t.setAlignmentX(LEFT_ALIGNMENT);

        JLabel d = new JLabel(course.description());
        d.setFont(Theme.fontPlain(13));
        d.setForeground(Theme.TEXT_DIM);
        d.setAlignmentX(LEFT_ALIGNMENT);

        info.add(t);
        info.add(Box.createVerticalStrut(4));
        info.add(d);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel badge = Theme.badge(course.state(), Theme.toneForeground(course.tone()),
                Theme.toneBackground(course.tone()));
        badge.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(badge);
        right.add(Box.createVerticalStrut(8));

        JButton open = Theme.secondaryButton("Ouvrir", "\u2192");
        open.setAlignmentX(Component.RIGHT_ALIGNMENT);
        open.setMaximumSize(new Dimension(130, 34));
        open.addActionListener(e -> {
            if (listener != null) {
                listener.onOpenCourse(course);
            }
        });
        right.add(open);

        c.add(info, BorderLayout.CENTER);
        c.add(right, BorderLayout.EAST);
        c.setAlignmentX(LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        return c;
    }

    private JPanel exerciseCard(ExerciseInfo exercise) {
        JPanel c = Theme.card();
        c.setLayout(new BorderLayout(18, 14));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setAlignmentX(LEFT_ALIGNMENT);

        JLabel badge = Theme.badge(exercise.completed() ? "Termin\u00e9" : exercise.level(),
                exercise.completed() ? Theme.GREEN : Theme.toneForeground(exercise.tone()),
                exercise.completed() ? Theme.GREEN_SOFT : Theme.toneBackground(exercise.tone()));
        badge.setAlignmentX(LEFT_ALIGNMENT);

        JLabel t = new JLabel(exercise.title());
        t.setFont(Theme.fontBold(16));
        t.setForeground(Theme.TEXT);
        t.setAlignmentX(LEFT_ALIGNMENT);

        JLabel d = new JLabel("<html><div style='width:460px'>"
                + exercise.description() + "</div></html>");
        d.setFont(Theme.fontPlain(13));
        d.setForeground(Theme.TEXT_DIM);
        d.setAlignmentX(LEFT_ALIGNMENT);

        info.add(badge);
        info.add(Box.createVerticalStrut(8));
        info.add(t);
        info.add(Box.createVerticalStrut(4));
        info.add(d);

        JButton open = Theme.secondaryButton(exercise.completed() ? "Revoir" : "Ouvrir l'exercice", "\u27a1");
        open.setAlignmentX(LEFT_ALIGNMENT);
        open.setMaximumSize(new Dimension(200, 40));
        open.addActionListener(e -> {
            if (listener != null) {
                listener.onOpenExercise(exercise);
            }
        });

        JPanel action = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        action.setOpaque(false);
        action.add(open);

        c.add(info, BorderLayout.CENTER);
        c.add(action, BorderLayout.EAST);
        c.setAlignmentX(LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        return c;
    }

    /* --------------------------------------------------------------- labels */
    private JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.fontPlain(12));
        label.setForeground(Theme.MUTED);
        return label;
    }

    private JLabel value(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.fontBold(13));
        label.setForeground(Theme.TEXT);
        return label;
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

    /* ========================================================== components */
    /** Purple gradient pill showing the current level. */
    private static class LevelPill extends JPanel {
        LevelPill(String level, String eyebrowText) {
            setOpaque(false);
            setLayout(new BorderLayout(12, 0));
            setBorder(new EmptyBorder(7, 16, 7, 18));
            setPreferredSize(new Dimension(232, 60));
            setMaximumSize(new Dimension(240, 60));
            setMinimumSize(new Dimension(220, 60));

            JComponent trophy = Icons.view("trophy", 30, new Color(250, 190, 40));
            JPanel labels = new JPanel();
            labels.setOpaque(false);
            labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
            JLabel eyebrow = new JLabel(eyebrowText);
            eyebrow.setFont(Theme.fontPlain(11));
            eyebrow.setForeground(new Color(224, 216, 255));
            JLabel name = new JLabel(level);
            name.setFont(Theme.fontBold(16));
            name.setForeground(Color.WHITE);
            labels.add(eyebrow);
            labels.add(Box.createVerticalStrut(1));
            labels.add(name);

            add(trophy, BorderLayout.WEST);
            add(labels, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(new Color(0, 0, 0, 34));
            g2.fillRoundRect(2, 4, w - 1, h - 2, 30, 30);
            g2.setPaint(new GradientPaint(0, 0, new Color(124, 58, 237),
                    w, h, new Color(88, 38, 196)));
            g2.fillRoundRect(0, 0, w - 1, h - 5, 30, 30);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Circular global-progress indicator with a gradient ring. */
    private static class ProgressRing extends JComponent {
        private final int value;

        ProgressRing(int value) {
            this.value = Math.max(0, Math.min(100, value));
            setOpaque(false);
            setPreferredSize(new Dimension(60, 60));
            setMinimumSize(new Dimension(60, 60));
            setMaximumSize(new Dimension(60, 60));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int pad = 4;
            int d = Math.min(getWidth(), getHeight()) - pad * 2;
            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(231, 233, 243));
            g2.draw(new Arc2D.Float(pad, pad, d, d, 90, -360, Arc2D.OPEN));
            g2.setPaint(new GradientPaint(pad, pad, Theme.PURPLE_BRIGHT,
                    pad + d, pad + d, new Color(16, 185, 129)));
            g2.draw(new Arc2D.Float(pad, pad, d, d, 90,
                    -Math.round(360f * value / 100f), Arc2D.OPEN));

            String text = value + "%";
            g2.setFont(Theme.fontBold(12));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Theme.TEXT_DIM);
            g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    /** Rounded hero panel with a code overlay and a decorative track logo. */
    private static class HeroBanner extends JPanel {
        private final String[] code;

        HeroBanner(String track) {
            this.code = snippet(track);
        }

        private static String[] snippet(String track) {
            return switch (track == null ? "" : track) {
                case "Python" -> new String[]{
                        "class CodeLearn:",
                        "    def apprendre(self, i):",
                        "        print(\"Bonjour !\")",
                        "",
                        "for i in range(10):",
                        "    apprendre(i)"
                };
                case "JavaScript" -> new String[]{
                        "class CodeLearn {",
                        "  apprendre(i) {",
                        "    console.log(\"Bonjour !\");",
                        "  }",
                        "}",
                        "",
                        "for (let i = 0; i < 10; i++) {",
                        "  apprendre(i);",
                        "}"
                };
                case "C++" -> new String[]{
                        "#include <iostream>",
                        "",
                        "int main() {",
                        "  std::cout << \"Bonjour !\";",
                        "  for (int i = 0; i < 10; i++) {",
                        "    apprendre(i);",
                        "  }",
                        "}"
                };
                default -> new String[]{
                        "public class CodeLearn {",
                        "  public static void main(String[] args) {",
                        "    System.out.println(\"Bonjour !\");",
                        "    for (int i = 0; i < 10; i++) {",
                        "      apprendre(i);",
                        "    }",
                        "  }",
                        "}"
                };
            };
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(0, 0, 0, 22));
            g2.fillRoundRect(3, 5, w - 1, h - 2, 24, 24);

            g2.setPaint(new GradientPaint(0, 0, new Color(72, 38, 148),
                    w * 0.55f, h, new Color(29, 57, 138)));
            g2.fillRoundRect(0, 0, w - 1, h - 5, 24, 24);

            g2.setClip(new RoundRectangle2D.Float(0, 0, w - 1, h - 5, 24, 24));
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w * 0.80f, h * 0.62f),
                    Math.max(80, w * 0.42f), new float[]{0f, 1f},
                    new Color[]{new Color(96, 165, 250, 125),
                            new Color(96, 165, 250, 0)}));
            g2.fillRect(0, 0, w, h);

            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w * 0.10f, h * 1.05f),
                    Math.max(80, w * 0.38f), new float[]{0f, 1f},
                    new Color[]{new Color(139, 92, 246, 105),
                            new Color(139, 92, 246, 0)}));
            g2.fillRect(0, 0, w, h);

            String[] code = this.code;
            g2.setFont(new Font("Consolas", Font.PLAIN, 10));
            g2.setColor(new Color(210, 220, 255, 55));
            int x = (int) (w * 0.49);
            int y = 26;
            for (String line : code) {
                g2.drawString(line, x, y);
                y += 16;
            }
            g2.dispose();
        }
    }

    /** Simple cup logo whose wordmark follows the chosen programming language. */
    private static class TrackLogo extends JComponent {
        private final String word;
        private final Color accent;

        TrackLogo(String word, Color accent) {
            this.word = word == null || word.isBlank() ? "Java" : word;
            this.accent = accent;
            setOpaque(false);
            setPreferredSize(new Dimension(245, 178));
            setMinimumSize(new Dimension(190, 140));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE);
            double scale = Math.min(getWidth() / 240.0, getHeight() / 180.0);
            int dx = (int) ((getWidth() - 240 * scale) / 2);
            int dy = (int) ((getHeight() - 180 * scale) / 2);
            g2.translate(dx, dy);
            g2.scale(scale, scale);

            // steam
            g2.setColor(new Color(232, 54, 45));
            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            drawSteam(g2, 96, 67, 42);
            drawSteam(g2, 120, 67, 50);
            drawSteam(g2, 144, 67, 42);

            // saucer
            g2.setColor(new Color(18, 86, 180));
            g2.fill(new Ellipse2D.Float(36, 120, 168, 27));
            g2.setColor(new Color(80, 155, 232));
            g2.fill(new Ellipse2D.Float(50, 114, 140, 19));

            // cup
            Path2D cup = new Path2D.Float();
            cup.moveTo(62, 72);
            cup.curveTo(64, 104, 80, 126, 120, 126);
            cup.curveTo(160, 126, 176, 104, 178, 72);
            cup.closePath();
            g2.setPaint(new GradientPaint(62, 72, new Color(57, 145, 235),
                    178, 126, new Color(15, 82, 172)));
            g2.fill(cup);

            g2.setColor(new Color(160, 210, 250));
            g2.fill(new Ellipse2D.Float(62, 63, 116, 19));
            g2.setColor(new Color(27, 104, 203));
            g2.fill(new Ellipse2D.Float(72, 69, 96, 10));

            // wordmark
            Font font = new Font("Segoe UI", Font.BOLD, 47);
            FontMetrics fm = g2.getFontMetrics(font);
            int maxWidth = 196;
            while (fm.stringWidth(word) > maxWidth && font.getSize() > 18) {
                font = font.deriveFont((float) font.getSize() - 1f);
                fm = g2.getFontMetrics(font);
            }
            g2.setFont(font);
            int wx = 120 - fm.stringWidth(word) / 2;
            g2.setColor(new Color(255, 255, 255, 190));
            g2.drawString(word, wx + 1, 174);
            g2.setColor(accent);
            g2.drawString(word, wx, 173);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 7));
            g2.drawString("TM", wx + fm.stringWidth(word) - 2, 160);

            g2.dispose();
        }

        private static void drawSteam(Graphics2D g2, float x, float y, float height) {
            Path2D path = new Path2D.Float();
            path.moveTo(x, y);
            path.curveTo(x - 8, y - height * 0.35f,
                    x + 9, y - height * 0.62f, x, y - height);
            g2.draw(path);
        }
    }

    /** Wordmark accent matching the chosen learning track. */
    private static Color trackAccent(String track) {
        return switch (track == null ? "" : track) {
            case "Python" -> new Color(255, 212, 59);
            case "JavaScript" -> new Color(247, 223, 30);
            case "C++" -> new Color(130, 195, 255);
            default -> new Color(220, 45, 40);
        };
    }

    /** Content panel that follows the viewport width inside a scroll pane. */
    private static class ScrollablePanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect,
                                              int orientation, int direction) {
            return 22;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect,
                                               int orientation, int direction) {
            return Math.max(60, visibleRect.height - 60);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /** Thin, discreet scrollbar used by the dashboard page. */
    private static class ThinScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(199, 203, 222);
            trackColor = Color.WHITE;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return zeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return zeroButton();
        }

        private JButton zeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle bounds) {
            g.setColor(Color.WHITE);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle bounds) {
            if (!c.isEnabled() || bounds.height <= 0) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(199, 203, 222));
            g2.fillRoundRect(bounds.x + 2, bounds.y, bounds.width - 4,
                    bounds.height, 6, 6);
            g2.dispose();
        }
    }
}
