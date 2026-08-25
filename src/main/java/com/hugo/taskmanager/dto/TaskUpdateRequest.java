package com.hugo.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TaskUpdateRequest {
    @NotBlank(message = "Title is required")
    private String title;
    @Pattern(regexp = "TODO|IN_PROGRESS|DONE", message = "Status must be TODO, IN_PROGRESS, or DONE")
    private String status;
    @NotNull(message = "UserID is required")
    private Long userId;
}
