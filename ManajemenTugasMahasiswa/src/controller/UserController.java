package controller;

import model.User;
import database.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    
    public static User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM Users WHERE name = ? AND password = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean registerUser(String username, String password) {
        // Check if username already exists
        if (isUsernameExists(username)) {
            return false;
        }
        
        User user = new User(username, password);
        return user.saveToDB();
    }
    
    private static boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE name = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Assume exists to be safe
        }
    }
}