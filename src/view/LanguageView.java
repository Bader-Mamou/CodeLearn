package view;

import model.LanguageInfo;
import view.components.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

/**
 * Language-selection screen shown to a student the first time they log in.
 * It only raises an event; the application controller authenticates and opens
 * the dashboard in the chosen language.
 */
public class LanguageView extends JPanel {

    public interface Listener {
        void onLanguageChosen(String language);
    }

    private final List<LanguageInfo> languages;
    private final AbstractButton[] buttons;
    private final JButton continueButton;
    private final String continueLabel;
    private final Function<String, Color> colorOf;
    private int selected = 0;
    private Listener listener;

    public LanguageView(List<LanguageInfo> languages) {
        this(languages, "Choisissez votre langue",
                "Cette langue sera utilis\u00e9e dans votre tableau de bord.",
                null, LanguageView::languageColor);
    }

    /**
     * Generic choice screen, reused for the interface language and for the
     * programming language the student wants to learn.
     */
    public LanguageView(List<LanguageInfo> languages, String titleText, String subtitleText,
                        String continueLabel, Function<String, Color> colorOf) {
        this.languages = List.copyOf(languages);
        this.buttons = new AbstractButton[languages.size()];
        this.continueLabel = continueLabel;
        this.colorOf = colorOf;

        setLayout(new GridBagLayout());
        setOpaque(true);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.CENTER;

        /* ------------------------------------------------------------- card */
        JPanel box = Theme.card();
        box.setLayout(new GridBagLayout());
        box.setBorder(new EmptyBorder(40, 44, 40, 44));
        int cardHeight = Math.max(420, 262 + languages.size() * 66);
        box.setPreferredSize(new Dimension(420, cardHeight));
        box.setMaximumSize(new Dimension(480, cardHeight + 40));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;

        /* ------------------------------------------------------------ header */
        JLabel title = new JLabel(titleText);
        title.setFont(Theme.fontBold(24));
        title.setForeground(Theme.TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel(subtitleText);
        sub.setFont(Theme.fontPlain(13));
        sub.setForeground(Theme.MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        int y = 0;
        c.gridy = y++;
        c.insets = new Insets(0, 0, 8, 0);
        box.add(title, c);

        c.gridy = y++;
        c.insets = new Insets(2, 0, 30, 0);
        box.add(sub, c);

        /* ---------------------------------------------------------- options */
        for (int i = 0; i < languages.size(); i++) {
            int index = i;
            LanguageInfo language = languages.get(i);
            AbstractButton button = new LanguageButton(language);
            buttons[i] = button;
            button.addActionListener(e -> pick(index));

            c.gridy = y++;
            c.insets = new Insets(0, 0, 10, 0);
            box.add(button, c);
        }

        /* ------------------------------------------------------------- action */
        continueButton = Theme.button("Continuer", "\u27a1");
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        continueButton.addActionListener(e -> {
            if (listener != null) {
                listener.onLanguageChosen(languages.get(selected).code());
            }
        });

        c.gridy = y++;
        c.insets = new Insets(24, 0, 0, 0);
        box.add(continueButton, c);

        /* select the first language by default */
        pick(0);

        /* center the card in the panel */
        add(box, gc);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /** Preselects an option by its code, keeping the first option as fallback. */
    public void selectLanguage(String code) {
        for (int i = 0; i < languages.size(); i++) {
            if (languages.get(i).code().equals(code)) {
                pick(i);
                return;
            }
        }
        pick(0);
    }

    private void pick(int index) {
        if (index < 0 || index >= languages.size()) {
            return;
        }
        selected = index;
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setSelected(i == index);
        }
        continueButton.setText(continueLabel != null ? continueLabel
                : switch (languages.get(index).code()) {
                    case "en" -> "Continue";
                    default -> "Continuer";
                });
    }

    /** One language option, highlighted when selected. */
    private class LanguageButton extends JButton {
        private final LanguageInfo language;
        private boolean selected;

        LanguageButton(LanguageInfo language) {
            this.language = language;
            this.selected = false;
            setText(language.label());
            setFocusPainted(false);
            setOpaque(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFont(Theme.fontBold(14));
            setHorizontalAlignment(SwingConstants.LEFT);
            setHorizontalTextPosition(SwingConstants.RIGHT);
            setIconTextGap(12);
            setPreferredSize(new Dimension(330, 56));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (selected) {
                g2.setColor(new Color(99, 102, 241, 16));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);
                g2.setColor(new Color(99, 102, 241));
                g2.setStroke(new BasicStroke(1.6f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
            } else {
                g2.setColor(new Color(247, 248, 252));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
            }

            // language chip (coloured dot)
            g2.setColor(colorOf.apply(language.code()));
            g2.fillOval(20, h / 2 - 9, 18, 18);

            // label
            g2.setColor(selected ? new Color(99, 102, 241) : Theme.TEXT_DIM);
            g2.setFont(getFont());
            FontMetrics metrics = g2.getFontMetrics();
            int tx = 52;
            int ty = h / 2 + metrics.getAscent() / 2 - 2;
            g2.drawString(language.label(), tx, ty);

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(330, 56);
        }
    }

    private static Color languageColor(String code) {
        return switch (code) {
            case "fr" -> new Color(236, 72, 153);   // pink
            case "en" -> new Color(34, 211, 238);   // cyan
            default   -> Theme.PURPLE;
        };
    }

    /** Accent colour of a programming language. */
    public static Color trackColor(String code) {
        return switch (code) {
            case "python"     -> new Color(55, 118, 171);   // python blue
            case "javascript" -> new Color(214, 184, 0);    // javascript yellow
            case "cpp"        -> new Color(0, 89, 156);     // C++ blue
            default           -> new Color(231, 111, 0);    // java orange
        };
    }
}
