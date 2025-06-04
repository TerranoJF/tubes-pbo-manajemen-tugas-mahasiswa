package controller;

import model.Course;
import database.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseController {
    
    public static List<Course> getCoursesByUserId(int userId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Courses WHERE user_id = ? ORDER BY course_name";
        
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Course course = new Course(
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getInt("user_id")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    
    public static boolean deleteCourse(int courseId) {
        try {
            // First delete all academic tasks related to this course
            String deleteTasksSql = "DELETE FROM AcademicTasks WHERE course_id = ?";
            try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(deleteTasksSql)) {
                pstmt.setInt(1, courseId);
                pstmt.executeUpdate();
            }
            
            // Then delete the course
            String deleteCourseSql = "DELETE FROM Courses WHERE course_id = ?";
            try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(deleteCourseSql)) {
                pstmt.setInt(1, courseId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean isCourseNameExists(String courseName, int userId) {
        String sql = "SELECT COUNT(*) FROM Courses WHERE course_name = ? AND user_id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, courseName);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Assume exists to be safe
        }
    }
    
    public static Course getCourseById(int courseId) {
        String sql = "SELECT * FROM Courses WHERE course_id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Course(
                    rs.getInt("course_id"),
                    rs.getString("course_name"),
                    rs.getInt("user_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}