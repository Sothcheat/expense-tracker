package sothcheat.dashboard;

import com.formdev.flatlaf.FlatClientProperties;
import com.toedter.calendar.JCalendar;
import net.miginfocom.swing.MigLayout;
import sothcheat.components.Sidebar;
import sothcheat.manager.FormsManager;
import sothcheat.manager.SessionManager;
import sothcheat.models.Category;
import sothcheat.models.User;
import sothcheat.services.CategoryService;
import sothcheat.services.TransactionService;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class AddTransaction extends JPanel {

    private User currentUser;
    private CategoryService categoryService;
    private TransactionService transactionService;

    private JRadioButton expenseRadio;
    private JRadioButton incomeRadio;
    private JComboBox<String> categoryCombo;
    private JTextField dateField;
    private JTextField amountField;
    private JTextField descriptionField;

    private List<Category> currentCategories;

    public AddTransaction() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        categoryService = new CategoryService();
        transactionService = new TransactionService();
        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 0", "[280!][grow]", "[grow]"));

        Sidebar sidebar = new Sidebar("AddTransaction");
        JPanel contentArea = createContentArea();

        mainPanel.add(sidebar, "grow");
        mainPanel.add(contentArea, "grow");

        add(mainPanel);
    }

    private JPanel createContentArea() {
        JPanel contentArea = new JPanel(new MigLayout("fill, insets 20", "[center]", "[][][grow]"));
        contentArea.setBackground(Color.WHITE);

        // Header
        JPanel header = createHeader();

        // Form
        JPanel formPanel = createFormPanel();

        contentArea.add(header, "growx, wrap");
        contentArea.add(formPanel, "growx, top, wrap");

        return contentArea;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new MigLayout("fill", "[grow][]", "[]"));
        header.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Add Transaction");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +16;" +
                "foreground:#3D3828");

        JLabel subtitleLabel = new JLabel("You can add your expense and income here:");
        subtitleLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:lighten(@foreground,30%)");

        JPanel titlePanel = new JPanel(new MigLayout("insets 0", "[]", "[]0[]"));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel, "wrap");
        titlePanel.add(subtitleLabel);

        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setBackground(Color.WHITE);

        JLabel userIcon = new JLabel(new ImageIcon(getClass().getResource("/assets/homepage/Generic-avatar.png")));
        userIcon.putClientProperty(FlatClientProperties.STYLE, "font:+8");

        JLabel userName = new JLabel(currentUser.getUsername());
        userName.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;" +
                "foreground:#3D3828");

        userPanel.add(userIcon);
        userPanel.add(userName);

        header.add(titlePanel, "grow");
        header.add(userPanel);

        return header;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new MigLayout("fillx, insets 20", "[grow]", "[]20[]20[]20[]20[]"));
        formPanel.setBackground(Color.WHITE);

        JLabel instructionLabel = new JLabel("Please select and fill all of the form.");
        instructionLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +2;" +
                "foreground:#3D3828");

        // Transaction type selection
        JPanel typePanel = createTypeSelectionPanel();

        // Category, Date, Amount
        JPanel fieldsPanel = createFieldsPanel();

        // Description
        JPanel descriptionPanel = createDescriptionPanel();

        // Buttons
        JPanel buttonsPanel = createButtonsPanel();

        formPanel.add(instructionLabel, "wrap");
        formPanel.add(typePanel, "growx, wrap");
        formPanel.add(fieldsPanel, "growx, wrap");
        formPanel.add(descriptionPanel, "growx, wrap");
        formPanel.add(buttonsPanel, "growx");

        return formPanel;
    }

    private JPanel createTypeSelectionPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 15", "[grow]", "[]"));
        panel.setBackground(new Color(0xFEE394));
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:12");

        JLabel label = new JLabel("Choose your transaction type:");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold;" +
                "foreground:#3D3828");

        expenseRadio = new JRadioButton("Expense", true);
        incomeRadio = new JRadioButton("Income");

        ButtonGroup group = new ButtonGroup();
        group.add(expenseRadio);
        group.add(incomeRadio);

        expenseRadio.setBackground(new Color(0xFEE394));
        incomeRadio.setBackground(new Color(0xFEE394));

        expenseRadio.addActionListener(e -> loadCategories("expense"));
        incomeRadio.addActionListener(e -> loadCategories("income"));

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        radioPanel.setBackground(new Color(0xFEE394));
        radioPanel.add(expenseRadio);
        radioPanel.add(incomeRadio);

        panel.add(label, "split 2");
        panel.add(radioPanel, "pushx, right");

        return panel;
    }

    private JPanel createFieldsPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx", "[grow]", "[]5[]10[]5[]10[]5[] "));
        panel.setBackground(Color.WHITE);

        // Category
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:#3D3828");
        categoryCombo = new JComboBox<>();
        categoryCombo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Select an option");
        loadCategories("expense");

        // Date
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:#3D3828");
        dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        dateField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "YYYY-MM-DD");

        // Date picker button
        JButton datePickerBtn = new JButton(new ImageIcon(getClass().getResource("/assets/icons/calendar.png")));
        datePickerBtn.setToolTipText("Pick a date");
        datePickerBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FEE394;" +
                "foreground:#3D3828;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "arc:12");
        datePickerBtn.addActionListener(e -> showJCalendarPicker());

        // Amount
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:#3D3828");
        amountField = new JTextField();
        amountField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0.00");

        panel.add(categoryLabel, "wrap");
        panel.add(categoryCombo, "growx, wrap");

        panel.add(dateLabel, "wrap");
        panel.add(dateField, "split 2, growx");
        panel.add(datePickerBtn, "w 45!, wrap");

        panel.add(amountLabel, "wrap");
        panel.add(amountField, "growx");

        return panel;
    }

    private void showJCalendarPicker() {
        JDialog calendarDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Date", true);
        calendarDialog.setLayout(new BorderLayout());

        // Create JCalendar
        JCalendar calendar = new JCalendar();

        try {
            LocalDate currentDate = LocalDate.parse(dateField.getText().trim());
            Date date = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            calendar.setDate(date);
        } catch (Exception ignored) {
            calendar.setDate(new Date());
        }

        calendar.setBackground(Color.WHITE);
        calendar.setWeekOfYearVisible(false);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JButton todayBtn = new JButton("Today");
        todayBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FEE394;" +
                "foreground:#3D3828;" +
                "borderWidth:0;" +
                "focusWidth:0");
        todayBtn.addActionListener(e -> {
            calendar.setDate(new Date());
        });

        JButton selectBtn = new JButton("Select");
        selectBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FEE394;" +
                "foreground:#3D3828;" +
                "borderWidth:0;" +
                "focusWidth:0");
        selectBtn.addActionListener(e -> {
            Date selectedDate = calendar.getDate();
            LocalDate localDate = selectedDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            dateField.setText(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            calendarDialog.dispose();
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FFFFFF;" +
                "foreground:#3D3828;" +
                "borderWidth:2;" +
                "borderColor:#FEE394;" +
                "focusWidth:0");
        cancelBtn.addActionListener(e -> calendarDialog.dispose());

        buttonsPanel.add(todayBtn);
        buttonsPanel.add(selectBtn);
        buttonsPanel.add(cancelBtn);

        // Add components to dialog
        calendarDialog.add(calendar, BorderLayout.CENTER);
        calendarDialog.add(buttonsPanel, BorderLayout.SOUTH);

        calendarDialog.setSize(400, 350);
        calendarDialog.setLocationRelativeTo(this);
        calendarDialog.setVisible(true);
    }

    private JPanel createDescriptionPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx", "[grow]", "[][]"));
        panel.setBackground(Color.WHITE);

        JLabel descLabel = new JLabel("Description:");
        descLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:#3D3828");

        descriptionField = new JTextField();
        descriptionField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter description");
        descriptionField.setPreferredSize(new Dimension(0, 80));

        panel.add(descLabel, "wrap");
        panel.add(descriptionField, "growx, h 80!");

        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx", "[][][][grow][]", "[]"));
        panel.setBackground(Color.WHITE);

        JButton addBtn = new JButton("Add");
        addBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FEE394;" +
                "foreground:#3D3828;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "arc:12");
        addBtn.addActionListener(e -> handleAdd());

        JButton clearBtn = new JButton("Clear");
        clearBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#3D3828;" +
                "foreground:#FEE394;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "arc:12");
        clearBtn.addActionListener(e -> handleClear());

        JButton backBtn = new JButton("Back");
        backBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FFFFFF;" +
                "foreground:#3D3828;" +
                "borderWidth:1;" +
                "borderColor:#3D3828;" +
                "focusWidth:0;" +
                "arc:12");
        backBtn.addActionListener(e -> FormsManager.getInstance().showForm(new Dashboard()));

        panel.add(addBtn);
        panel.add(clearBtn);
        panel.add(backBtn, "skip, pushx, right");

        return panel;
    }

    private void loadCategories(String type) {
        categoryCombo.removeAllItems();
        currentCategories = categoryService.getCategoriesByUserAndType(currentUser.getUserId(), type);

        for (Category category : currentCategories) {
            categoryCombo.addItem(category.getCategoryName());
        }
    }

    private void handleAdd() {
        try {
            if (currentCategories.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "You don't have any categories yet.\nWould you like to create one now?",
                        "No Categories",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    FormsManager.getInstance().showForm(new ManageCategory());
                }
                return;
            }

            String type = expenseRadio.isSelected() ? "expense" : "income";
            int selectedIndex = categoryCombo.getSelectedIndex();

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select a category!");
                return;
            }

            int categoryId = currentCategories.get(selectedIndex).getCategoryId();
            String description = descriptionField.getText().trim();
            String amountStr = amountField.getText().trim();
            String dateStr = dateField.getText().trim();

            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a description!");
                return;
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Amount must be greater than 0!",
                            "Invalid Amount",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number for amount!",
                        "Invalid Amount",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate date;
            try {
                date = LocalDate.parse(dateStr);
                if (date.isAfter(LocalDate.now())) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "This date is in the future. Continue anyway?",
                            "Future Date",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid date (YYYY-MM-DD)!",
                        "Invalid Date",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = transactionService.addTransaction(
                    currentUser.getUserId(), categoryId, description, amount, date, type
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Transaction added successfully!");
                handleClear();
                // Refresh dashboard
                // FormsManager.getInstance().showForm(new Dashboard());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add transaction!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please check your data.");
            e.printStackTrace();
        }
    }

    private void handleClear() {
        categoryCombo.setSelectedIndex(-1);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        amountField.setText("");
        descriptionField.setText("");
        expenseRadio.setSelected(true);
        loadCategories("expense");
    }
}