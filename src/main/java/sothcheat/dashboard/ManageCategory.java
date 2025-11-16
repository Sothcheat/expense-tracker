package sothcheat.dashboard;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import sothcheat.components.Sidebar;
import sothcheat.manager.SessionManager;
import sothcheat.models.Category;
import sothcheat.models.User;
import sothcheat.services.CategoryService;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ManageCategory extends JPanel {

    private User currentUser;
    private CategoryService categoryService;

    private JTextField searchField;
    private JComboBox<String> sortCombo;
    private JTable categoryTable;
    private DefaultTableModel tableModel;

    private List<Category> allCategories;

    public ManageCategory() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        categoryService = new CategoryService();
        init();
        loadData();
    }

    private void init() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 0", "[280!][grow]", "[grow]"));

        Sidebar sidebar = new Sidebar("ManageCategory");
        JPanel contentArea = createContentArea();

        mainPanel.add(sidebar, "grow");
        mainPanel.add(contentArea, "grow");

        add(mainPanel);
    }

    private void loadData() {
        allCategories = categoryService.getCategoriesByUser(currentUser.getUserId());
        displayCategories(allCategories);
    }

    private void displayCategories(List<Category> categories) {
        tableModel.setRowCount(0);

        for (Category category : categories) {
            String type = category.getType();
            String displayType = type.substring(0, 1).toUpperCase() + type.substring(1);

            tableModel.addRow(new Object[]{
                    category.getCategoryName(),
                    displayType,
                    category.getCategoryId()
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

        JLabel titleLabel = new JLabel("Manage Category");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +16;" +
                "foreground:#3D3828");

        JLabel subtitleLabel = new JLabel("Create, edit or delete your expense & income category");
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

        // Sort and Add button
        JPanel actionsPanel = new JPanel(new MigLayout("insets 0", "[][]push[]", "[]"));
        actionsPanel.setBackground(Color.WHITE);

        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:#3D3828");
        sortCombo = new JComboBox<>(new String[]{"Ascending", "Descending"});
        sortCombo.addActionListener(e -> applyFilters());

        JButton addCategoryBtn = new JButton("Add new category");
        addCategoryBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FEE394;" +
                "foreground:#3D3828;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "arc:12");
        addCategoryBtn.addActionListener(e -> showAddCategoryDialog());

        actionsPanel.add(sortLabel);
        actionsPanel.add(sortCombo, "w 150!");
        actionsPanel.add(addCategoryBtn);

        panel.add(searchPanel, "growx, wrap");
        panel.add(actionsPanel, "growx");

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"Category", "Type", "Action"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return Object.class;
                }
                return String.class;
            }
        };

        categoryTable = new JTable(tableModel);
        categoryTable.setRowHeight(36);
        categoryTable.setShowGrid(true);

        categoryTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "" +
                "height:36;" +
                "background:#3D3828;" +
                "foreground:#FEE394;" +
                "font:bold");

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        categoryTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        categoryTable.getColumnModel().getColumn(2).setCellRenderer(new ActionCellRenderer());
        categoryTable.getColumnModel().getColumn(2).setCellEditor(new ActionCellEditor());

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0), 1));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String sortOrder = (String) sortCombo.getSelectedItem();

        List<Category> filtered = allCategories.stream()
                .filter(c -> {
                    if (!searchText.isEmpty()) {
                        return c.getCategoryName().toLowerCase().contains(searchText) ||
                                c.getType().toLowerCase().contains(searchText);
                    }
                    return true;
                })
                .collect(Collectors.toList());

        if ("Ascending".equals(sortOrder)) {
            filtered.sort((a, b) -> a.getCategoryName().compareToIgnoreCase(b.getCategoryName()));
        } else {
            filtered.sort((a, b) -> b.getCategoryName().compareToIgnoreCase(a.getCategoryName()));
        }

        displayCategories(filtered);
    }

    private void showAddCategoryDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Category", true);
        dialog.setLayout(new MigLayout("fill, insets 20", "[grow]", "[]10[]10[]10[]"));

        JLabel titleLabel = new JLabel("Add New Category");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +4;" +
                "foreground:#3D3828");

        JLabel nameLabel = new JLabel("Category Name:");
        JTextField nameField = new JTextField();
        nameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter category name");

        JLabel typeLabel = new JLabel("Type:");
        JRadioButton expenseRadio = new JRadioButton("Expense", true);
        JRadioButton incomeRadio = new JRadioButton("Income");
        ButtonGroup group = new ButtonGroup();
        group.add(expenseRadio);
        group.add(incomeRadio);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(expenseRadio);
        radioPanel.add(incomeRadio);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        saveBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FEE394;" +
                "foreground:#3D3828;" +
                "borderWidth:0;" +
                "focusWidth:0");

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FFFFFF;" +
                "foreground:#3D3828;" +
                "borderWidth:1;" +
                "borderColor:#3D3828;" +
                "focusWidth:0");

        saveBtn.addActionListener(e -> {
            String categoryName = nameField.getText().trim();
            if (categoryName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a category name!");
                return;
            }

            String type = expenseRadio.isSelected() ? "expense" : "income";
            boolean success = categoryService.addCategory(currentUser.getUserId(), categoryName, type);

            if (success) {
                JOptionPane.showMessageDialog(dialog, "Category added successfully!");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add category!");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(saveBtn);
        buttonsPanel.add(cancelBtn);

        dialog.add(titleLabel, "wrap");
        dialog.add(nameLabel, "wrap");
        dialog.add(nameField, "growx, wrap");
        dialog.add(typeLabel, "wrap");
        dialog.add(radioPanel, "wrap");
        dialog.add(buttonsPanel, "growx");

        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditCategoryDialog(int categoryId, String currentName, String currentType) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Category", true);
        dialog.setLayout(new MigLayout("fill, insets 20", "[grow]", "[]10[]10[]"));

        JLabel titleLabel = new JLabel("Edit Category");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +4;" +
                "foreground:#3D3828");

        JLabel nameLabel = new JLabel("Category Name:");
        nameLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:#3D3828");
        JTextField nameField = new JTextField(currentName);
        nameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter category name");

        JLabel typeLabel = new JLabel("Type:");
        JRadioButton expenseRadio = new JRadioButton("Expense", "expense".equalsIgnoreCase(currentType));
        JRadioButton incomeRadio  = new JRadioButton("Income",  "income".equalsIgnoreCase(currentType));
        ButtonGroup group = new ButtonGroup();
        group.add(expenseRadio);
        group.add(incomeRadio);
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(expenseRadio);
        radioPanel.add(incomeRadio);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        saveBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FEE394;" +
                "foreground:#3D3828;" +
                "borderWidth:0;" +
                "focusWidth:0");

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:#FFFFFF;" +
                "foreground:#3D3828;" +
                "borderWidth:1;" +
                "borderColor:#3D3828;" +
                "focusWidth:0");

        saveBtn.addActionListener(e -> {
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a category name!");
                return;
            }

            if (newName.length() < 2 || newName.length() > 100) {
                JOptionPane.showMessageDialog(dialog, "Category name must be between 2 and 100 characters!");
                return;
            }
            String newType = expenseRadio.isSelected() ? "expense" : "income";

            boolean success = categoryService.updateCategory(categoryId, newName, newType);

            if (success) {
                JOptionPane.showMessageDialog(dialog, "Category updated successfully!");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update category!");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonsPanel.add(saveBtn);
        buttonsPanel.add(cancelBtn);

        dialog.add(titleLabel, "wrap");
        dialog.add(nameLabel, "wrap");
        dialog.add(nameField, "growx, wrap");
        dialog.add(typeLabel, "wrap");
        dialog.add(radioPanel, "wrap");
        dialog.add(buttonsPanel, "growx");

        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteCategory(int categoryId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this category?\nAll transactions with this category will remain but show as 'Unknown'.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = categoryService.deleteCategory(categoryId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Category deleted successfully!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete category!");
            }
        }
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
                    String currentName = (String) tableModel.getValueAt(currentRow, 0);
                    Category category = allCategories.stream()
                            .filter(c -> c.getCategoryName().equals(currentName))
                            .findFirst()
                            .orElse(null);

                    if (category != null) {
                        showEditCategoryDialog(category.getCategoryId(), currentName, category.getType());
                    } else {
                        JOptionPane.showMessageDialog(panel, "Category not found!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            deleteBtn.addActionListener(e -> {
                stopCellEditing();
                try {
                    String currentName = (String) tableModel.getValueAt(currentRow, 0);
                    Category category = allCategories.stream()
                            .filter(c -> c.getCategoryName().equals(currentName))
                            .findFirst()
                            .orElse(null);

                    if (category != null) {
                        deleteCategory(category.getCategoryId());
                    } else {
                        JOptionPane.showMessageDialog(panel, "Category not found!");
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
}