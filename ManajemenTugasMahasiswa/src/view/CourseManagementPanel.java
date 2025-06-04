package view;

import controller.CourseController;
import model.User;
import model.Course;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CourseManagementPanel extends BasePanel {
    private JPanel coursesPanel;
    private JTextField courseNameField;
    private JButton addButton;
    private JButton deleteButton;
    private Course selectedCourse;
    
    // Additional colors specific to course management
    private final Color COURSE_ITEM_COLOR = new Color(200, 200, 200);
    private final Color COURSE_SELECTED_COLOR = new Color(180, 180, 180);
    
    public CourseManagementPanel(MainFrame mainFrame, User user) {
        super(mainFrame, user);
        this.selectedCourse = null;
        initCourseComponents();
        setupCourseLayout();
        loadCourses();
    }
    
    private void initCourseComponents() {
        // Input field
        courseNameField = new JTextField();
        courseNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        courseNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Action buttons
        addButton = createActionButton("Tambah", BUTTON_COLOR);
        deleteButton = createActionButton("Hapus", BUTTON_COLOR);
        
        // Courses panel
        coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        coursesPanel.setBackground(BACKGROUND_COLOR);
        coursesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }
    
    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
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
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(text, textX, textY);
                
                g2.dispose();
            }
        };
        
        button.setText(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(100, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void setupCourseLayout() {
        // Header panel
        JPanel headerPanel = createHeaderPanel("Manajemen Mata Kuliah");
        
        // Main content panel
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
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Form section
        JPanel formPanel = createFormPanel();
        
        // Courses list section
        JPanel coursesListPanel = createCoursesListPanel();
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(coursesListPanel, BorderLayout.CENTER);
        
        // Bottom navigation panel
        JPanel navPanel = createNavigationPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(navPanel, BorderLayout.SOUTH);
        
        // Setup event handlers
        setupCourseEventHandlers();
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        formPanel.setOpaque(false);
        
        JLabel label = new JLabel("Tambah Matkul :");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        
        courseNameField.setPreferredSize(new Dimension(200, 35));
        
        formPanel.add(label);
        formPanel.add(courseNameField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setOpaque(false);
        containerPanel.add(formPanel, BorderLayout.NORTH);
        containerPanel.add(buttonPanel, BorderLayout.CENTER);
        
        return containerPanel;
    }
    
    private JPanel createCoursesListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel headerLabel = new JLabel("Mata Kuliah");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerLabel.setForeground(TEXT_COLOR);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setOpaque(true);
        headerLabel.setBackground(COURSE_ITEM_COLOR);
        headerLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JScrollPane scrollPane = new JScrollPane(coursesPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        listPanel.add(headerLabel, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        
        return listPanel;
    }
    
    private void loadCourses() {
        coursesPanel.removeAll();
        List<Course> courses = CourseController.getCoursesByUserId(currentUser.getUserId());
        
        if (courses.isEmpty()) {
            JLabel noCoursesLabel = new JLabel("Belum ada mata kuliah");
            noCoursesLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noCoursesLabel.setForeground(TEXT_COLOR.brighter());
            noCoursesLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noCoursesLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
            coursesPanel.add(noCoursesLabel);
        } else {
            for (Course course : courses) {
                coursesPanel.add(createCourseItem(course));
                coursesPanel.add(Box.createVerticalStrut(2));
            }
        }
        
        coursesPanel.revalidate();
        coursesPanel.repaint();
    }
    
    private JPanel createCourseItem(Course course) {
        JPanel itemPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = (selectedCourse != null && selectedCourse.getCourseId() == course.getCourseId()) 
                    ? COURSE_SELECTED_COLOR : COURSE_ITEM_COLOR;
                
                if (getMousePosition() != null) {
                    bgColor = bgColor.brighter();
                }
                
                g2.setColor(bgColor);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        
        itemPanel.setOpaque(false);
        itemPanel.setPreferredSize(new Dimension(0, 40));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        itemPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel nameLabel = new JLabel(course.getCourseName());
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        nameLabel.setForeground(TEXT_COLOR);
        
        itemPanel.add(nameLabel, BorderLayout.WEST);
        
        // Add click handler
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedCourse = course;
                coursesPanel.repaint(); // Refresh to show selection
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                itemPanel.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                itemPanel.repaint();
            }
        });
        
        return itemPanel;
    }
    
    private void setupCourseEventHandlers() {
        addButton.addActionListener(this::handleAddCourse);
        deleteButton.addActionListener(this::handleDeleteCourse);
        
        // Enter key untuk add course
        courseNameField.addActionListener(this::handleAddCourse);
    }
    
    private void handleAddCourse(ActionEvent e) {
        String courseName = courseNameField.getText().trim();
        
        if (courseName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama mata kuliah tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (CourseController.isCourseNameExists(courseName, currentUser.getUserId())) {
            JOptionPane.showMessageDialog(this, "Mata kuliah dengan nama tersebut sudah ada!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Course course = new Course(courseName, currentUser.getUserId());
        boolean success = course.saveToDB();
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Mata kuliah berhasil ditambahkan!", "Success", JOptionPane.INFORMATION_MESSAGE);
            courseNameField.setText("");
            selectedCourse = null;
            loadCourses();
            
            // Refresh all panels in main frame
            mainFrame.refreshAllPanels();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan mata kuliah!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleDeleteCourse(ActionEvent e) {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Pilih mata kuliah yang akan dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(
            this, 
            "Yakin ingin menghapus mata kuliah '" + selectedCourse.getCourseName() + "'?\nSemua tugas terkait juga akan dihapus!", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            boolean success = CourseController.deleteCourse(selectedCourse.getCourseId());
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Mata kuliah berhasil dihapus!", "Success", JOptionPane.INFORMATION_MESSAGE);
                selectedCourse = null;
                loadCourses();
                
                // Refresh all panels in main frame
                mainFrame.refreshAllPanels();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus mata kuliah!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Override BasePanel abstract methods
    @Override
    protected void handleAdd(ActionEvent e) {
        // Focus on course name field for quick add
        courseNameField.requestFocus();
    }
    
    @Override
    public void refreshData() {
        selectedCourse = null;
        loadCourses();
    }
    
    // Override navigation to highlight current page
    @Override
    protected void handleCourse(ActionEvent e) {
        // Already on course page, just refresh
        refreshData();
    }
}