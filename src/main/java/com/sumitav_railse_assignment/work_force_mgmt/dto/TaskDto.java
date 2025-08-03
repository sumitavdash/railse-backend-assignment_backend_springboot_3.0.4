package com.sumitav_railse_assignment.work_force_mgmt.dto;

import com.sumitav_railse_assignment.work_force_mgmt.model.ActivityLog;
import com.sumitav_railse_assignment.work_force_mgmt.model.Comment;
import com.sumitav_railse_assignment.work_force_mgmt.model.Priority;
import com.sumitav_railse_assignment.work_force_mgmt.model.Status;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private Status status;
    private Priority priority;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String assignedStaffName;

    private List<Comment> comments;
    private List<ActivityLog> activityHistory;
}
