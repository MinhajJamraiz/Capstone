package task_manager;

import robot_manager.LogisticsRobot;


public abstract class Task {
    protected final String taskId;
    protected final TaskType type;
    protected TaskStatus status;
    protected final String parcelId;

    public Task(String taskId, TaskType type, String parcelId) {
        this.taskId = taskId;
        this.type = type;
        this.parcelId = parcelId;
        this.status = TaskStatus.PENDING;
    }

    public String getTaskId() { return taskId; }
    public TaskType getType() { return type; }
    public TaskStatus getStatus() { return status; }
    public String getParcelId() { return parcelId; }

    public abstract void execute(LogisticsRobot robot);

    public void markComplete() { this.status = TaskStatus.COMPLETED; }
    public void markInProgress() { this.status = TaskStatus.IN_PROGRESS; }
    public void markFailed() { this.status = TaskStatus.FAILED; }
}
