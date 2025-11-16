package sothcheat.login;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class LoginContainer extends JPanel {
    private static LoginContainer instance;

    private JPanel rightPanel;
    private JPanel leftPanel;

    public LoginContainer() {
        instance = this;
        init();
    }

    public static LoginContainer getInstance() {
        return instance;
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 0", "[50%][50%]", "[grow]"));

        leftPanel = createLeftPanel();

        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        rightPanel.add(new Login(), BorderLayout.CENTER);

        add(leftPanel, "grow");
        add(rightPanel, "grow");
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0","[center]","[center]"));
        panel.setBackground(new Color(0xFEE394));

        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/logo/IMG_0197.JPG"));
        Image scaleImage = icon.getImage().getScaledInstance(630,630,Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaleImage));
        panel.add(logoLabel);
        return panel;
    }

    public void showForm(JComponent form) {
        rightPanel.removeAll();
        rightPanel.add(form, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }
}
