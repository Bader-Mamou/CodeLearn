package view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Dark navigation sidebar matching the CodeLearn design: gradient background,
 * centered brand, rounded active navigation item, logout action and the
 * laptop illustration at the bottom.
 */
public class Sidebar extends JPanel {

    private final Consumer<String> navigate;
    private final List<NavButton> navButtons = new ArrayList<>();
    private String selected = "";

    private static final Image ART =
            Assets.withTransparentBackground(Assets.load("pc.png"));

    public Sidebar(String role, String[] items, Consumer<String> navigate, Runnable logout) {
        this(role, items, items, "D\u00e9connexion",
                "Apprendre. Pratiquez. Progresser.", navigate, logout);
    }

    public Sidebar(String role, String[] items, String[] labels, String logoutLabel,
                   String tagline, Consumer<String> navigate, Runnable logout) {
        this.navigate = navigate;

        setOpaque(false);
        setPreferredSize(new Dimension(262, 0));
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(22, 0, 14, 0));

        /* ------------------------------------------------------------ header */
        BrandPanel brand = new BrandPanel(tagline);
        brand.setAlignmentX(LEFT_ALIGNMENT);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(brand);
        top.add(Box.createVerticalStrut(16));

        /* --------------------------------------------------------------- nav */
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(0, 18, 0, 18));

        for (int i = 0; i < items.length; i++) {
            String key = items[i];
            String label = i < labels.length ? labels[i] : key;
            NavButton button = new NavButton(label, iconFor(key));
            button.setAlignmentX(LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            button.setPreferredSize(new Dimension(210, 44));
            button.addActionListener(e -> select(key));
            navButtons.add(button);
            nav.add(button);
            nav.add(Box.createVerticalStrut(6));
        }
        top.add(nav);

        /* ------------------------------------------------------------ footer */
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(0, 18, 0, 0));

        JComponent line = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 28));
                g2.fillRect(4, 0, getWidth() - 24, 1);
                g2.dispose();
            }
        };
        line.setOpaque(false);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(10, 1));

        FooterButton logoutButton = new FooterButton(logoutLabel);
        logoutButton.setAlignmentX(LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        logoutButton.setPreferredSize(new Dimension(210, 44));
        logoutButton.addActionListener(e -> logout.run());

        ArtPanel art = new ArtPanel();
        art.setAlignmentX(LEFT_ALIGNMENT);
        art.setMaximumSize(new Dimension(Integer.MAX_VALUE, 138));
        art.setPreferredSize(new Dimension(210, 138));

        bottom.add(line);
        bottom.add(Box.createVerticalStrut(12));
        bottom.add(logoutButton);
        bottom.add(Box.createVerticalStrut(6));
        bottom.add(art);

        add(top, BorderLayout.NORTH);
        add(bottom, BorderLayout.SOUTH);
    }

    /** Highlights the current page in the sidebar. */
    public void setSelected(String page) {
        this.selected = page == null ? "" : page;
        for (NavButton button : navButtons) {
            button.repaint();
        }
    }

    private void select(String page) {
        setSelected(page);
        navigate.accept(page);
    }

    private static String iconFor(String item) {
        String value = item == null ? "" : item.toLowerCase(Locale.ROOT);
        if (value.contains("accueil") || value.contains("tableau")) {
            return "home";
        }
        if (value.contains("exercice") || value.contains("\u00e9valuation")) {
            return "exercise";
        }
        if (value.contains("test")) {
            return "test";
        }
        if (value.contains("utilisateur") || value.contains("apprenant")
                || value.contains("enseignant")) {
            return "profile";
        }
        if (value.contains("r\u00f4le") || value.contains("droit")) {
            return "lock";
        }
        if (value.contains("cours")) {
            return "book";
        }
        if (value.contains("progression") || value.contains("niveau")) {
            return "progress";
        }
        if (value.contains("profil")) {
            return "profile";
        }
        if (value.contains("suivi") || value.contains("supervision")
                || value.contains("statistique") || value.contains("rapport")) {
            return "chart";
        }
        if (value.contains("param\u00e8tre") || value.contains("configuration")) {
            return "gear";
        }
        return "test";
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2.setPaint(new GradientPaint(0, 0, new Color(30, 27, 68),
                0, getHeight(), new Color(14, 21, 52)));
        g2.fillRect(0, 0, getWidth(), getHeight());

        float cx = getWidth() * 0.5f;
        g2.setPaint(new RadialGradientPaint(new Point2D.Float(cx, 70),
                Math.max(120, getWidth()), new float[]{0f, 1f},
                new Color[]{new Color(103, 55, 234, 80),
                        new Color(103, 55, 234, 0)}));
        g2.fillRect(0, 0, getWidth(), Math.max(220, getHeight() / 2));

        if (getHeight() > 280) {
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(getWidth() * 0.3f, getHeight() - 80),
                    Math.max(100, getWidth() * 0.85f), new float[]{0f, 1f},
                    new Color[]{new Color(124, 58, 237, 82),
                            new Color(124, 58, 237, 0)}));
            int glow = Math.max(180, getHeight() / 3);
            g2.fillRect(0, getHeight() - glow, getWidth(), glow);
        }
        g2.dispose();
    }

    /* ------------------------------------------------------------ nav button */
    private class NavButton extends JButton {
        private final String item;
        private final String icon;

        NavButton(String item, String icon) {
            super(item);
            this.item = item;
            this.icon = icon;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 20, 10, 14));
            setFont(Theme.fontPlain(14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            boolean active = item.equals(selected);
            int h = getHeight();

            if (active) {
                g2.setColor(new Color(0, 0, 0, 28));
                g2.fillRoundRect(1, 3, getWidth() - 1, h - 2, 14, 14);
                g2.setPaint(new GradientPaint(0, 0, new Color(116, 70, 246),
                        getWidth(), h, new Color(91, 44, 225)));
                g2.fillRoundRect(0, 0, getWidth() - 1, h - 3, 14, 14);
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillRoundRect(0, 0, getWidth() - 1, h - 3, 14, 14);
            }

            Color fg = active ? Color.WHITE : new Color(214, 214, 238);
            Icons.paint(g2, icon, 20, (h - 3 - 18) / 2, 18,
                    active ? Color.WHITE : new Color(198, 198, 228));

            g2.setFont(getFont().deriveFont(active ? Font.BOLD : Font.PLAIN));
            FontMetrics fm = g2.getFontMetrics();
            int y = (h - 3 - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(fg);
            g2.drawString(getText(), 50, y);
            g2.dispose();
        }
    }

    /* ------------------------------------------------------------ footer btn */
    private class FooterButton extends JButton {
        FooterButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 20, 10, 14));
            setFont(Theme.fontPlain(14));
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isRollover()) {
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 14, 14);
            }
            Icons.paint(g2, "logout", 20, (getHeight() - 3 - 18) / 2, 18,
                    new Color(208, 206, 236));
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(new Color(226, 225, 246));
            g2.drawString(getText(), 50,
                    (getHeight() - 3 - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    /* ------------------------------------------------------------ brand */
    private static class BrandPanel extends JComponent {
        private final String tagline;

        BrandPanel(String tagline) {
            this.tagline = tagline;
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));
            setPreferredSize(new Dimension(230, 118));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Font iconFont = new Font("Consolas", Font.BOLD, 35);
            g2.setFont(iconFont);
            g2.setColor(new Color(137, 99, 255));
            FontMetrics im = g2.getFontMetrics();
            String icon = "</>";
            int iconX = (getWidth() - im.stringWidth(icon)) / 2;
            g2.drawString(icon, iconX, 40);

            Font brand = Theme.fontBold(26);
            g2.setFont(brand);
            FontMetrics bm = g2.getFontMetrics();
            String code = "Code";
            String learn = "Learn";
            int total = bm.stringWidth(code) + bm.stringWidth(learn);
            int x = (getWidth() - total) / 2;
            int baseline = 80;
            g2.setColor(new Color(142, 103, 255));
            g2.drawString(code, x, baseline);
            g2.setColor(Color.WHITE);
            g2.drawString(learn, x + bm.stringWidth(code), baseline);

            g2.setFont(Theme.fontPlain(10));
            g2.setColor(new Color(178, 177, 212));

            FontMetrics tm = g2.getFontMetrics();
            g2.drawString(tagline, (getWidth() - tm.stringWidth(tagline)) / 2, 100);
            g2.dispose();
        }
    }

    /* ------------------------------------------------------------ illustration */
    private static class ArtPanel extends JComponent {
        ArtPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth();
            int h = getHeight();
            if (ART != null) {
                int iw = ART.getWidth(null);
                int ih = ART.getHeight(null);
                double scale = Math.min((w - 6.0) / iw, (h - 6.0) / ih);
                int dw = Math.max(1, (int) Math.round(iw * scale));
                int dh = Math.max(1, (int) Math.round(ih * scale));
                int dx = (w - dw) / 2;
                int dy = h - dh;

                g2.setPaint(new RadialGradientPaint(
                        new Point2D.Float(dx + dw / 2f, dy + dh * 0.55f),
                        Math.max(40, dw * 0.55f), new float[]{0f, 1f},
                        new Color[]{new Color(124, 58, 237, 135),
                                new Color(124, 58, 237, 0)}));
                g2.fill(new Ellipse2D.Float(dx - 8, dy + dh * 0.2f,
                        dw + 16, dh * 0.8f));
                g2.drawImage(ART, dx, dy, dw, dh, null);
            } else {
                int cx = w / 2;
                g2.setColor(new Color(124, 58, 237, 120));
                g2.fillOval(cx - 60, h - 70, 120, 60);
                g2.setColor(new Color(59, 130, 246));
                g2.fillRoundRect(cx - 48, h - 66, 96, 56, 10, 10);
                g2.setColor(new Color(226, 232, 240));
                g2.fillRoundRect(cx - 40, h - 58, 80, 40, 6, 6);
                g2.setColor(new Color(99, 102, 241));
                g2.setFont(Theme.fontBold(18));
                g2.drawString("</>", cx - 15, h - 32);
            }
            g2.dispose();
        }
    }
}
