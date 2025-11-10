package task_manager;

//import application.DHLWareHouseMainWithLogs.Task;
//import application.DHLWareHouseMainWithLogs.TaskStatus;

public interface ITaskAssignable {
    void assignTask(Task task);
    void updateTaskStatus(String taskId, TaskStatus status);
}
