package model;

import model.abstractes.TaskBase;
import model.enums.Status;
import database.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class AcademicTask extends TaskBase {
    private int courseId;
    
    public AcademicTask(String title, String description, LocalDate deadline, int courseId) {
        super(title, description, deadline);
        this.courseId = courseId;
    }
    
    public AcademicTask(int taskId, String title, String description, LocalDate deadline, Status status, int courseId) {
        super(taskId, title, description, deadline, status);
        this.courseId = courseId;
    }
    
    @Override
    public boolean updateStatus(Status newStatus) {
        this.status = newStatus;
        String sql = "UPDATE AcademicTasks SET status = ? WHERE task_id = ?";
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
        String sql = "INSERT INTO AcademicTasks (title, description, deadline, status, course_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, deadline.toString());
            pstmt.setString(4, status.name());
            pstmt.setInt(5, courseId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Getters and Setters
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
}