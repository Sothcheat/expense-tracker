package sothcheat.dashboard;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import sothcheat.components.Sidebar;
import sothcheat.manager.SessionManager;
import sothcheat.models.Category;
import sothcheat.models.Transaction;
import sothcheat.models.User;
import sothcheat.services.CategoryService;
import sothcheat.services.TransactionService;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ViewAllIncome extends JPanel {

    private User currentUser;
    private TransactionService transactionService;
    private CategoryService categoryService;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JTextField dateField;
    private JComboBox<String> sortCombo;
    private JTable incomeTable;
    private DefaultTableModel tableModel;
    private List<Transaction> allIncome;
    private List<Category> incomeCategories;

    public ViewAllIncome() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        transactionService = new TransactionService();
        categoryService = new CategoryService();
        init();
        loadData();
    }

    private void init() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 0", "[280!][grow]", "[grow]"));

        Sidebar sidebar = new Sidebar("ViewAllIncome");
        JPanel contentArea = createContentArea();

        mainPanel.add(sidebar, "grow");
        mainPanel.add(contentArea, "grow");

        add(mainPanel);
    }

    private void loadData() {
        allIncome = transactionService.getTransactionsByUserAndType(currentUser.getUserId(), "income");
        incomeCategories = categoryService.getCategoriesByUserAndType(currentUser.getUserId(), "income");

        categoryCombo.removeAllItems();
        categoryCombo.addItem("Select an option");
        for (Category category : incomeCategories) {
            categoryCombo.addItem(category.getCategoryName());
        }

        displayTransactions(allIncome);
    }

    private void displayTransactions(List<Transaction> transactions) {
        tableModel.setRowCount(0);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        for (Transaction transaction : transactions) {
            tableModel.addRow(new Object[]{
                    transaction.getDate().toString(),
                    transaction.getCategoryName(),
                    transaction.getDescription(),
                    currencyFormat.format(transaction.getAmount()),
                    transaction.getTransactionId()
            });
        }
    }

    private JPanel createContentArea() {
        JPanel contentArea = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[]20[]20[grow]"));
        contentArea.setBackground(Color.WHITE);

        JPanel header = createHeader();
        JPanel filterPanel = createFilterPanel();
        JPanel tablePanel = createTablePanel();

        contentArea.add(header, "growx, wrap");
        contentArea.add(filterPanel, "growx, wrap");
        contentArea.add(tablePanel, "grow");

        return contentArea;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new MigLayout("fill", "[grow][]", "[]"));
        header.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("View All Income");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +16;" +
                "foreground:#3D3828");

        JLabel subtitleLabel = new JLabel("You can view and manage all your income here:");
        subtitleLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:lighten(@foreground,30%)");

        JPanel titlePanel = new JPanel(new MigLayout("insets 0", "[]", "[]0[]"));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel, "wrap");
        titlePanel.add(subtitleLabel);

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

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx", "[grow]", "[]10[]"));
        panel.setBackground(Color.WHITE);

        // Search bar
        JPanel searchPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]"));
        searchPanel.setBackground(new Color(0xFEE394));
        searchPanel.putClientProperty(FlatClientProperties.STYLE, "arc:12");

        searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search:");
        searchField.addActionListener(e -> applyFilters());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() { applyFilters(); }
            public void insertUpdate (DocumentEvent e) { update(); }
            public void removeUpdate (DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });

        searchPanel.add(searchField, "growx");

        // Filters
        JPanel filtersPanel = new JPanel(new MigLayout("insets 0 0 0 2", "[]8[]8[]8[]8[]10[]", "[]"));
        filtersPanel.setBackground(Color.WHITE);

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:#3D3828");
        categoryCombo = new JComboBox<>();

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:#3D3828");
        dateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:#3D3828");
        sortCombo = new JComboBox<>(new String[]{"Amount", "Date", "Category"});

        JButton applyBtn = new JButton("Apply Filter");
        applyBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FEE394;" +
                "foreground:#3D3828;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "arc:12");
        applyBtn.addActionListener(e -> applyFilters());

        JButton resetBtn = new JButton("Reset");
        resetBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FFFFFF;" +
                "foreground:#3D3828;" +
                "borderWidth:1;" +
                "borderColor:#3D3828;" +
                "focusWidth:0;" +
                "arc:12");
        resetBtn.addActionListener(e -> resetFilters());

        filtersPanel.add(categoryLabel);
        filtersPanel.add(categoryCombo, "growx, wmin 180");
        filtersPanel.add(dateLabel);
        filtersPanel.add(dateField, "growx, wmin 140");
        filtersPanel.add(sortLabel);
        filtersPanel.add(sortCombo, "growx, wmin 140");
        filtersPanel.add(new JLabel(""), "growx, pushx");
        filtersPanel.add(applyBtn, "gapright 10");
        filtersPanel.add(resetBtn);

        panel.add(searchPanel, "growx, wrap");
        panel.add(filtersPanel, "growx");

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"Date", "Category", "Description", "Amount", "Action", "ID"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        incomeTable = new JTable(tableModel);
        incomeTable.setRowHeight(36);
        incomeTable.setShowGrid(true);

        incomeTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "" +
                "height:36;" +
                "background:#3D3828;" +
                "foreground:#FEE394;" +
                "font:bold");

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        incomeTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        incomeTable.getColumnModel().getColumn(4).setCellRenderer(new ActionCellRenderer());
        incomeTable.getColumnModel().getColumn(4).setCellEditor(new ActionCellEditor());
        incomeTable.getColumnModel().getColumn(5).setMinWidth(0);
        incomeTable.getColumnModel().getColumn(5).setMaxWidth(0);
        incomeTable.getColumnModel().getColumn(5).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(incomeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0), 1));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        String dateText = dateField.getText().trim();
        String sortBy = (String) sortCombo.getSelectedItem();

        List<Transaction> filtered = allIncome.stream()
                .filter(t -> {
                    if (!searchText.isEmpty()) {
                        return t.getDescription().toLowerCase().contains(searchText) ||
                                t.getCategoryName().toLowerCase().contains(searchText);
                    }
                    return true;
                })
                .filter(t -> {
                    if (selectedCategory != null && !selectedCategory.equals("Select an option")) {
                        return t.getCategoryName().equals(selectedCategory);
                    }
                    return true;
                })
                .filter(t -> {
                    if (!dateText.isEmpty() && !dateText.equals(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))) {
                        try {
                            LocalDate filterDate = LocalDate.parse(dateText);
                            return t.getDate().equals(filterDate);
                        } catch (Exception e) {
                            return true;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        if ("Amount".equals(sortBy)) {
            filtered.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));
        } else if ("Date".equals(sortBy)) {
            filtered.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        } else if ("Category".equals(sortBy)) {
            filtered.sort((a, b) -> a.getCategoryName().compareTo(b.getCategoryName()));
        }

        displayTransactions(filtered);
    }

    private void resetFilters() {
        searchField.setText("");
        categoryCombo.setSelectedIndex(0);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        sortCombo.setSelectedIndex(0);
        displayTransactions(allIncome);
    }

    private void editTransaction(int transactionId) {
        Transaction transaction = allIncome.stream()
                .filter(t -> t.getTransactionId() == transactionId)
                .findFirst()
                .orElse(null);

        if (transaction == null) {
            JOptionPane.showMessageDialog(this, "Transaction not found!");
            return;
        }

        showEditTransactionDialog(transaction);
    }

    private void showEditTransactionDialog(Transaction transaction) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Expense", true);
        dialog.setLayout(new MigLayout("fill, insets 20", "[grow]", "[]10[]10[]10[]10[]10[]"));

        JLabel titleLabel = new JLabel("Edit Income");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +4;foreground:#3D3828");

        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryCombo = new JComboBox<>();
        for (Category cat : incomeCategories) {
            categoryCombo.addItem(cat.getCategoryName());
            if (cat.getCategoryId() == transaction.getCategoryId()) {
                categoryCombo.setSelectedItem(cat.getCategoryName());
            }
        }

        JLabel dateLabel = new JLabel("Date:");
        JTextField dateField = new JTextField(transaction.getDate().toString());

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField(transaction.getAmount().toString());

        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField(transaction.getDescription());

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.putClientProperty(FlatClientProperties.STYLE, "background:#FEE394;foreground:#3D3828;borderWidth:0;focusWidth:0");

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.putClientProperty(FlatClientProperties.STYLE, "background:#FFFFFF;foreground:#3D3828;borderWidth:1;borderColor:#3D3828;focusWidth:0");

        saveBtn.addActionListener(e -> {
            try {
                int selectedIndex = categoryCombo.getSelectedIndex();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(dialog, "Please select a category!");
                    return;
                }

                int categoryId = incomeCategories.get(selectedIndex).getCategoryId();
                String description = descField.getText().trim();
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                LocalDate date = LocalDate.parse(dateField.getText().trim());

                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a description!");
                    return;
                }

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Amount must be greater than 0!");
                    return;
                }

                boolean success = transactionService.updateTransaction(
                        transaction.getTransactionId(), categoryId, description, amount, date);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Transaction updated successfully!");
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update transaction!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input! Please check your data.");
                ex.printStackTrace();
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(saveBtn);
        buttonsPanel.add(cancelBtn);

        dialog.add(titleLabel, "wrap");
        dialog.add(categoryLabel, "wrap");
        dialog.add(categoryCombo, "growx, wrap");
        dialog.add(dateLabel, "wrap");
        dialog.add(dateField, "growx, wrap");
        dialog.add(amountLabel, "wrap");
        dialog.add(amountField, "growx, wrap");
        dialog.add(descLabel, "wrap");
        dialog.add(descField, "growx, wrap");
        dialog.add(buttonsPanel, "growx");

        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    class ActionCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton editBtn;
        private JButton deleteBtn;

        public ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editBtn = new JButton(new ImageIcon(getClass().getResource("/assets/icons/edit_16px.png")));
            deleteBtn = new JButton(new ImageIcon(getClass().getResource("/assets/icons/delete_16.png")));

            editBtn.setPreferredSize(new Dimension(30, 30));
            deleteBtn.setPreferredSize(new Dimension(30, 30));

            add(editBtn);
            add(deleteBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ActionCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private JPanel panel;
        private JButton editBtn;
        private JButton deleteBtn;
        private int currentRow;

        public ActionCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editBtn = new JButton(new ImageIcon(getClass().getResource("/assets/icons/edit_16px.png")));
            deleteBtn = new JButton(new ImageIcon(getClass().getResource("/assets/icons/delete_16.png")));

            editBtn.setPreferredSize(new Dimension(30, 30));
            deleteBtn.setPreferredSize(new Dimension(30, 30));

            editBtn.addActionListener(e -> {
                stopCellEditing();
                try {
                    int transactionId = (Integer) tableModel.getValueAt(currentRow, 5);

                    Transaction transaction = allIncome.stream()
                            .filter(t -> t.getTransactionId() == transactionId)
                            .findFirst()
                            .orElse(null);

                    if (transaction != null) {
                        editTransaction(transaction.getTransactionId());
                    } else {
                        JOptionPane.showMessageDialog(panel, "Transaction not found!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            deleteBtn.addActionListener(e -> {
                stopCellEditing();
                try {
                    int transactionId = (Integer) tableModel.getValueAt(currentRow, 5);

                    Transaction transaction = allIncome.stream()
                            .filter(t -> t.getTransactionId() == transactionId)
                            .findFirst()
                            .orElse(null);

                    if (transaction != null) {
                        deleteTransaction(transaction.getTransactionId());
                    } else {
                        JOptionPane.showMessageDialog(panel, "Transaction not found!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }
        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    private void deleteTransaction(int transactionId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this transaction?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = transactionService.deleteTransaction(transactionId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Transaction deleted successfully!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete transaction!");
            }
        }
    }
}