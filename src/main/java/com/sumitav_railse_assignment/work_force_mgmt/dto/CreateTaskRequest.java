package com.sumitav_railse_assignment.work_force_mgmt.dto;


import com.sumitav_railse_assignment.work_force_mgmt.model.Priority;
import lombok.Data;

import java.time.LocalDate;

@Data
    public class CreateTaskRequest {
        private String title;
        private Priority priority;
        private LocalDate startDate;
        private LocalDate dueDate;
        private Long staffId;
    }

