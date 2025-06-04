package model.abstractes;

import model.interfaces.Savable;
import model.enums.Status;
import java.time.LocalDate;

public abstract class TaskBase implements Savable {
    protected int taskId;
    protected String title;
    protected String description;
    protected LocalDate deadline;
    protected Status status;
    
    public TaskBase(String title, String description, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = Status.BELUM_MULAI;
    }
    
    public TaskBase(int taskId, String title, String description, LocalDate deadline, Status status) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
    }
    
    // Abstract methods
    public abstract boolean updateStatus(Status newStatus);
    
    // Getters and Setters
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}