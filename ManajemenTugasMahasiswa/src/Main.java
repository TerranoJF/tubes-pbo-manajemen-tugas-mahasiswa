import view.MainFrame;
import database.DatabaseManager;
import javax.swing.SwingUtilities;
//import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Initialize database connection
        DatabaseManager.getConnection();
        
//        // Set look and feel
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
//        } catch (Exception e) {
//            // Use default look and feel if system look and feel is not available
//        }
//        
        // Start application with MainFrame
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
        
        // Add shutdown hook to close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseManager.closeConnection();
        }));
    }
}