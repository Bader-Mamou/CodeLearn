package view;

import model.Role;
import view.components.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Login screen. It only raises events; the login controller decides what
 * happens next.
 */
public class LoginView extends JPanel {

    public interface Listener {
        void onLogin(String username, String password, Role role);
    }

    private final JComboBox<Role> roleCombo = Theme.comboBox(new Role[0]);
    private Listener listener;

    public LoginView() {
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
        box.setPreferredSize(new Dimension(420, 560));
        box.setMaximumSize(new Dimension(480, 640));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;

        /* ------------------------------------------------------------ header */
        JLabel logo = new JLabel("\u2212\u003e CodeLearn");
        logo.setFont(Theme.fontBold(30));
        logo.setForeground(Theme.PURPLE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Apprenez \u00e0 coder, niveau par niveau.");
        tagline.setFont(Theme.fontPlain(13));
        tagline.setForeground(Theme.MUTED);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Bienvenue !");
        title.setFont(Theme.fontBold(26));
        title.setForeground(Theme.TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Connectez-vous pour continuer");
        sub.setFont(Theme.fontPlain(14));
        sub.setForeground(Theme.TEXT_DIM);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        int y = 0;
        c.gridy = y++;
        c.insets = new Insets(0, 0, 4, 0);
        box.add(logo, c);

        c.gridy = y++;
        c.insets = new Insets(2, 0, 14, 0);
        box.add(tagline, c);

        c.gridy = y++;
        c.insets = new Insets(0, 0, 2, 0);
        box.add(title, c);

        c.gridy = y++;
        c.insets = new Insets(4, 0, 26, 0);
        box.add(sub, c);

        /* ------------------------------------------------------------- fields */
        JTextField username = Theme.textField();
        JPasswordField password = Theme.passwordField();

        y = addField(box, c, y, "Identifiant", username, 16);
        y = addField(box, c, y, "Mot de passe", password, 16);
        y = addField(box, c, y, "R\u00f4le", roleCombo, 16);

        /* ------------------------------------------------------------- action */
        JButton connect = Theme.button("Se connecter", "\u27a1");
        connect.setAlignmentX(Component.CENTER_ALIGNMENT);
        connect.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        connect.addActionListener(e -> {
            Role selected = (Role) roleCombo.getSelectedItem();
            if (selected != null && listener != null) {
                listener.onLogin(username.getText().trim(),
                        new String(password.getPassword()), selected);
            }
        });

        c.gridy = y++;
        c.insets = new Insets(22, 0, 10, 0);
        box.add(connect, c);

        JLabel hint = new JLabel("D\u00e9mo : etudiant / 1234 \u00b7 sara02 / 1234 \u00b7 admin / 1234");
        hint.setFont(Theme.fontPlain(10));
        hint.setForeground(new Color(150, 155, 175));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        c.gridy = y++;
        c.insets = new Insets(8, 0, 0, 0);
        box.add(hint, c);

        /* center the card in the panel */
        add(box, gc);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setRoles(List<Role> roles) {
        roleCombo.removeAllItems();
        for (Role role : roles) {
            roleCombo.addItem(role);
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Connexion",
                JOptionPane.WARNING_MESSAGE);
    }

    private int addField(JPanel box, GridBagConstraints c, int y, String labelText,
                         JComponent field, int topInset) {
        JLabel label = new JLabel(labelText);
        label.setFont(Theme.fontBold(11));
        label.setForeground(Theme.MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        c.gridy = y++;
        c.insets = new Insets(topInset, 0, 4, 0);
        box.add(label, c);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setPreferredSize(new Dimension(330, 44));

        c.gridy = y++;
        c.insets = new Insets(0, 0, 0, 0);
        box.add(field, c);
        return y;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        // soft mesh gradient background
        GradientPaint gp = new GradientPaint(0, 0,
                new Color(245, 244, 253),
                getWidth(), getHeight(),
                new Color(230, 236, 250));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // decorative blobs
        g2.setColor(new Color(99, 102, 241, 14));
        g2.fillOval(getWidth() - 220, -110, 360, 360);
        g2.setColor(new Color(34, 211, 238, 10));
        g2.fillOval(-120, getHeight() - 200, 320, 320);
        g2.setColor(new Color(236, 72, 153, 8));
        g2.fillOval(getWidth() - 160, getHeight() - 120, 260, 260);

        g2.dispose();
    }
}
