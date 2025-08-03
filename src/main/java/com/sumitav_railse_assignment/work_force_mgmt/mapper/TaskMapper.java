package com.sumitav_railse_assignment.work_force_mgmt.mapper;

import com.sumitav_railse_assignment.work_force_mgmt.dto.TaskDto;
import com.sumitav_railse_assignment.work_force_mgmt.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "assignedStaff.name", target = "assignedStaffName")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "activityHistory", source = "activityHistory")
    TaskDto taskToDto(Task task);
}