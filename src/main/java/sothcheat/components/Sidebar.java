package sothcheat.components;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import sothcheat.dashboard.*;
import sothcheat.login.LoginContainer;
import sothcheat.manager.FormsManager;
import sothcheat.manager.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class Sidebar extends JPanel {

    private String activePage;
    private JButton dashboardBtn;
    private JButton addTransactionBtn;
    private JButton viewExpenseBtn;
    private JButton viewIncomeBtn;
    private JButton manageCategoryBtn;
    private JButton signOutBtn;

    public Sidebar(String activePage) {
        this.activePage = activePage;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fillx, insets 20", "[grow]", "[]20[]20[]20[]20[]20[]push[]"));
        setBackground(new Color(0xFEE394));

        JLabel titleLabel = new JLabel("Expense Tracker");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +12;" +
                "foreground:#3D3828");

        dashboardBtn = createMenuButton("Dashboard", "/assets/icons/dashboard.png", activePage.equals("Dashboard"));
        addTransactionBtn = createMenuButton("Add Transaction", "/assets/icons/add.png", activePage.equals("AddTransaction"));
        viewExpenseBtn = createMenuButton("View All Expense", "/assets/icons/expense.png", activePage.equals("ViewAllExpense"));
        viewIncomeBtn = createMenuButton("View All Income", "/assets/icons/income.png", activePage.equals("ViewAllIncome"));
        manageCategoryBtn = createMenuButton("Manage Category", "/assets/icons/category.png", activePage.equals("ManageCategory"));

        signOutBtn = createMenuButton("Sign out", "/assets/icons/logout.png",false);

        dashboardBtn.addActionListener(e -> FormsManager.getInstance().showForm(new Dashboard()));
        addTransactionBtn.addActionListener(e -> FormsManager.getInstance().showForm(new AddTransaction()));
        viewExpenseBtn.addActionListener(e -> FormsManager.getInstance().showForm(new ViewAllExpense()));
        viewIncomeBtn.addActionListener(e -> FormsManager.getInstance().showForm(new ViewAllIncome()));
        manageCategoryBtn.addActionListener(e -> FormsManager.getInstance().showForm(new ManageCategory()));
        signOutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to sign out?",
                    "Sign Out",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                SessionManager.getInstance().logout();
                FormsManager.getInstance().showForm(new LoginContainer());
            }
        });

        add(titleLabel, "wrap");
        add(dashboardBtn, "growx, wrap");
        add(addTransactionBtn, "growx, wrap");
        add(viewExpenseBtn, "growx, wrap");
        add(viewIncomeBtn, "growx, wrap");
        add(manageCategoryBtn, "growx, wrap");
        add(signOutBtn, "growx");
    }

    private JButton createMenuButton(String text, String iconPath, boolean isActive) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ImageIcon normalIcon = null;
        ImageIcon hoverIcon = null;

        try {
            URL iconUrl = getClass().getResource(iconPath);
            if (iconUrl != null) {
                normalIcon = new ImageIcon(iconUrl);

                // Try to load hover icon (blue version)
                String hoverPath = iconPath.replace(".png", "_blue.png");
                URL hoverUrl = getClass().getResource(hoverPath);
                if (hoverUrl != null) {
                    hoverIcon = new ImageIcon(hoverUrl);
                }
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, "Can't find the icon.");
        }


        if (normalIcon != null) {
            button.setIcon(normalIcon);
            button.setIconTextGap(10);

            final ImageIcon finalNormalIcon = normalIcon;
            final ImageIcon finalHoverIcon = hoverIcon;

            if (isActive) {
                if (finalHoverIcon != null) {
                    button.setIcon(finalHoverIcon);
                }
                button.putClientProperty(FlatClientProperties.STYLE,
                        "font:bold;" +
                                "foreground:#4876FD;" +
                                "arc:0");
            } else {
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (finalHoverIcon != null) {
                            button.setIcon(finalHoverIcon);
                        }
                        button.setForeground(new Color(0x4876FD));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        button.setIcon(finalNormalIcon);
                        button.setForeground(new Color(0x3D3828));
                    }
                });

                button.putClientProperty(FlatClientProperties.STYLE,
                        "foreground:#3D3828");
            }
        }

        return button;
    }
}