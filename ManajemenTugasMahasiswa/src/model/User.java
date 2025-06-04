package model;

import model.interfaces.Savable;
import database.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class User implements Savable {
    private int userId;
    private String name;
    private String password;
    
    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
    
    public User(int userId, String name, String password) {
        this.userId = userId;
        this.name = name;
        this.password = password;
    }
    
    @Override
    public boolean saveToDB() {
        String sql = "INSERT INTO Users (name, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, password);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}