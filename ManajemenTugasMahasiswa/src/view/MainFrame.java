package view;

import model.User;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User currentUser;
    
    // Panel references
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private DashboardPanel dashboardPanel;
    private CourseManagementPanel courseManagementPanel;
    private TaskManagementPanel taskManagementPanel;
    
    // Panel names for CardLayout
    public static final String LOGIN_PANEL = "LOGIN";
    public static final String REGISTER_PANEL = "REGISTER";
    public static final String DASHBOARD_PANEL = "DASHBOARD";
    public static final String COURSE_PANEL = "COURSE";
    public static final String TASK_PANEL = "TASK";
    
    // Colors - konsisten dengan design
    private final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    
    public MainFrame() {
        initComponents();
        setupLayout();
        showLoginPanel();
    }
    
    private void initComponents() {
        setTitle("Student Task Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Initialize CardLayout and main panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Initialize login and register panels
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        
        // Add panels to CardLayout
        mainPanel.add(loginPanel, LOGIN_PANEL);
        mainPanel.add(registerPanel, REGISTER_PANEL);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }
    
    // Navigation methods
    public void showLoginPanel() {
        loginPanel.clearFields();
        cardLayout.show(mainPanel, LOGIN_PANEL);
    }
    
    public void showRegisterPanel() {
        registerPanel.clearForm();
        cardLayout.show(mainPanel, REGISTER_PANEL);
    }
    
    public void showDashboardPanel(User user) {
        this.currentUser = user;
        
        // Initialize dashboard panel if not exists
        if (dashboardPanel == null) {
            dashboardPanel = new DashboardPanel(this, user);
            mainPanel.add(dashboardPanel, DASHBOARD_PANEL);
        } else {
            dashboardPanel.setCurrentUser(user);
            dashboardPanel.refreshData();
        }
        
        cardLayout.show(mainPanel, DASHBOARD_PANEL);
    }
    
    public void showCourseManagementPanel() {
        if (currentUser == null) return;
        
        // Initialize course panel if not exists
        if (courseManagementPanel == null) {
            courseManagementPanel = new CourseManagementPanel(this, currentUser);
            mainPanel.add(courseManagementPanel, COURSE_PANEL);
        } else {
            courseManagementPanel.refreshData();
        }
        
        cardLayout.show(mainPanel, COURSE_PANEL);
    }
    
    public void showTaskManagementPanel() {
        if (currentUser == null) return;
        
        // Initialize task panel if not exists
        if (taskManagementPanel == null) {
            taskManagementPanel = new TaskManagementPanel(this, currentUser);
            mainPanel.add(taskManagementPanel, TASK_PANEL);
        } else {
            taskManagementPanel.refreshData();
        }
        
        cardLayout.show(mainPanel, TASK_PANEL);
    }
    
    // Utility methods
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void logout() {
        this.currentUser = null;
        
        // Clear panels to free memory
        if (dashboardPanel != null) {
            mainPanel.remove(dashboardPanel);
            dashboardPanel = null;
        }
        if (courseManagementPanel != null) {
            mainPanel.remove(courseManagementPanel);
            courseManagementPanel = null;
        }
        if (taskManagementPanel != null) {
            mainPanel.remove(taskManagementPanel);
            taskManagementPanel = null;
        }
        
        // Reset login panel
        showLoginPanel();
    }
    
    // Method untuk refresh data di semua panel
    public void refreshAllPanels() {
        if (dashboardPanel != null) {
            dashboardPanel.refreshData();
        }
        if (courseManagementPanel != null) {
            courseManagementPanel.refreshData();
        }
        if (taskManagementPanel != null) {
            taskManagementPanel.refreshData();
        }
    }
}