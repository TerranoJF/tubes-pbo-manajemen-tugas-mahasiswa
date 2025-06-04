package view;

import controller.UserController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton createButton;
    private JLabel signInLabel;
    
    // Colors - sama dengan LoginPanel
    private final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private final Color CARD_COLOR = new Color(220, 180, 220);
    private final Color BUTTON_COLOR = new Color(150, 50, 200);
    private final Color BUTTON_HOVER_COLOR = new Color(130, 40, 180);
    private final Color TEXT_COLOR = new Color(80, 80, 80);
    private final Color FIELD_COLOR = new Color(200, 160, 200);
    
    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        setupLayout();
        setupEventHandlers();
        setupStyling();
    }
    
    private void initComponents() {
        setBackground(BACKGROUND_COLOR);
        
        // Initialize components
        usernameField = createStyledTextField("Username");
        passwordField = createStyledPasswordField("Password");
        confirmPasswordField = createStyledPasswordField("Confirm Password");
        createButton = createStyledButton("CREATE");
        signInLabel = createStyledLabel("Already have account? Sign In");
    }
    
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw text
                super.paintComponent(g);
                
                // Draw underline
                g2.setColor(BUTTON_COLOR);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);
                
                g2.dispose();
            }
        };
        
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(10, 5, 10, 5));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setBackground(CARD_COLOR);
        
        // Add placeholder functionality
        addPlaceholderBehavior(field, placeholder);
        
        return field;
    }
    
    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw text
                super.paintComponent(g);
                
                // Draw underline
                g2.setColor(BUTTON_COLOR);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);
                
                g2.dispose();
            }
        };
        
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(10, 5, 10, 5));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setBackground(CARD_COLOR);
        field.setEchoChar((char) 0); // Show placeholder initially
        
        // Add placeholder functionality
        addPlaceholderBehavior(field, placeholder);
        
        return field;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded background
                if (getModel().isPressed()) {
                    g2.setColor(BUTTON_HOVER_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(BUTTON_HOVER_COLOR);
                } else {
                    g2.setColor(BUTTON_COLOR);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
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
        
        button.setPreferredSize(new Dimension(200, 45));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(TEXT_COLOR);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return label;
    }
    
    private void addPlaceholderBehavior(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(TEXT_COLOR.brighter());
        
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('•');
                    }
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_COLOR.brighter());
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel with background
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Card panel
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded background
                g2.setColor(CARD_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                g2.dispose();
            }
        };
        
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(350, 450)); // Sedikit lebih tinggi untuk 3 fields
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add components to card
        gbc.gridx = 0; gbc.gridy = 0;
        cardPanel.add(usernameField, gbc);
        
        gbc.gridy = 1;
        cardPanel.add(passwordField, gbc);
        
        gbc.gridy = 2;
        cardPanel.add(confirmPasswordField, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(25, 0, 15, 0);
        cardPanel.add(createButton, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 0, 0, 0);
        cardPanel.add(signInLabel, gbc);
        
        // Add card to main panel
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainPanel.add(cardPanel, mainGbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupStyling() {
        // Add hover effect to sign in label
        signInLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signInLabel.setForeground(BUTTON_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                signInLabel.setForeground(TEXT_COLOR);
            }
        });
    }
    
    private void setupEventHandlers() {
        createButton.addActionListener(this::handleRegister);
        signInLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleBackToLogin();
            }
        });
        
        // Enter key navigation
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> confirmPasswordField.requestFocus());
        confirmPasswordField.addActionListener(this::handleRegister);
    }
    
    private void handleRegister(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validation
        if (username.equals("Username") || username.isEmpty()) {
            showStyledMessage("Username tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return;
        }
        
        if (password.equals("Password") || password.isEmpty()) {
            showStyledMessage("Password tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return;
        }
        
        if (confirmPassword.equals("Confirm Password") || confirmPassword.isEmpty()) {
            showStyledMessage("Konfirmasi password tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showStyledMessage("Password tidak cocok!", "Error", JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.requestFocus();
            return;
        }
        
        if (password.length() < 6) {
            showStyledMessage("Password minimal 6 karakter!", "Error", JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return;
        }
        
        if (username.length() < 3) {
            showStyledMessage("Username minimal 3 karakter!", "Error", JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return;
        }
        
        // Show loading effect
        createButton.setText("Creating...");
        createButton.setEnabled(false);
        
        // Simulate loading delay
        Timer timer = new Timer(500, event -> {
            boolean success = UserController.registerUser(username, password);
            if (success) {
                showStyledMessage("Registrasi berhasil!\nSilakan login dengan akun baru Anda.", "Success", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showLoginPanel();
            } else {
                showStyledMessage("Username sudah digunakan!\nSilakan pilih username lain.", "Error", JOptionPane.ERROR_MESSAGE);
                createButton.setText("CREATE");
                createButton.setEnabled(true);
                usernameField.requestFocus();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void handleBackToLogin() {
        mainFrame.showLoginPanel();
    }
    
    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    public void clearForm() {
        usernameField.setText("Username");
        usernameField.setForeground(TEXT_COLOR.brighter());
        
        passwordField.setText("Password");
        passwordField.setForeground(TEXT_COLOR.brighter());
        passwordField.setEchoChar((char) 0);
        
        confirmPasswordField.setText("Confirm Password");
        confirmPasswordField.setForeground(TEXT_COLOR.brighter());
        confirmPasswordField.setEchoChar((char) 0);
        
        createButton.setText("CREATE");
        createButton.setEnabled(true);
        
        usernameField.requestFocus();
    }
}