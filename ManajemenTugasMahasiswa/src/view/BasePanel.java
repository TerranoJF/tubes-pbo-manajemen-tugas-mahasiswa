package view;

import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class BasePanel extends JPanel {
    protected MainFrame mainFrame;
    protected User currentUser;
    
    // Navigation buttons
    protected JButton homeButton; 
    protected JButton courseButton;
    protected JButton taskButton;
    protected JButton logoutButton;
    
    // Colors - konsisten dengan design
    protected final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    protected final Color CARD_COLOR = new Color(220, 180, 220);
    protected final Color HEADER_COLOR = new Color(60, 60, 60);
    protected final Color ACCENT_COLOR = new Color(150, 50, 200);
    protected final Color BUTTON_COLOR = new Color(150, 50, 200);
    protected final Color TEXT_COLOR = new Color(80, 80, 80);
    protected final Color NAV_COLOR = new Color(220, 180, 220);
    
    public BasePanel(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.currentUser = user;
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());
    }
    
    protected JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        navPanel.setBackground(NAV_COLOR);
        navPanel.setPreferredSize(new Dimension(0, 80));
        
        // Create navigation buttons
        homeButton = createNavButton("HOME", "Beranda"); 
        courseButton = createNavButton("BOOK", "Matkul");
        taskButton = createNavButton("TASK", "Tugas");
        logoutButton = createNavButton("LOGOUT", "Keluar");
        
        // Add action listeners
        homeButton.addActionListener(this::handleHome); 
        courseButton.addActionListener(this::handleCourse);
        taskButton.addActionListener(this::handleTask);
        logoutButton.addActionListener(this::handleLogout);
        
        navPanel.add(homeButton); 
        navPanel.add(courseButton);
        navPanel.add(taskButton);
        navPanel.add(logoutButton);
        
        return navPanel;
    }
    
    protected JButton createNavButton(String iconText, String text) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                if (getModel().isPressed()) {
                    g2.setColor(NAV_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(NAV_COLOR.brighter());
                } else {
                    g2.setColor(NAV_COLOR);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw icon
                g2.setColor(TEXT_COLOR);
                drawCustomIcon(g2, iconText, getWidth(), getHeight());
                
                // Draw text
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = getHeight() - 5;
                g2.drawString(text, textX, textY);
                
                g2.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(70, 50));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
     
    protected void drawCustomIcon(Graphics2D g2, String iconType, int width, int height) {
        int centerX = width / 2;
        int centerY = height / 2 - 8;
        
        g2.setStroke(new BasicStroke(2));
        
        switch (iconType) {
            case "HOME":
                int[] xPoints = {centerX - 8, centerX, centerX + 8};
                int[] yPoints = {centerY + 2, centerY - 6, centerY + 2};
                g2.drawPolygon(xPoints, yPoints, 3);
                g2.drawRect(centerX - 5, centerY + 2, 10, 6);
                g2.drawRect(centerX - 2, centerY + 5, 4, 3);
                break;
                
            case "BOOK":
                g2.drawRect(centerX - 6, centerY - 4, 12, 8);
                g2.drawLine(centerX - 6, centerY - 1, centerX + 6, centerY - 1);
                g2.drawLine(centerX - 6, centerY + 2, centerX + 6, centerY + 2);
                g2.drawLine(centerX, centerY - 4, centerX, centerY + 4);
                break;
                
            case "TASK":
                g2.drawRect(centerX - 6, centerY - 6, 12, 12);
                g2.drawLine(centerX - 3, centerY - 2, centerX - 1, centerY);
                g2.drawLine(centerX - 1, centerY, centerX + 4, centerY - 4);
                g2.drawLine(centerX - 3, centerY + 2, centerX + 3, centerY + 2);
                break;
                
            case "LOGOUT":
                g2.drawRect(centerX - 6, centerY - 4, 8, 8);
                g2.drawLine(centerX + 2, centerY, centerX + 6, centerY);
                g2.drawLine(centerX + 4, centerY - 2, centerX + 6, centerY);
                g2.drawLine(centerX + 4, centerY + 2, centerX + 6, centerY);
                break;
        }
    }
    
    protected JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 50));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    // Navigation event handlers
    protected void handleHome(ActionEvent e) {
        mainFrame.showDashboardPanel(currentUser);
    }
    
    protected void handleCourse(ActionEvent e) {
        mainFrame.showCourseManagementPanel();
    }
    
    protected void handleTask(ActionEvent e) {
        mainFrame.showTaskManagementPanel();
    }
    
    protected void handleLogout(ActionEvent e) {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin keluar dari aplikasi?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            mainFrame.logout();
        }
    }
    
    // Abstract methods yang harus diimplementasi oleh subclass
    protected abstract void handleAdd(ActionEvent e);
    public abstract void refreshData();
    
    // Utility methods
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
}