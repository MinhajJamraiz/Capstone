package task_manager;


public interface ITaskAssignable {
    void assignTask(Task task);
    void updateTaskStatus(String taskId, TaskStatus status);
}
