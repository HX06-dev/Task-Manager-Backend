package com.hugo.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;
    @NotNull(message = "UserID is required")
    private Long userId;
}
