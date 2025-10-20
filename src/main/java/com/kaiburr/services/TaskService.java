package com.kaiburr.services;

import com.kaiburr.models.Task;
import com.kaiburr.models.TaskExecution;
import com.kaiburr.repositories.TaskRepository;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Task 1: Basic validation and save
    public Task saveTask(Task task) {
        if (isCommandMalicious(task.getCommand())) { // Command validation [cite: 68]
            throw new IllegalArgumentException("Command contains unsafe code.");
        }
        return taskRepository.save(task);
    }

    // Task 1: Implement local shell command execution (to be replaced in Task 2)
    public Task executeTaskLocally(String taskId) throws Exception {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found."));

        TaskExecution execution = new TaskExecution();
        execution.setStartTime(new Date()); // Record start time [cite: 40]

        Process process = Runtime.getRuntime().exec(task.getCommand());
        int exitCode = process.waitFor();

        // Capture output [cite: 41]
        String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines().reduce((a, b) -> a + "\n" + b).orElse("");
        
        execution.setEndTime(new Date()); // Record end time [cite: 40]
        execution.setOutput(output + "\nExit Code: " + exitCode);
        
        task.getTaskExecutions().add(execution); // Store TaskExecution [cite: 78]
        return taskRepository.save(task);
    }
    
    // Simple command validation (Needs to be robust) [cite: 68]
    private boolean isCommandMalicious(String command) {
        return command.contains("rm ") || command.contains("mv ") || command.contains(";") || command.contains(">>");
    }

    // ... other service methods for GET, DELETE, findByName ...
}