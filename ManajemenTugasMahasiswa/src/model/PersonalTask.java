package model;

import model.abstractes.TaskBase;
import model.enums.Status;
import database.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class PersonalTask extends TaskBase {
    private String category;
    private int userId;
    
    public PersonalTask(String title, String description, LocalDate deadline, String category, int userId) {
        super(title, description, deadline);
        this.category = category;
        this.userId = userId;
    }
    
    public PersonalTask(int taskId, String title, String description, LocalDate deadline, Status status, String category, int userId) {
        super(taskId, title, description, deadline, status);
        this.category = category;
        this.userId = userId;
    }
    
    @Override
    public boolean updateStatus(Status newStatus) {
        this.status = newStatus;
        String sql = "UPDATE PersonalTasks SET status = ? WHERE personal_task_id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, newStatus.name());
            pstmt.setInt(2, taskId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean saveToDB() {
        String sql = "INSERT INTO PersonalTasks (title, description, category, deadline, status, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, category);
            pstmt.setString(4, deadline.toString());
            pstmt.setString(5, status.name());
            pstmt.setInt(6, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Getters and Setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}