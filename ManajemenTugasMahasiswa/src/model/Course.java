package model;

import model.interfaces.Savable;
import database.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Course implements Savable {
    private int courseId;
    private String courseName;
    private int userId;
    
    public Course(String courseName, int userId) {
        this.courseName = courseName;
        this.userId = userId;
    }
    
    public Course(int courseId, String courseName, int userId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.userId = userId;
    }
    
    @Override
    public boolean saveToDB() {
        String sql = "INSERT INTO Courses (course_name, user_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, courseName);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Getters and Setters
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    @Override
    public String toString() {
        return courseName;
    }
}