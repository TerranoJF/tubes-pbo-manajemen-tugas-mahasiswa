package view;

import controller.TaskController;
import controller.CourseController;
import model.User;
import model.Course;
import model.AcademicTask;
import model.PersonalTask;
import model.abstractes.TaskBase;
import model.enums.Status;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TaskManagementPanel extends BasePanel {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField deadlineField;
    private JComboBox<String> taskTypeCombo;
    private JComboBox<Course> courseCombo;
    private JTextField categoryField;
    private JButton saveButton;
    private JButton updateStatusButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JLabel dynamicLabel;
    private JPanel dynamicFieldPanel;
    
    // Konstanta untuk ukuran field
    private final Dimension FIELD_SIZE = new Dimension(200, 35);
    
    public TaskManagementPanel(MainFrame mainFrame, User user) {
        super(mainFrame, user);
        initTaskComponents();
        setupTaskLayout();
        loadCourses();
        loadAllTasks();
    }
    
    private void initTaskComponents() {
        // Form fields dengan ukuran yang konsisten
        titleField = createStyledTextField();
        titleField.setPreferredSize(FIELD_SIZE);
        
        descriptionArea = new JTextArea(2, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        deadlineField = createStyledTextField();
        deadlineField.setPreferredSize(FIELD_SIZE);
        deadlineField.setText(LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        taskTypeCombo = new JComboBox<>(new String[]{"Academic", "Personal"});
        taskTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        taskTypeCombo.setPreferredSize(FIELD_SIZE);
        
        // Course combo untuk Academic tasks
        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        courseCombo.setPreferredSize(FIELD_SIZE);
        
        // Category field untuk Personal tasks
        categoryField = createStyledTextField();
        categoryField.setPreferredSize(FIELD_SIZE);
        
        // Dynamic label yang berubah sesuai tipe task
        dynamicLabel = createLabel("Matakuliah :");
        
        // Panel dinamis untuk course/category
        dynamicFieldPanel = new JPanel(new CardLayout());
        dynamicFieldPanel.setOpaque(false);
        dynamicFieldPanel.setPreferredSize(FIELD_SIZE);
        
        // Panel untuk course (Academic)
        JPanel coursePanel = new JPanel(new BorderLayout());
        coursePanel.setOpaque(false);
        coursePanel.add(courseCombo, BorderLayout.CENTER);
        
        // Panel untuk category (Personal)
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setOpaque(false);
        categoryPanel.add(categoryField, BorderLayout.CENTER);
        
        dynamicFieldPanel.add(coursePanel, "ACADEMIC");
        dynamicFieldPanel.add(categoryPanel, "PERSONAL");
        
        // Action buttons dengan styling purple
        saveButton = createActionButton("Simpan", BUTTON_COLOR);
        updateStatusButton = createActionButton("Update Status", BUTTON_COLOR);
        deleteButton = createActionButton("Hapus", BUTTON_COLOR);
        clearButton = createActionButton("Clear", BUTTON_COLOR.brighter());
        
        // Initialize table
        initializeTable();
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(text, textX, textY);
                
                g2.dispose();
            }
        };
        
        button.setText(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(120, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void initializeTable() {
        String[] columnNames = {"ID", "Judul", "Deskripsi", "Deadline", "Mata Kuliah", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        taskTable = new JTable(tableModel);
        taskTable.setFont(new Font("Arial", Font.PLAIN, 12));
        taskTable.setRowHeight(25);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setGridColor(new Color(200, 200, 200));
        
        // Hide ID column
        taskTable.getColumnModel().getColumn(0).setMinWidth(0);
        taskTable.getColumnModel().getColumn(0).setMaxWidth(0);
        taskTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Judul
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Deskripsi
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Deadline
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Mata Kuliah
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        
        // Custom header renderer
        taskTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(CARD_COLOR);
                c.setForeground(TEXT_COLOR);
                c.setFont(new Font("Arial", Font.BOLD, 12));
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
    }
    
    private void setupTaskLayout() {
        // Header panel
        JPanel headerPanel = createHeaderPanel("Tambah Tugas");
        
        // Main content panel dengan background card
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(CARD_COLOR);
                g2.fillRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 20, 20);
                
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 20, 30));
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        // Gunakan BorderLayout untuk memastikan tabel terlihat
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setOpaque(false);
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Bottom navigation panel
        JPanel navPanel = createNavigationPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(navPanel, BorderLayout.SOUTH);
        
        // Setup event handlers
        setupTaskEventHandlers();
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Jenis Tugas dan Judul
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createLabel("Jenis Tugas :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(taskTypeCombo, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Judul :"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(titleField, gbc);
        
        // Row 2: Deadline dan Matakuliah/Kategori
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(createLabel("Deadline :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(deadlineField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(dynamicLabel, gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        formPanel.add(dynamicFieldPanel, gbc);
        
        // Row 3: Deskripsi (span across all columns)
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Deskripsi :"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.5;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(400, 60));
        formPanel.add(descScrollPane, gbc);
        
        // Row 4: Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private void loadCourses() {
        courseCombo.removeAllItems();
        List<Course> courses = CourseController.getCoursesByUserId(currentUser.getUserId());
        
        if (courses.isEmpty()) {
            courseCombo.addItem(new Course("Tidak ada mata kuliah", 0));
        } else {
            for (Course course : courses) {
                courseCombo.addItem(course);
            }
        }
    }
    
    private void loadAllTasks() {
        tableModel.setRowCount(0);
        List<TaskBase> allTasks = TaskController.getAllTasksByUserId(currentUser.getUserId());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (TaskBase task : allTasks) {
            String courseOrCategory = "";
            
            if (task instanceof AcademicTask) {
                courseOrCategory = TaskController.getCourseName(((AcademicTask) task).getCourseId());
            } else if (task instanceof PersonalTask) {
                courseOrCategory = ((PersonalTask) task).getCategory();
            }
            
            Object[] row = {
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline().format(formatter),
                courseOrCategory,
                task.getStatus().getDisplayName()
            };
            tableModel.addRow(row);
        }
    }
    
    private void setupTaskEventHandlers() {
        saveButton.addActionListener(this::handleSaveTask);
        updateStatusButton.addActionListener(this::handleUpdateStatus);
        deleteButton.addActionListener(this::handleDeleteTask);
        clearButton.addActionListener(this::handleClearForm);
        
        // Task type change handler
        taskTypeCombo.addActionListener(e -> {
            String selectedType = (String) taskTypeCombo.getSelectedItem();
            boolean isAcademic = "Academic".equals(selectedType);
            
            CardLayout cl = (CardLayout) dynamicFieldPanel.getLayout();
            if (isAcademic) {
                dynamicLabel.setText("Matakuliah :");
                cl.show(dynamicFieldPanel, "ACADEMIC");
            } else {
                dynamicLabel.setText("Kategori :");
                cl.show(dynamicFieldPanel, "PERSONAL");
            }
            
            revalidate();
            repaint();
        });
    }
    
    private void handleSaveTask(ActionEvent e) {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String deadlineStr = deadlineField.getText().trim();
        String taskType = (String) taskTypeCombo.getSelectedItem();
        
        if (title.isEmpty() || deadlineStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Judul dan deadline tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        LocalDate deadline;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            deadline = LocalDate.parse(deadlineStr, formatter);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid! Gunakan dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = false;
        
        if ("Academic".equals(taskType)) {
            Course selectedCourse = (Course) courseCombo.getSelectedItem();
            if (selectedCourse == null || selectedCourse.getCourseId() == 0) {
                JOptionPane.showMessageDialog(this, "Pilih mata kuliah terlebih dahulu!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            AcademicTask task = new AcademicTask(0, title, description, deadline, Status.BELUM_MULAI, selectedCourse.getCourseId());
            success = TaskController.addAcademicTask(task);
        } else {
            String category = categoryField.getText().trim();
            if (category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kategori tidak boleh kosong untuk Personal Task!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            PersonalTask task = new PersonalTask(0, title, description, deadline, Status.BELUM_MULAI, category, currentUser.getUserId());
            success = TaskController.addPersonalTask(task);
        }
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Tugas berhasil ditambahkan!", "Success", JOptionPane.INFORMATION_MESSAGE);
            handleClearForm(null);
            loadAllTasks();
            mainFrame.refreshAllPanels();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan tugas!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleUpdateStatus(ActionEvent e) {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih tugas yang akan diupdate!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] statusOptions = {"Belum Mulai", "Sedang Dikerjakan", "Selesai"};
        String selectedStatus = (String) JOptionPane.showInputDialog(
            this,
            "Pilih status baru:",
            "Update Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            statusOptions[0]
        );
        
        if (selectedStatus != null) {
            Status newStatus = Status.BELUM_MULAI;
            switch (selectedStatus) {
                case "Sedang Dikerjakan" -> newStatus = Status.SEDANG_DIKERJAKAN;
                case "Selesai" -> newStatus = Status.SELESAI;
            }
            
            int taskId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            // Get task from database to determine type
            AcademicTask academicTask = TaskController.getAcademicTaskById(taskId);
            PersonalTask personalTask = null;
            if (academicTask == null) {
                personalTask = TaskController.getPersonalTaskById(taskId);
            }
            
            boolean success = false;
            if (academicTask != null) {
                success = TaskController.updateTaskStatus(academicTask, newStatus);
            } else if (personalTask != null) {
                success = TaskController.updateTaskStatus(personalTask, newStatus);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Status berhasil diupdate!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllTasks();
                mainFrame.refreshAllPanels();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate status!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleDeleteTask(ActionEvent e) {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih tugas yang akan dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus tugas ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            int taskId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            // Try to delete as academic task first, then personal task
            boolean success = TaskController.deleteAcademicTask(taskId);
            if (!success) {
                success = TaskController.deletePersonalTask(taskId);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Tugas berhasil dihapus!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllTasks();
                mainFrame.refreshAllPanels();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus tugas!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleClearForm(ActionEvent e) {
        titleField.setText("");
        descriptionArea.setText("");
        deadlineField.setText(LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        taskTypeCombo.setSelectedIndex(0);
        categoryField.setText("");
        
        if (courseCombo.getItemCount() > 0) {
            courseCombo.setSelectedIndex(0);
        }
        
        // Reset ke Academic view
        CardLayout cl = (CardLayout) dynamicFieldPanel.getLayout();
        cl.show(dynamicFieldPanel, "ACADEMIC");
        dynamicLabel.setText("Matakuliah :");
        
        titleField.requestFocus();
    }
    
    // Override BasePanel abstract methods
    @Override
    protected void handleAdd(ActionEvent e) {
        titleField.requestFocus();
    }
    
    @Override
    public void refreshData() {
        loadCourses();
        loadAllTasks();
    }
    
    // Override navigation to highlight current page
    @Override
    protected void handleTask(ActionEvent e) {
        refreshData();
    }
}
