package sothcheat.main;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import sothcheat.database.DBConnection;
import sothcheat.login.LoginContainer;
import sothcheat.manager.FormsManager;
import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {

    public Application() {
        init();
    }

    private void init() {
        if (!DBConnection.testConnection()) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to database!\n" +
                            "Please ensure MySQL is running and database exists.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1245, 800);
        setLocationRelativeTo(null);
        setContentPane(new LoginContainer());
        FormsManager.getInstance().initApplication(this);
    }

    public static void main(String[] args) {
        FlatInterFont.install();
        FlatLaf.registerCustomDefaultsSource("sothcheat.themes");
        UIManager.put("defaultFont", new Font(FlatInterFont.FAMILY,Font.PLAIN,13));
        FlatMacLightLaf.setup();
        EventQueue.invokeLater(() -> new Application().setVisible(true));
    }
}
