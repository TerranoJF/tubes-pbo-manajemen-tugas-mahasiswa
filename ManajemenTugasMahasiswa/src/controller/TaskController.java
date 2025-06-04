package controller;

import model.AcademicTask;
import model.PersonalTask;
import model.abstractes.TaskBase;
import model.enums.Status;
import database.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskController {
    
    public static List<AcademicTask> getAcademicTasksByUserId(int userId) {
        List<AcademicTask> tasks = new ArrayList<>();
        String sql = """
            SELECT at.*, c.course_name 
            FROM AcademicTasks at 
            JOIN Courses c ON at.course_id = c.course_id 
            WHERE c.user_id = ? 
            ORDER BY at.deadline ASC
        """;
        
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                AcademicTask task = new AcademicTask(
                    rs.getInt("task_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    LocalDate.parse(rs.getString("deadline")),
                    Status.fromString(rs.getString("status")), // Gunakan method konversi yang aman
                    rs.getInt("course_id")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
    
    public static List<PersonalTask> getPersonalTasksByUserId(int userId) {
        List<PersonalTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM PersonalTasks WHERE user_id = ? ORDER BY deadline ASC";
        
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PersonalTask task = new PersonalTask(
                    rs.getInt("personal_task_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    LocalDate.parse(rs.getString("deadline")),
                    Status.fromString(rs.getString("status")), // Gunakan method konversi yang aman
                    rs.getString("category"),
                    rs.getInt("user_id")
                );
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
    
    public static List<TaskBase> getAllTasksByUserId(int userId) {
        List<TaskBase> allTasks = new ArrayList<>();
        allTasks.addAll(getAcademicTasksByUserId(userId));
        allTasks.addAll(getPersonalTasksByUserId(userId));
        
        // Sort by deadline
        allTasks.sort((t1, t2) -> t1.getDeadline().compareTo(t2.getDeadline()));
        return allTasks;
    }
    
    // Method untuk menambah Academic Task
    public static boolean addAcademicTask(AcademicTask task) {
        String sql = "INSERT INTO AcademicTasks (title, description, deadline, status, course_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getDeadline().toString());
            pstmt.setString(4, task.getStatus().toDatabaseString()); // Gunakan method untuk database
            pstmt.setInt(5, task.getCourseId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method untuk menambah Personal Task
    public static boolean addPersonalTask(PersonalTask task) {
        String sql = "INSERT INTO PersonalTasks (title, description, deadline, status, category, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getDeadline().toString());
            pstmt.setString(4, task.getStatus().toDatabaseString()); // Gunakan method untuk database
            pstmt.setString(5, task.getCategory());
            pstmt.setInt(6, task.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method untuk update Academic Task
    public static boolean updateAcademicTask(AcademicTask task) {
        String sql = "UPDATE AcademicTasks SET title = ?, description = ?, deadline = ?, status = ?, course_id = ? WHERE task_id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getDeadline().toString());
            pstmt.setString(4, task.getStatus().toDatabaseString()); // Gunakan method untuk database
            pstmt.setInt(5, task.getCourseId());
            pstmt.setInt(6, task.getTaskId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method untuk update Personal Task
    public static boolean updatePersonalTask(PersonalTask task) {
        String sql = "UPDATE PersonalTasks SET title = ?, description = ?, deadline = ?, status = ?, category = ? WHERE personal_task_id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getDeadline().toString());
            pstmt.setString(4, task.getStatus().toDatabaseString()); // Gunakan method untuk database
            pstmt.setString(5, task.getCategory());
            pstmt.setInt(6, task.getTaskId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method untuk update status saja (lebih efisien untuk popup detail)
    public static boolean updateTaskStatus(TaskBase task, Status newStatus) {
        if (task instanceof AcademicTask) {
            String sql = "UPDATE AcademicTasks SET status = ? WHERE task_id = ?";
            try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, newStatus.toDatabaseString()); // Gunakan method untuk database
                pstmt.setInt(2, ((AcademicTask) task).getTaskId());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else if (task instanceof PersonalTask) {
            String sql = "UPDATE PersonalTasks SET status = ? WHERE personal_task_id = ?";
            try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, newStatus.toDatabaseString()); // Gunakan method untuk database
                pstmt.setInt(2, ((PersonalTask) task).getTaskId());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    public static boolean deleteAcademicTask(int taskId) {
        String sql = "DELETE FROM AcademicTasks WHERE task_id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deletePersonalTask(int taskId) {
        String sql = "DELETE FROM PersonalTasks WHERE personal_task_id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<TaskBase> getUpcomingDeadlines(int userId, int days) {
        List<TaskBase> upcomingTasks = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        
        for (TaskBase task : getAllTasksByUserId(userId)) {
            if (task.getDeadline().isAfter(today) && 
                task.getDeadline().isBefore(futureDate.plusDays(1)) &&
                task.getStatus() != Status.SELESAI) {
                upcomingTasks.add(task);
            }
        }
        return upcomingTasks;
    }
    
    public static String getCourseName(int courseId) {
        String sql = "SELECT course_name FROM Courses WHERE course_id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("course_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Course";
    }
    
    // Method untuk mendapatkan task berdasarkan ID
    public static AcademicTask getAcademicTaskById(int taskId) {
        String sql = "SELECT * FROM AcademicTasks WHERE task_id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new AcademicTask(
                    rs.getInt("task_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    LocalDate.parse(rs.getString("deadline")),
                    Status.fromString(rs.getString("status")), // Gunakan method konversi yang aman
                    rs.getInt("course_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static PersonalTask getPersonalTaskById(int taskId) {
        String sql = "SELECT * FROM PersonalTasks WHERE personal_task_id = ?";
        try (PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new PersonalTask(
                    rs.getInt("personal_task_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    LocalDate.parse(rs.getString("deadline")),
                    Status.fromString(rs.getString("status")), // Gunakan method konversi yang aman
                    rs.getString("category"),
                    rs.getInt("user_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}