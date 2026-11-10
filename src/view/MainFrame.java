package view;

import javax.swing.*;
import java.awt.*;

/** Main application window that swaps between the login and dashboard views. */
public class MainFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    public MainFrame() {
        setTitle("CodeLearn \u2014 Plateforme d'apprentissage");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1420, Math.max(1180, screen.width - 80));
        int height = Math.min(900, Math.max(700, screen.height - 80));
        setSize(width, height);
        setMinimumSize(new Dimension(1180, 700));
        setLocationRelativeTo(null);
        add(root);
    }

    public void addView(String key, JComponent view) {
        root.add(view, key);
    }

    public void showView(String key) {
        cards.show(root, key);
    }
}
