package com.hugo.taskmanager.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String status;
    private LocalDateTime createdAt;
    private Long userId;
    private String username;
}
