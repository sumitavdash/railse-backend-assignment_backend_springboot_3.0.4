package com.sumitav_railse_assignment.work_force_mgmt.controller;

import com.sumitav_railse_assignment.work_force_mgmt.dto.CommentRequest;
import com.sumitav_railse_assignment.work_force_mgmt.dto.CreateTaskRequest;
import com.sumitav_railse_assignment.work_force_mgmt.dto.TaskDto;
import com.sumitav_railse_assignment.work_force_mgmt.model.Comment;
import com.sumitav_railse_assignment.work_force_mgmt.model.Priority;
import com.sumitav_railse_assignment.work_force_mgmt.model.Staff;
import com.sumitav_railse_assignment.work_force_mgmt.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    //  Feature 0: Create a new task
    @PostMapping
    public TaskDto createTask(@RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    //  Feature 0: Retrieve all tasks
    @GetMapping
    public List<TaskDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    //  Feature 0: Add a new staff member
    @PostMapping("/staff")
    public Staff addStaff(@RequestBody Map<String, String> request) {
        return taskService.addStaff(request.get("name"));
    }

    //  Bug Fix 1: Reassign a task and automatically cancel the previous one
    @PostMapping("/{taskId}/reassign")
    public ResponseEntity<?> reassignTask(
            @PathVariable Long taskId,
            @RequestParam Long newStaffId
    ) {
        try {
            TaskDto reassignedTask = taskService.reassignTask(taskId, newStaffId);
            return ResponseEntity.ok(reassignedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //  Bug Fix 2: Return tasks by date range, excluding cancelled tasks
    @GetMapping("/by-date")
    public ResponseEntity<?> getTasksByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        if (start.isAfter(end)) {
            return ResponseEntity.badRequest().body("Start date must be before or equal to end date.");
        }
        return ResponseEntity.ok(taskService.getTasksByDateRange(start, end));
    }

    //  Feature 1: Smart daily task view — includes active tasks in range AND open ones started before
    @GetMapping("/smart-by-date")
    public ResponseEntity<?> getSmartTasks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        if (start.isAfter(end)) {
            return ResponseEntity.badRequest().body("Start date must be before or equal to end date.");
        }
        return ResponseEntity.ok(taskService.getSmartTasksByDateRange(start, end));
    }

    //  Feature 2: Update task priority (e.g., HIGH, MEDIUM, LOW)
    @PutMapping("/{id}/priority")
    public ResponseEntity<?> updatePriority(
            @PathVariable Long id,
            @RequestParam Priority priority
    ) {
        try {
            return ResponseEntity.ok(taskService.updateTaskPriority(id, priority));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //  Feature 2: Fetch tasks by a specific priority
    @GetMapping("/priority/{priority}")
    public List<TaskDto> getTasksByPriority(@PathVariable Priority priority) {
        return taskService.getTasksByPriority(priority);
    }

    //  Feature 3: Add a comment to a task
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Long taskId,
            @RequestBody Comment request
    ) {
        try {
            Comment comment = taskService.addCommentToTask(taskId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //  Feature 3: Get task details including activity history and comments
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskDetails(@PathVariable Long taskId) {
        try {
            TaskDto taskDetails = taskService.getTaskDetails(taskId);
            return ResponseEntity.ok(taskDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}