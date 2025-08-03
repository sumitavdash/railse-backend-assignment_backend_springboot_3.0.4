package com.sumitav_railse_assignment.work_force_mgmt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    private Long id;
    private String title;
    private Status status;
    private Priority priority;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Staff assignedStaff;

    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
    @Builder.Default
    private List<ActivityLog> activityHistory = new ArrayList<>();
}
