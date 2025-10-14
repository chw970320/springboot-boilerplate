package com.boilerplate.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorHistoryResponse {
    private Long id;
    private String url;
    private String method;
    private String ipAddress;
    private String userAgent;
    private String username;
    private LocalDateTime visitedAt;
}
