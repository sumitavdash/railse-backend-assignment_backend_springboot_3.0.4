package com.sumitav_railse_assignment.work_force_mgmt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    private String actor;
    private String description;
    private LocalDateTime timestamp;
}
