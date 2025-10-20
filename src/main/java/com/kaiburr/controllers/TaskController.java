package com.kaiburr.controllers;

import com.kaiburr.models.Task;
import com.kaiburr.repositories.TaskRepository;
import com.kaiburr.services.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskRepository taskRepository;

    public TaskController(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    // GET /tasks (all) and GET /tasks?id={id} (single) [cite: 65, 66]
    @GetMapping
    public ResponseEntity<?> getTasks(@RequestParam(required = false) String id) {
        if (id != null) {
            return taskRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build()); // 404 if not found [cite: 66]
        }
        return ResponseEntity.ok(taskRepository.findAll()); // Return all tasks [cite: 65]
    }

    // GET /tasks/search?name={name} (find by name) [cite: 74]
    @GetMapping("/search")
    public ResponseEntity<List<Task>> findTasksByName(@RequestParam String name) {
        List<Task> tasks = taskRepository.findByNameContaining(name);
        if (tasks.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 if nothing is found [cite: 76]
        }
        return ResponseEntity.ok(tasks);
    }

    // PUT /tasks (Create/Update) [cite: 67]
    @PutMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        try {
            Task savedTask = taskService.saveTask(task);
            return ResponseEntity.ok(savedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /tasks/{id} [cite: 73]
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // PUT /tasks/{id}/execute (Execute command) [cite: 77]
    @PutMapping("/{id}/execute")
    public ResponseEntity<Task> executeTask(@PathVariable String id) {
        try {
            // Task 1: Calls local execution (Task 2 will replace this logic)
            Task updatedTask = taskService.executeTaskLocally(id); 
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}