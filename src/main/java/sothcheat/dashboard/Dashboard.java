package sothcheat.dashboard;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import sothcheat.components.Sidebar;
import sothcheat.database.DBConnection;
import sothcheat.manager.FormsManager;
import sothcheat.manager.SessionManager;
import sothcheat.models.Transaction;
import sothcheat.models.User;
import sothcheat.services.TransactionService;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class Dashboard extends JPanel {

    private JLabel expenseAmountLabel;
    private JLabel balanceAmountLabel;
    private JLabel incomeAmountLabel;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private TransactionService transactionService;
    private User currentUser;
    private JComboBox<String> monthSelector;
    private YearMonth selectedMonth;

    public Dashboard() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        transactionService = new TransactionService();
        init();
        loadData();
    }

    private void init() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 0", "[280!][grow]", "[grow]"));
        Sidebar sidebar = new Sidebar("Dashboard");
        JPanel contentArea = createContentArea();

        mainPanel.add(sidebar, "grow");
        mainPanel.add(contentArea, "grow");

        add(mainPanel);
    }

    private void loadData() {
        BigDecimal expense = transactionService.getTotalExpense(currentUser.getUserId(), selectedMonth);
        BigDecimal income = transactionService.getTotalIncome(currentUser.getUserId(), selectedMonth);
        BigDecimal balance = transactionService.getBalance(currentUser.getUserId(), selectedMonth);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        expenseAmountLabel.setText(currencyFormat.format(expense));
        incomeAmountLabel.setText(currencyFormat.format(income));
        balanceAmountLabel.setText(currencyFormat.format(balance));

        List<Transaction> transactions = transactionService.getRecentTransactionsByUser(currentUser.getUserId(), 20);
        tableModel.setRowCount(0);

        for (Transaction transaction : transactions) {
            if ("expense".equals(transaction.getType())) {
                tableModel.addRow(new Object[]{
                        transaction.getDate().toString(),
                        transaction.getCategoryName(),
                        transaction.getDescription(),
                        currencyFormat.format(transaction.getAmount())
                });
            }
        }
    }

    private JPanel createContentArea() {
        JPanel contentArea = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[]20[]20[]20[]"));
        contentArea.setBackground(Color.WHITE);

        JPanel header = createHeader();

        // Stats cards
        JPanel statsPanel = createStatsPanel();

        // Recent expenses table
        JPanel tablePanel = createTablePanel();

        // Quick action buttons
        JPanel quickActionsPanel = createQuickActionsPanel();

        contentArea.add(header, "growx, wrap");
        contentArea.add(statsPanel, "growx, wrap");
        contentArea.add(tablePanel, "grow, wrap");
        contentArea.add(quickActionsPanel, "growx");

        return contentArea;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new MigLayout("fill", "[grow][]", "[]"));
        header.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +16;" +
                "foreground:#3D3828");

        selectedMonth = YearMonth.now();

        String[] months = new String[12];
        YearMonth current = YearMonth.now();
        for (int i = 0; i < 12; i++) {
            YearMonth month = current.minusMonths(i);
            months[i] = month.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        }

        monthSelector = new JComboBox<>(months);
        monthSelector.addActionListener(e -> {
            String selected = (String) monthSelector.getSelectedItem();
            selectedMonth = YearMonth.parse(selected, DateTimeFormatter.ofPattern("MMMM yyyy"));
            loadData();  // Reload with new month
        });

        JLabel subtitleLabel = new JLabel("Welcome to Expense Tracker");
        subtitleLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:lighten(@foreground,30%)");

        JPanel titlePanel = new JPanel(new MigLayout("insets 0", "[]", "[]0[]"));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.add(monthSelector, "gapleft 20");
        titlePanel.add(subtitleLabel, "newline");

        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setBackground(Color.WHITE);

        JLabel userIcon = new JLabel(new ImageIcon(getClass().getResource("/assets/homepage/Generic-avatar.png")));
        userIcon.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:+8");

        JLabel userName = new JLabel(currentUser.getUsername());
        userName.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;" +
                "foreground:#3D3828");

        JButton signOutBtn = new JButton(new ImageIcon(getClass().getResource("/assets/icons/logout.png")));
        signOutBtn.setToolTipText("Sign Out");
        signOutBtn.putClientProperty(FlatClientProperties.STYLE,"" +
                "font:bold: +4;" +
                "foreground:#3D3828;" +
                "background:lighten(@background,3%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "arc:20");
        signOutBtn.setPreferredSize(new Dimension(35, 35));
        signOutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signOutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to sign out?",
                    "Sign Out",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                SessionManager.getInstance().logout();
                FormsManager.getInstance().showForm(new sothcheat.login.LoginContainer());
            }
        });

        userPanel.add(userIcon);
        userPanel.add(userName);
        userPanel.add(signOutBtn);

        header.add(titlePanel, "grow");
        header.add(userPanel);

        return header;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new MigLayout("fill, insets 0", "[grow][grow][grow]", "[]"));
        statsPanel.setBackground(Color.WHITE);

        JPanel expenseCard = createStatCard("Expense", "$0.00", new Color(0xFEE394), "#4876FD");
        expenseAmountLabel = (JLabel) getAmountLabel(expenseCard);

        JPanel balanceCard = createStatCard("Balance", "$0.00", Color.WHITE, "#8B7355");
        balanceAmountLabel = (JLabel) getAmountLabel(balanceCard);

        JPanel incomeCard = createStatCard("Income", "$0.00", Color.WHITE, "#C57B57");
        incomeAmountLabel = (JLabel) getAmountLabel(incomeCard);

        statsPanel.add(expenseCard, "grow");
        statsPanel.add(balanceCard, "grow");
        statsPanel.add(incomeCard, "grow");

        return statsPanel;
    }

    private JLabel getAmountLabel(JPanel card) {
        Component[] components = card.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] subComponents = panel.getComponents();
                for (Component subComp : subComponents) {
                    if (subComp instanceof JLabel) {
                        JLabel label = (JLabel) subComp;
                        if (label.getText().startsWith("$")) {
                            return label;
                        }
                    }
                }
            }
        }
        return new JLabel("$0.00");
    }

    private JPanel createStatCard(String title, String amount, Color bgColor, String amountColor) {
        JPanel card = new JPanel(new MigLayout("fill, insets 20", "[]", "[]10[]"));
        card.setBackground(bgColor);
        card.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:12;" +
                "border:1,1,1,1,shade(@background,10%),1,12");

        JLabel titleLabel = new JLabel(title);
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +8;" +
                "foreground:#3D3828");

        JPanel amountPanel = new JPanel(new MigLayout("insets 0", "[]", "[]0[]"));
        amountPanel.setBackground(bgColor);

        JLabel monthLabel = new JLabel("This month");
        monthLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:-1;" +
                "foreground:lighten(@foreground,30%)");

        JLabel amountLabel = new JLabel(amount);
        amountLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +18;" +
                "foreground:" + amountColor);

        amountPanel.add(monthLabel, "wrap");
        amountPanel.add(amountLabel);

        card.add(titleLabel, "wrap");
        card.add(amountPanel);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        JLabel tableTitle = new JLabel("Recent Expenses:");
        tableTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +4;" +
                "foreground:#3D3828");

        String[] columns = {"Date", "Category", "Description", "Amount"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setRowHeight(36);
        expenseTable.setShowGrid(true);
        expenseTable.setIntercellSpacing(new Dimension(0, 0));

        expenseTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "" +
                "height:36;" +
                "background:#3D3828;" +
                "foreground:#FEE394;" +
                "font:bold");

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        expenseTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0), 1));

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0", "[]20[]20[]20[]", "[]"));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Quick action:");
        title.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;" +
                "foreground:#3D3828");

        JButton addBtn = createQuickActionButton("Add Transaction", new Color(0xFEE394), false);
        JButton viewExpenseBtn = createQuickActionButton("View All Expense", new Color(0x3D3828), true);
        JButton viewIncomeBtn = createQuickActionButton("View All Income", Color.WHITE, false);
        JButton manageCategoryBtn = createQuickActionButton("Manage Category", Color.WHITE, false);

        addBtn.addActionListener(e -> FormsManager.getInstance().showForm(new AddTransaction()));
        viewExpenseBtn.addActionListener(e -> FormsManager.getInstance().showForm(new ViewAllExpense()));
        viewIncomeBtn.addActionListener(e -> FormsManager.getInstance().showForm(new ViewAllIncome()));
        manageCategoryBtn.addActionListener(e -> FormsManager.getInstance().showForm(new ManageCategory()));

        panel.add(title);
        panel.add(addBtn);
        panel.add(viewExpenseBtn);
        panel.add(viewIncomeBtn);
        panel.add(manageCategoryBtn);

        return panel;
    }

    private JButton createQuickActionButton(String text, Color bgColor, boolean isDark) {
        JButton button = new JButton(text);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (isDark) {
            button.putClientProperty(FlatClientProperties.STYLE, "" +
                    "background:#3D3828;" +
                    "foreground:#FEE394;" +
                    "borderWidth:0;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;" +
                    "arc:12");
        } else if (bgColor.equals(Color.WHITE)) {
            button.putClientProperty(FlatClientProperties.STYLE, "" +
                    "background:#FFFFFF;" +
                    "foreground:#3D3828;" +
                    "borderWidth:2;" +
                    "borderColor:#FEE394;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;" +
                    "arc:12");
        } else {
            button.putClientProperty(FlatClientProperties.STYLE, "" +
                    "background:#FEE394;" +
                    "foreground:#3D3828;" +
                    "borderWidth:0;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;" +
                    "arc:12");
        }

        return button;
    }
}