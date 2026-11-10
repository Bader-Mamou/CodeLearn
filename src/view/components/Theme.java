package view.components;

import model.Tone;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import static javax.swing.SwingConstants.CENTER;

/**
 * Central design system for CodeLearn — modern, polished UI kit.
 *
 * <p>All buttons are custom-painted instead of relying on the native
 * look-and-feel. Native Windows buttons ignore {@code setBackground} in many
 * cases, which made the white labels invisible on light gray buttons.</p>
 */
public final class Theme {

    /* ---------------------------------------------------------------- colors */
    public static final Color NAVY        = new Color(24, 32, 56);
    public static final Color NAVY_SOFT   = new Color(38, 48, 78);
    public static final Color PURPLE      = new Color(99, 102, 241);   // indigo-500
    public static final Color PURPLE_DARK = new Color(79, 70, 229);    // indigo-600
    public static final Color PURPLE_SOFT = new Color(241, 243, 255);  // indigo-50
    public static final Color PURPLE_500  = new Color(99, 102, 241);
    public static final Color PURPLE_BRIGHT = new Color(124, 58, 237); // violet-600
    public static final Color PURPLE_LINK = new Color(91, 33, 182);    // violet-800
    public static final Color BG          = new Color(247, 248, 252);  // gray-50
    public static final Color BG_PAPER    = Color.WHITE;
    public static final Color GREEN       = new Color(16, 185, 129);   // emerald-500
    public static final Color GREEN_SOFT  = new Color(232, 248, 240);
    public static final Color AMBER       = new Color(245, 158, 11);   // amber-500
    public static final Color AMBER_SOFT  = new Color(255, 246, 226);
    public static final Color RED         = new Color(239, 68, 68);    // red-500
    public static final Color RED_SOFT    = new Color(253, 236, 236);
    public static final Color TEXT        = new Color(15, 23, 42);     // slate-900
    public static final Color TEXT_DIM    = new Color(51, 65, 85);     // slate-600
    public static final Color MUTED       = new Color(100, 116, 139);  // slate-500
    public static final Color MUTED_SOFT  = new Color(148, 163, 184);  // slate-400
    public static final Color BORDER      = new Color(226, 232, 240);  // slate-200
    public static final Color SHADOW      = new Color(0, 0, 0, 35);
    public static final Color WHITE       = Color.WHITE;

    /** Foreground colour for a semantic tone supplied by the model layer. */
    public static Color toneForeground(Tone tone) {
        switch (tone) {
            case GREEN: return GREEN;
            case AMBER: return AMBER;
            case RED:   return RED;
            case MUTED: return MUTED;
            default:    return PURPLE;
        }
    }

    /** Soft background colour for a semantic tone. */
    public static Color toneBackground(Tone tone) {
        switch (tone) {
            case GREEN: return GREEN_SOFT;
            case AMBER: return AMBER_SOFT;
            case RED:   return RED_SOFT;
            case MUTED: return new Color(241, 245, 249);
            default:    return PURPLE_SOFT;
        }
    }

    public static final String FAMILY = "Segoe UI";

    private Theme() {}

    /* ----------------------------------------------------------------- fonts */
    public static Font font(int style, int size) {
        return new Font(FAMILY, style, size);
    }

    public static Font fontBold(int size)   { return new Font(FAMILY, Font.BOLD, size); }
    public static Font fontPlain(int size)  { return new Font(FAMILY, Font.PLAIN, size); }
    public static Font fontItalic(int size) { return new Font(FAMILY, Font.ITALIC, size); }

    /** Emoji label used for the decorative glyphs of the dashboard. */
    public static JLabel emoji(String glyph, int size) {
        JLabel label = new JLabel(glyph, CENTER);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size));
        label.setOpaque(false);
        return label;
    }

    /* --------------------------------------------------------------- shadows */
    /**
     * Drop-shadow border painted around a component. Call
     * {@code panel.setBorder(Theme.shadowBorder())} on cards to give them
     * depth without heavy native borders.
     */
    public static class ShadowBorder extends AbstractBorder {
        private final int radius;
        private final int opacity;

        public ShadowBorder(int radius, int opacity) {
            this.radius = radius;
            this.opacity = opacity;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            Color shadow = new Color(SHADOW.getRed(), SHADOW.getGreen(),
                    SHADOW.getBlue(), opacity);

            // outer glow layers for softness
            for (int i = 4; i > 0; i--) {
                g2.setColor(new Color(shadow.getRed(), shadow.getGreen(),
                        shadow.getBlue(), shadow.getAlpha() / (i * 2)));
                g2.fillRoundRect(x - i, y - i, w + i * 2, h + i * 2,
                        radius + i, radius + i);
            }
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            int pad = radius + 6;
            return new Insets(pad, pad, pad, pad);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets target) {
            int pad = radius + 6;
            target.set(pad, pad, pad, pad);
            return target;
        }
    }

    public static Border shadowBorder(int radius, int opacity) {
        return new ShadowBorder(radius, opacity);
    }

    public static Border shadowBorder() {
        return shadowBorder(16, 45);
    }

    /* --------------------------------------------------------------- buttons */
    /**
     * Custom-painted, rounded button with an optional leading icon (Unicode
     * glyph or short text) and a subtle hover lift.
     */
    public static class RoundedButton extends JButton {
        private final Color base;
        private final Color hover;
        private final Color pressed;
        private final Color textColor;
        private final int radius;
        private final String icon;
        private boolean raised;
        private Color disabledFill = new Color(239, 241, 247);
        private Color disabledText = new Color(156, 163, 175);

        public RoundedButton(String text, Color base, Color textColor) {
            this(text, base, mix(base, Color.BLACK, 0.10), mix(base, Color.BLACK, 0.18),
                    textColor, 10, null, false);
        }

        public RoundedButton(String text, Color base, Color hover, Color pressed,
                             Color textColor, int radius) {
            this(text, base, hover, pressed, textColor, radius, null, false);
        }

        public RoundedButton(String text, Color base, Color hover, Color pressed,
                             Color textColor, int radius, String icon, boolean raised) {
            super(text);
            this.base = base;
            this.hover = hover;
            this.pressed = pressed;
            this.textColor = textColor;
            this.radius = radius;
            this.icon = icon;
            this.raised = raised;

            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 18, 10, 18));
            setFont(fontBold(13));
        }

        public void setRaised(boolean raised) {
            this.raised = raised;
            repaint();
        }

        /** Palette used when the button is disabled (e.g. locked levels). */
        public void setDisabledPalette(Color fill, Color text) {
            this.disabledFill = fill;
            this.disabledText = text;
            repaint();
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            setCursor(enabled
                    ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    : Cursor.getDefaultCursor());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            boolean enabled = isEnabled();
            Color fill;
            Color fg;
            if (!enabled) {
                fill = disabledFill;
                fg = disabledText;
            } else if (getModel().isPressed()) {
                fill = pressed;
                fg = textColor;
            } else if (getModel().isRollover()) {
                fill = hover;
                fg = textColor;
            } else {
                fill = base;
                fg = textColor;
            }

            if (raised && enabled && !getModel().isPressed()) {
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(2, 3, getWidth() - 1, getHeight() - 1, radius, radius);
            }

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            // draw content (icon + text)
            String text = getText();
            FontMetrics fm = g2.getFontMetrics();
            int totalWidth = 0;
            if (text != null && !text.isEmpty()) {
                totalWidth += fm.stringWidth(text);
            }
            if (icon != null && !icon.isEmpty()) {
                totalWidth += fm.stringWidth(icon) + 8;
            }
            int x = (getWidth() - totalWidth) / 2;

            if (icon != null && !icon.isEmpty()) {
                Font iconFont = fontPlain(14);
                if (iconFont.canDisplayUpTo(icon) != -1) {
                    Font symbol = new Font("Segoe UI Symbol", Font.PLAIN, 14);
                    if (symbol.canDisplayUpTo(icon) == -1) {
                        iconFont = symbol;
                    } else {
                        Font emoji = new Font("Segoe UI Emoji", Font.PLAIN, 14);
                        if (emoji.canDisplayUpTo(icon) == -1) {
                            iconFont = emoji;
                        }
                    }
                }
                g2.setFont(iconFont);
                g2.setColor(fg);
                g2.drawString(icon, x, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                x += fm.stringWidth(icon) + 8;
                g2.setFont(getFont());
            }

            if (text != null && !text.isEmpty()) {
                g2.setFont(getFont());
                g2.setColor(fg);
                g2.drawString(text, x, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            }

            if (isFocusOwner()) {
                g2.setColor(new Color(textColor.getRed(), textColor.getGreen(),
                        textColor.getBlue(), 110));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5,
                        Math.max(4, radius - 2), Math.max(4, radius - 2));
            }
            g2.dispose();
        }
    }

    public static JButton button(String text) {
        RoundedButton b = new RoundedButton(text, PURPLE, Color.WHITE);
        return b;
    }

    public static JButton button(String text, String icon) {
        RoundedButton b = new RoundedButton(text, PURPLE,
                mix(PURPLE, Color.BLACK, 0.10), mix(PURPLE, Color.BLACK, 0.18),
                Color.WHITE, 10, icon, false);
        return b;
    }

    public static JButton secondaryButton(String text, String icon) {
        RoundedButton b = new RoundedButton(text, PURPLE_SOFT,
                new Color(228, 221, 255), new Color(216, 207, 252), PURPLE_DARK, 10, icon, false);
        b.setFont(fontBold(13));
        return b;
    }

    public static JButton dangerButton(String text, String icon) {
        RoundedButton b = new RoundedButton(text, RED_SOFT,
                new Color(250, 223, 223), new Color(245, 210, 210), RED, 10, icon, false);
        b.setFont(fontBold(13));
        return b;
    }

    public static JButton buttonRaised(String text) {
        RoundedButton b = new RoundedButton(text, PURPLE, Color.WHITE);
        b.setRaised(true);
        return b;
    }

    public static JButton secondaryButton(String text) {
        RoundedButton b = new RoundedButton(text, PURPLE_SOFT,
                new Color(228, 221, 255), new Color(216, 207, 252), PURPLE_DARK, 10);
        b.setFont(fontBold(13));
        return b;
    }

    public static JButton dangerButton(String text) {
        RoundedButton b = new RoundedButton(text, RED_SOFT,
                new Color(250, 223, 223), new Color(245, 210, 210), RED, 10);
        b.setFont(fontBold(13));
        return b;
    }

    public static JButton ghostButton(String text) {
        RoundedButton b = new RoundedButton(text, new Color(247, 248, 252),
                new Color(234, 237, 244), new Color(226, 230, 238), TEXT_DIM, 10);
        b.setFont(fontBold(13));
        return b;
    }

    /** White button used on dark backgrounds. */
    public static JButton buttonOnDark(String text) {
        RoundedButton b = new RoundedButton(text, Color.WHITE,
                new Color(237, 233, 255), new Color(224, 218, 250), NAVY, 10);
        b.setFont(fontBold(13));
        return b;
    }

    /* ----------------------------------------------------------------- cards */
    /** White card with rounded corners, light border and soft shadow. */
    public static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;
        private final Color line;
        private boolean shadow;

        public RoundedPanel(int radius, Color fill, Color line) {
            this.radius = radius;
            this.fill = fill;
            this.line = line;
            setOpaque(false);
            setBackground(fill);
            setBorder(new EmptyBorder(18, 20, 18, 20));
            this.shadow = true;
        }

        public RoundedPanel(int radius, Color fill, Color line, boolean shadow) {
            this(radius, fill, line);
            this.shadow = shadow;
        }

        public void setShadow(boolean shadow) {
            this.shadow = shadow;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            if (shadow) {
                g2.setColor(new Color(0, 0, 0, 16));
                g2.fillRoundRect(3, 4, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            if (line != null) {
                g2.setColor(line);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JPanel card() {
        return new RoundedPanel(16, WHITE, BORDER);
    }

    public static JPanel cardNoShadow() {
        return new RoundedPanel(16, WHITE, BORDER, false);
    }

    public static JPanel roundedPanel(int radius, Color fill, Color line) {
        return new RoundedPanel(radius, fill, line);
    }

    /* ----------------------------------------------------------------- pages */
    public static JPanel page(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout(16, 20));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(28, 36, 32, 36));

        JPanel head = new JPanel(new BorderLayout(14, 0));
        head.setOpaque(false);

        JComponent accent = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PURPLE);
                g2.fillRoundRect(0, 3, 4, getHeight() - 6, 4, 4);
                g2.dispose();
            }
        };
        accent.setPreferredSize(new Dimension(4, 48));
        accent.setOpaque(false);

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setFont(fontBold(26));
        t.setForeground(TEXT);
        JLabel s = new JLabel(subtitle);
        s.setFont(fontPlain(14));
        s.setForeground(MUTED);
        texts.add(t);
        texts.add(Box.createVerticalStrut(6));
        texts.add(s);

        head.add(accent, BorderLayout.WEST);
        head.add(texts, BorderLayout.CENTER);
        p.add(head, BorderLayout.NORTH);
        return p;
    }

    /* ---------------------------------------------------------------- badges */
    /** Small rounded pill with an explicit text and fill colour. */
    public static class Badge extends JLabel {
        private final Color fill;
        private final int radius;

        public Badge(String text, Color foreground, Color fill) {
            this(text, foreground, fill, 11, 10);
        }

        public Badge(String text, Color foreground, Color fill, int fontSize, int radius) {
            super(text);
            this.fill = fill;
            this.radius = radius;
            setOpaque(false);
            setForeground(foreground);
            setFont(fontBold(fontSize));
            setBorder(new EmptyBorder(5, 10, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JLabel badge(String text, Color foreground, Color fill) {
        return new Badge(text, foreground, fill);
    }

    public static JLabel badge(String text, Color foreground, Color fill, int fontSize) {
        return new Badge(text, foreground, fill, fontSize, 10);
    }

    /* ------------------------------------------------------------- progress */
    /** Rounded progress bar drawn by the theme instead of the native L&F. */
    public static class ProgressBar extends JComponent {
        private int value;
        private final Color track;
        private final Color fill;
        private boolean animated;

        public ProgressBar(int value, Color fill, Color track) {
            this.value = Math.max(0, Math.min(100, value));
            this.fill = fill;
            this.track = track;
            setOpaque(false);
            setPreferredSize(new Dimension(10, 8));
        }

        public void setValue(int value) {
            this.value = Math.max(0, Math.min(100, value));
            repaint();
        }

        public void setAnimated(boolean animated) {
            this.animated = animated;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth() - 1;
            int h = getHeight() - 1;
            g2.setColor(track);
            g2.fillRoundRect(0, 0, w, h, h, h);
            int filled = (int) Math.round(w * (value / 100.0));
            if (filled > 0) {
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, Math.max(filled, h), h, h, h);
                if (animated) {
                    g2.setColor(new Color(fill.getRed(), fill.getGreen(),
                            fill.getBlue(), 60));
                    g2.fillRoundRect(Math.max(0, filled - 20), 0, 20, h, h, h);
                }
            }
            g2.dispose();
        }
    }

    public static JComponent progressBar(int value, Color fill, Color track) {
        return new ProgressBar(value, fill, track);
    }

    /* ----------------------------------------------------------------- forms */
    public static class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final Insets insets;

        public RoundedLineBorder(Color color, int radius, Insets insets) {
            this.color = color;
            this.radius = radius;
            this.insets = insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return insets;
        }

        @Override
        public Insets getBorderInsets(Component c, Insets target) {
            target.set(insets.top, insets.left, insets.bottom, insets.right);
            return target;
        }
    }

    public static Border roundedBorder(Color color) {
        return new RoundedLineBorder(color, 10, new Insets(8, 12, 8, 12));
    }

    public static Border focusBorder(Color color) {
        return new RoundedLineBorder(color, 10, new Insets(8, 12, 8, 12));
    }

    private static void addFocusHighlight(JComponent field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(focusBorder(PURPLE));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(focusBorder(BORDER));
            }
        });
    }

    public static JTextField textField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    public static JTextField textField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        styleField(field);
        field.setForeground(MUTED);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT);
                }
                field.setBorder(focusBorder(PURPLE));
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(MUTED);
                }
                field.setBorder(focusBorder(BORDER));
            }
        });
        return field;
    }

    public static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        styleField(field);
        return field;
    }

    private static void styleField(JTextField field) {
        field.setFont(fontPlain(14));
        field.setForeground(TEXT);
        field.setBackground(WHITE);
        field.setCaretColor(PURPLE);
        field.setBorder(roundedBorder(BORDER));
        addFocusHighlight(field);
    }

    public static <T> JComboBox<T> comboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(fontPlain(13));
        combo.setForeground(TEXT);
        combo.setBackground(WHITE);
        combo.setBorder(roundedBorder(BORDER));
        combo.setMaximumRowCount(5);
        combo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                combo.setBorder(focusBorder(PURPLE));
            }
            @Override
            public void focusLost(FocusEvent e) {
                combo.setBorder(focusBorder(BORDER));
            }
        });
        return combo;
    }

    /* ------------------------------------------------------------- dividers */
    public static JSeparator separator() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        return s;
    }

    /* --------------------------------------------------------------- helpers */
    /** Stat card: big value on top, label below. */
    public static JPanel statCard(String label, String value, Color toneColor) {
        JPanel c = card();
        c.setLayout(new BorderLayout(0, 8));
        c.setPreferredSize(new Dimension(150, 96));
        c.setMaximumSize(new Dimension(150, 96));

        JLabel v = new JLabel(value, CENTER);
        v.setFont(fontBold(30));
        v.setForeground(toneColor);

        JLabel t = new JLabel(label, CENTER);
        t.setFont(fontPlain(12));
        t.setForeground(MUTED);

        c.add(v, BorderLayout.CENTER);
        c.add(t, BorderLayout.SOUTH);
        return c;
    }

    /** Section heading used on dashboard pages. */
    public static JLabel sectionTitle(String text) {
        JLabel t = new JLabel(text);
        t.setFont(fontBold(18));
        t.setForeground(TEXT);
        return t;
    }

    /** Blends colour {@code a} towards {@code b} by {@code ratio} (0..1). */
    public static Color mix(Color a, Color b, double ratio) {
        ratio = Math.max(0, Math.min(1, ratio));
        int r = (int) Math.round(a.getRed() + (b.getRed() - a.getRed()) * ratio);
        int g = (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * ratio);
        int bl = (int) Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * ratio);
        int alpha = (int) Math.round(a.getAlpha() + (b.getAlpha() - a.getAlpha()) * ratio);
        return new Color(r, g, bl, alpha);
    }

    /** Creates a rounded rectangle clip for a component (mask). */
    public static Shape roundedClip(int x, int y, int w, int h, int radius) {
        return new RoundRectangle2D.Float(x, y, w, h, radius, radius);
    }
}
