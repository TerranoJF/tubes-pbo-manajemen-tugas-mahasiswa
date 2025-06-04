import database.DatabaseManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        try {
            // Test koneksi database
            Connection conn = DatabaseManager.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connection successful!");
                
                // Test create table
                Statement stmt = conn.createStatement();
                
                // Test query untuk melihat tabel yang sudah dibuat
                ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table'"
                );
                
                System.out.println("\n📋 Tables created:");
                while (rs.next()) {
                    System.out.println("- " + rs.getString("name"));
                }
                
                rs.close();
                stmt.close();
                
                System.out.println("\n✅ Database setup complete!");
                
            } else {
                System.out.println("❌ Database connection failed!");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeConnection();
        }
    }
}