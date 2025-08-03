package com.sumitav_railse_assignment.work_force_mgmt.service;

import com.sumitav_railse_assignment.work_force_mgmt.dto.CreateTaskRequest;
import com.sumitav_railse_assignment.work_force_mgmt.dto.TaskDto;
import com.sumitav_railse_assignment.work_force_mgmt.mapper.TaskMapper;
import com.sumitav_railse_assignment.work_force_mgmt.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final Map<Long, Task> taskMap = new HashMap<>();
    private final Map<Long, Staff> staffMap = new HashMap<>();
    private final AtomicLong taskIdGenerator = new AtomicLong();
    private final AtomicLong staffIdGenerator = new AtomicLong();
    private final TaskMapper taskMapper;


    /**
     * Feature 0: Create a new task and assign it to a staff member.
     */
    public TaskDto createTask(CreateTaskRequest request) {
        Staff staff = staffMap.get(request.getStaffId());
        if (staff == null) {
            throw new RuntimeException("Staff not found");
        }
//        Task task = new Task(
//                taskIdGenerator.incrementAndGet(),
//                request.getTitle(),
//                Status.ACTIVE,
//                request.getPriority(),
//                request.getStartDate(),
//                request.getDueDate(),
//                staff
//        );
        Task task = Task.builder()
                .id(taskIdGenerator.incrementAndGet())
                .title(request.getTitle())
                .status(Status.ACTIVE)
                .priority(request.getPriority())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .assignedStaff(staff)
                .comments(new ArrayList<>())
                .activityHistory(new ArrayList<>())
                .build();
        taskMap.put(task.getId(), task);

        return taskMapper.taskToDto(task);
    }


    /**
     * Feature 0: Retrieve all tasks.
     */
    public List<TaskDto> getAllTasks() {
        return taskMap.values().stream()
                .map(taskMapper::taskToDto)
                .collect(Collectors.toList());
    }


    /**
     * Feature 0: Add a new staff member.
     */
    public Staff addStaff(String name) {
        Staff staff = new Staff(staffIdGenerator.incrementAndGet(), name);
        staffMap.put(staff.getId(), staff);
        return staff;
    }


    /**
     * Bug Fix 1: Reassign a task to a new staff member by canceling the old task and creating a new one.
     */
    public TaskDto reassignTask(Long taskId, Long newStaffId) {
        Task oldTask = taskMap.get(taskId);
        if (oldTask == null) {
            throw new RuntimeException("Task not found with ID: " + taskId);
        }

        Staff newStaff = staffMap.get(newStaffId);
        if (newStaff == null) {
            throw new RuntimeException("Staff not found with ID: " + newStaffId);
        }

        // Cancel old task
        oldTask.setStatus(Status.CANCELLED);

        // Create new task with same info but new staff
//        Task newTask = new Task(
//                taskIdGenerator.incrementAndGet(),
//                oldTask.getTitle(),
//                Status.ACTIVE,
//                oldTask.getPriority(),
//                oldTask.getStartDate(),
//                oldTask.getDueDate(),
//                newStaff
//        );
        Task newTask = Task.builder()
                .id(taskIdGenerator.incrementAndGet())
                .title(oldTask.getTitle())
                .status(Status.ACTIVE)
                .priority(oldTask.getPriority())
                .startDate(oldTask.getStartDate())
                .dueDate(oldTask.getDueDate())
                .assignedStaff(newStaff)
                .comments(new ArrayList<>())
                .activityHistory(new ArrayList<>())
                .build();

        taskMap.put(newTask.getId(), newTask);
        return taskMapper.taskToDto(newTask);
    }

    /**
     * Bug Fix 2: Return all tasks in a date range, excluding CANCELLED ones.
     */
    public List<TaskDto> getTasksByDateRange(LocalDate start, LocalDate end) {
        return taskMap.values().stream()
                .filter(task -> task.getStatus() != Status.CANCELLED)
                .filter(task ->
                        !task.getStartDate().isAfter(end) && !task.getDueDate().isBefore(start)
                )
                .map(taskMapper::taskToDto)
                .collect(Collectors.toList());
    }


    /**
     *  Feature 1: Smart Task View - show tasks that are either ongoing or overlap with the date range.
     */
    public List<TaskDto> getSmartTasksByDateRange(LocalDate start, LocalDate end) {
        return taskMap.values().stream()
                .filter(task -> task.getStatus() == Status.ACTIVE)
                .filter(task ->
                        // Started in range
                        (!task.getStartDate().isAfter(end) && !task.getStartDate().isBefore(start))
                                // OR started before range and still open (dueDate >= start)
                                || (task.getStartDate().isBefore(start) && !task.getDueDate().isBefore(start))
                )
                .map(taskMapper::taskToDto)
                .collect(Collectors.toList());
    }

    /**
     * Feature 2: Update task priority and return updated task.
     */
    public TaskDto updateTaskPriority(Long taskId, Priority newPriority) {
        Task task = taskMap.get(taskId);
        if (task == null) throw new RuntimeException("Task not found");
        task.setPriority(newPriority);
        return taskMapper.taskToDto(task);
    }

    /**
     * Feature 2: Filter tasks by priority.
     */
    public List<TaskDto> getTasksByPriority(Priority priority) {
        return taskMap.values().stream()
                .filter(task -> task.getPriority() == priority && task.getStatus() != Status.CANCELLED)
                .map(taskMapper::taskToDto)
                .collect(Collectors.toList());
    }



    /**
     * Feature 3: Explicit log activity (e.g. “Priority changed”).
     */
    public void logActivity(Long taskId, String actor, String description) {
        Task task = findTaskById(taskId);
        ActivityLog log = new ActivityLog(actor, description, LocalDateTime.now());
        task.getActivityHistory().add(log);
    }


    /**
     * Feature 3: Add comment via request object.
     */
    public Comment addCommentToTask(Long taskId, Comment commentRequest) {
        Task task = findTaskById(taskId);
        Comment comment = new Comment(commentRequest.getCommenter(), commentRequest.getMessage(), LocalDateTime.now());
        task.getComments().add(comment);

        ActivityLog log = new ActivityLog(commentRequest.getCommenter(), "Added a comment", LocalDateTime.now());
        task.getActivityHistory().add(log);

        return comment;
    }

    /**
     * Feature 3: Get detailed view of a task including comments and activity history.
     */
    public TaskDto getTaskDetails(Long taskId) {
        Task task = findTaskById(taskId);

        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setStartDate(task.getStartDate());
        dto.setDueDate(task.getDueDate());
        dto.setAssignedStaffName(task.getAssignedStaff().getName());
        dto.setComments(task.getComments());
        dto.setActivityHistory(task.getActivityHistory());

        return dto;
    }


    /**
     * Utility Method: Find a task by ID or throw if not found.
     */
    private Task findTaskById(Long taskId) {
        Task task = taskMap.get(taskId);
        if (task == null) {
            throw new RuntimeException("Task not found with ID: " + taskId);
        }
        return task;
    }

}