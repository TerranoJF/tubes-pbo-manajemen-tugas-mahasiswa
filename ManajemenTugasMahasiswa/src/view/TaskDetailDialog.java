package view;

import controller.TaskController;
import model.abstractes.TaskBase;
import model.AcademicTask;
import model.PersonalTask;
import model.enums.Status;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;

public class TaskDetailDialog extends JDialog {
    private TaskBase task;
    private DashboardPanel parentPanel;
    private MainFrame mainFrame;
    private JComboBox<Status> statusComboBox;
    private JButton saveButton;
    private JButton closeButton;
    
    // Colors - konsisten dengan design
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private final Color CARD_COLOR = new Color(220, 180, 220);
    private final Color BUTTON_COLOR = new Color(150, 50, 200);
    private final Color TEXT_COLOR = new Color(80, 80, 80);
    
    public TaskDetailDialog(MainFrame mainFrame, TaskBase task, DashboardPanel parentPanel) {
        super(mainFrame, "Detail Tugas", true);
        this.mainFrame = mainFrame;
        this.task = task;
        this.parentPanel = parentPanel;
        
        initComponents();
        setupLayout();
        setupEventHandlers();
        loadTaskData();
    }
    
    private void initComponents() {
        setSize(400, 350);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        statusComboBox = new JComboBox<>(Status.values());
        statusComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        
        saveButton = createStyledButton("Simpan");
        closeButton = createStyledButton("Tutup");
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded background
                Color bgColor = text.equals("Simpan") ? BUTTON_COLOR : BUTTON_COLOR.brighter();
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw text
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(100, 35));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main content panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(CARD_COLOR);
                g2.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 20, 20);
                
                g2.dispose();
            }
        };
        
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Detail Tugas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Task title
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(createLabel("Judul:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(createValueLabel(task.getTitle()), gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(createLabel("Deskripsi:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(createValueLabel(task.getDescription()), gbc);
        
        // Deadline
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(createLabel("Deadline:"), gbc);
        gbc.gridx = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        mainPanel.add(createValueLabel(task.getDeadline().format(formatter)), gbc);
        
        // Course/Category
        gbc.gridx = 0; gbc.gridy = 4;
        if (task instanceof AcademicTask) {
            mainPanel.add(createLabel("Mata Kuliah:"), gbc);
            gbc.gridx = 1;
            String courseName = TaskController.getCourseName(((AcademicTask) task).getCourseId());
            mainPanel.add(createValueLabel(courseName), gbc);
        } else if (task instanceof PersonalTask) {
            mainPanel.add(createLabel("Kategori:"), gbc);
            gbc.gridx = 1;
            mainPanel.add(createValueLabel(((PersonalTask) task).getCategory()), gbc);
        }
        
        // Status
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(createLabel("Status:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(statusComboBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        return label;
    }
    
    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel("<html>" + text + "</html>");
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(TEXT_COLOR.brighter());
        return label;
    }
    
    private void loadTaskData() {
        statusComboBox.setSelectedItem(task.getStatus());
    }
    
    private void setupEventHandlers() {
        saveButton.addActionListener(this::handleSave);
        closeButton.addActionListener(this::handleClose);
        
        // ESC key to close
        getRootPane().registerKeyboardAction(
            this::handleClose,
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void handleSave(ActionEvent e) {
        Status newStatus = (Status) statusComboBox.getSelectedItem();
        
        if (newStatus != task.getStatus()) {
            boolean success = TaskController.updateTaskStatus(task, newStatus);
            
            if (success) {
                task.setStatus(newStatus);
                JOptionPane.showMessageDialog(this, "Status tugas berhasil diperbarui!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh parent panel
                if (parentPanel != null) {
                    parentPanel.refreshData();
                }
                
                // Refresh all panels in main frame
                mainFrame.refreshAllPanels();
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui status tugas!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            dispose();
        }
    }
    
    private void handleClose(ActionEvent e) {
        dispose();
    }
}