package view.components;

import model.StatInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Shared building blocks for the teacher and administrator dashboards.
 *
 * <p>The student dashboard already has a rich visual language; this kit keeps
 * the two management dashboards consistent with it while staying reusable.</p>
 */
public final class DashboardKit {

    private DashboardKit() {
    }

    /* ------------------------------------------------------------- layout */

    /** Vertical content column used inside a dashboard scroll pane. */
    public static JPanel content() {
        ScrollPanel content = new ScrollPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 34, 28, 34));
        return content;
    }

    public static JScrollPane scroll(JComponent content) {
        JScrollPane scroll = new JScrollPane(content,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(Theme.BG);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(9, 0));
        scroll.getVerticalScrollBar().setOpaque(false);
        scroll.getVerticalScrollBar().setUI(new ThinScrollBarUI());
        return scroll;
    }

    /** Makes a component fill the dashboard width at a fixed height. */
    public static void fullWidth(JComponent component, int height) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        component.setPreferredSize(new Dimension(10, height));
    }

    /* ------------------------------------------------------------- header */

    public static JPanel header(String greeting, String subtitle, JComponent right) {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);
        fullWidth(header, 82);

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));

        JPanel greetingRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        greetingRow.setOpaque(false);
        greetingRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel(greeting);
        title.setFont(Theme.fontBold(29));
        title.setForeground(Theme.TEXT);
        greetingRow.add(title);
        greetingRow.add(Box.createHorizontalStrut(8));
        greetingRow.add(Theme.emoji("\uD83D\uDC4B", 24));

        JLabel message = new JLabel(subtitle);
        message.setFont(Theme.fontPlain(14));
        message.setForeground(Theme.MUTED);
        message.setAlignmentX(Component.LEFT_ALIGNMENT);

        copy.add(greetingRow);
        copy.add(Box.createVerticalStrut(3));
        copy.add(message);

        header.add(copy, BorderLayout.WEST);
        if (right != null) {
            header.add(right, BorderLayout.EAST);
        }
        return header;
    }

    /** Identity pill shown in the top-right corner of the dashboard header. */
    public static JPanel identity(String name, String role) {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(11, 0));
        card.setBorder(new EmptyBorder(8, 11, 8, 16));
        card.setPreferredSize(new Dimension(236, 56));
        card.setMaximumSize(new Dimension(236, 56));

        String initial = name == null || name.isBlank()
                ? "?" : name.substring(0, 1).toUpperCase();
        JComponent avatar = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight());
                g2.setColor(Theme.PURPLE);
                g2.fillOval(0, 0, d - 1, d - 1);
                g2.setColor(new Color(255, 255, 255, 110));
                g2.fillOval(4, 4, d - 9, d / 2);
                g2.setColor(Color.WHITE);
                g2.setFont(Theme.fontBold(15));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initial, (d - fm.stringWidth(initial)) / 2,
                        (d - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(38, 38));
        avatar.setMinimumSize(new Dimension(38, 38));
        avatar.setMaximumSize(new Dimension(38, 38));

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        copy.add(Box.createVerticalGlue());

        JLabel nameLabel = new JLabel(name == null ? "" : name);
        nameLabel.setFont(Theme.fontBold(13));
        nameLabel.setForeground(Theme.TEXT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(Theme.fontPlain(11));
        roleLabel.setForeground(Theme.MUTED);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        copy.add(nameLabel);
        copy.add(Box.createVerticalStrut(2));
        copy.add(roleLabel);
        copy.add(Box.createVerticalGlue());

        card.add(avatar, BorderLayout.WEST);
        card.add(copy, BorderLayout.CENTER);
        return card;
    }

    /* --------------------------------------------------------------- hero */

    public static JPanel hero(String eyebrow, String title, String subtitle,
                              Color start, Color end, String artIcon,
                              List<JComponent> actions) {
        GradientPanel hero = new GradientPanel(start, end);
        hero.setLayout(new BorderLayout(20, 0));
        hero.setBorder(new EmptyBorder(22, 30, 22, 24));
        fullWidth(hero, 164);

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));

        JLabel tag = new JLabel(eyebrow.toUpperCase());
        tag.setFont(Theme.fontBold(11));
        tag.setForeground(new Color(218, 221, 250));
        tag.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel(title);
        heading.setFont(Theme.fontBold(27));
        heading.setForeground(Color.WHITE);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel text = new JLabel("<html><div style='width:560px'>" + subtitle + "</div></html>");
        text.setFont(Theme.fontPlain(13));
        text.setForeground(new Color(228, 230, 250));
        text.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (JComponent action : actions) {
            buttons.add(action);
        }

        copy.add(Box.createVerticalGlue());
        copy.add(tag);
        copy.add(Box.createVerticalStrut(5));
        copy.add(heading);
        copy.add(Box.createVerticalStrut(5));
        copy.add(text);
        copy.add(Box.createVerticalStrut(13));
        copy.add(buttons);
        copy.add(Box.createVerticalGlue());

        hero.add(copy, BorderLayout.CENTER);
        hero.add(new HeroArt(artIcon), BorderLayout.EAST);
        return hero;
    }

    public static JComponent heroButton(String text, String glyph, Color accent,
                                        boolean primary, Runnable action) {
        Color base = primary ? Color.WHITE : new Color(255, 255, 255, 38);
        Color hover = primary ? new Color(241, 240, 255) : new Color(255, 255, 255, 58);
        Color pressed = primary ? new Color(226, 223, 250) : new Color(255, 255, 255, 78);
        Color foreground = primary ? accent : Color.WHITE;

        Theme.RoundedButton button = new Theme.RoundedButton(text, base, hover, pressed,
                foreground, 18, glyph, false);
        button.setFont(Theme.fontBold(12));
        button.setBorder(new EmptyBorder(9, 15, 9, 15));
        button.setPreferredSize(new Dimension(text.length() * 8 + 42, 38));
        button.setMaximumSize(new Dimension(text.length() * 8 + 60, 38));
        if (action != null) {
            button.addActionListener(e -> action.run());
        }
        return button;
    }

    /* --------------------------------------------------------------- stats */

    public static JPanel statCard(String icon, StatInfo stat, String caption) {
        Color tone = Theme.toneForeground(stat.tone());
        Color fill = Theme.toneBackground(stat.tone());

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(13, 0));
        card.setBorder(new EmptyBorder(15, 17, 15, 17));

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        copy.add(Box.createVerticalGlue());

        JLabel value = new JLabel(stat.value());
        value.setFont(Theme.fontBold(25));
        value.setForeground(tone);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(stat.label());
        label.setFont(Theme.fontBold(12));
        label.setForeground(Theme.TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel note = new JLabel(caption);
        note.setFont(Theme.fontPlain(10));
        note.setForeground(Theme.MUTED);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);

        copy.add(value);
        copy.add(Box.createVerticalStrut(2));
        copy.add(label);
        copy.add(Box.createVerticalStrut(2));
        copy.add(note);
        copy.add(Box.createVerticalGlue());

        card.add(iconBadge(icon, tone, fill, 46), BorderLayout.WEST);
        card.add(copy, BorderLayout.CENTER);
        return card;
    }

    /* ---------------------------------------------------------- card headings */

    public static JPanel sectionHeader(String title, String subtitle, JComponent action) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        fullWidth(row, 44);

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel(title);
        heading.setFont(Theme.fontBold(17));
        heading.setForeground(Theme.TEXT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (subtitle != null && !subtitle.isBlank()) {
            JLabel note = new JLabel(subtitle);
            note.setFont(Theme.fontPlain(11));
            note.setForeground(Theme.MUTED);
            note.setAlignmentX(Component.LEFT_ALIGNMENT);
            copy.add(heading);
            copy.add(Box.createVerticalStrut(2));
            copy.add(note);
        } else {
            copy.add(heading);
            copy.add(Box.createVerticalGlue());
        }

        row.add(copy, BorderLayout.CENTER);
        if (action != null) {
            row.add(action, BorderLayout.EAST);
        }
        return row;
    }

    public static JPanel cardHeader(String title, String subtitle, JComponent action) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel(title);
        heading.setFont(Theme.fontBold(15));
        heading.setForeground(Theme.TEXT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel note = new JLabel(subtitle);
        note.setFont(Theme.fontPlain(11));
        note.setForeground(Theme.MUTED);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        note.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));

        copy.add(heading);
        copy.add(Box.createVerticalStrut(2));
        copy.add(note);

        row.add(copy, BorderLayout.CENTER);
        if (action != null) {
            row.add(action, BorderLayout.EAST);
        }
        return row;
    }

    /* -------------------------------------------------------------- helpers */

    public static JComponent iconBadge(String icon, Color color, Color fill, int size) {
        JComponent badge = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight());
                g2.setColor(fill);
                g2.fillOval(0, 0, d - 1, d - 1);
                int iconSize = Math.round(d * 0.50f);
                Icons.paint(g, icon, (d - iconSize) / 2, (d - iconSize) / 2,
                        iconSize, color);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(size, size));
        badge.setMinimumSize(new Dimension(size, size));
        badge.setMaximumSize(new Dimension(size, size));
        return badge;
    }

    public static JPanel actionButton(String glyph, String text, Color accent,
                                      Runnable action) {
        Theme.RoundedButton button = new Theme.RoundedButton(text,
                new Color(247, 246, 255), new Color(239, 237, 255),
                new Color(230, 227, 252), accent, 12, glyph, false);
        button.setFont(Theme.fontBold(12));
        button.setBorder(new EmptyBorder(9, 12, 9, 12));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        button.setPreferredSize(new Dimension(10, 38));
        button.addActionListener(e -> action.run());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        wrap.add(button, BorderLayout.CENTER);
        return wrap;
    }

    public static JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.fontPlain(12));
        label.setForeground(Theme.MUTED);
        return label;
    }

    public static JLabel note(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.fontPlain(10));
        label.setForeground(Theme.MUTED);
        return label;
    }

    public static JPanel empty(String text) {
        JPanel panel = Theme.roundedPanel(12, new Color(249, 249, 253), Theme.BORDER);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panel.add(muted(text), BorderLayout.CENTER);
        return panel;
    }

    /* ----------------------------------------------------------- components */

    private static class GradientPanel extends JPanel {
        private final Color start;
        private final Color end;

        GradientPanel(Color start, Color end) {
            this.start = start;
            this.end = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            Shape clip = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 22, 22);
            g2.setClip(clip);
            g2.setPaint(new GradientPaint(0, 0, start, w, h, end));
            g2.fill(clip);

            g2.setColor(new Color(255, 255, 255, 16));
            g2.fillOval(w - 250, -120, 330, 330);
            g2.setColor(new Color(255, 255, 255, 12));
            g2.fillOval(w - 130, h - 120, 220, 220);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class HeroArt extends JComponent {
        private final String icon;

        HeroArt(String icon) {
            this.icon = icon;
            setOpaque(false);
            setPreferredSize(new Dimension(220, 118));
            setMinimumSize(new Dimension(170, 110));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int d = Math.min(getWidth() - 20, getHeight() - 8);
            int cx = getWidth() / 2 + 10;
            int cy = getHeight() / 2;
            int x = cx - d / 2;
            int y = cy - d / 2;

            g2.setColor(new Color(255, 255, 255, 20));
            g2.fillOval(x, y, d, d);
            g2.setColor(new Color(255, 255, 255, 55));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawOval(x, y, d, d);

            g2.setColor(new Color(255, 255, 255, 16));
            g2.fill(new Ellipse2D.Float(x - 18, y + d - 26, 54, 54));
            g2.fill(new Ellipse2D.Float(x + d - 30, y - 16, 46, 46));

            int iconSize = Math.round(d * 0.46f);
            Icons.paint(g, icon, cx - iconSize / 2, cy - iconSize / 2,
                    iconSize, new Color(255, 255, 255, 235));
            g2.dispose();
        }
    }

    /** Content panel that follows the viewport width inside a scroll pane. */
    private static class ScrollPanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect,
                                              int orientation, int direction) {
            return 20;
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

    private static class ThinScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(199, 203, 222);
            trackColor = Theme.BG;
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
            g.setColor(Theme.BG);
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
            g2.setColor(thumbColor);
            g2.fillRoundRect(bounds.x + 2, bounds.y, Math.max(3, bounds.width - 4),
                    bounds.height, 8, 8);
            g2.dispose();
        }
    }
}
