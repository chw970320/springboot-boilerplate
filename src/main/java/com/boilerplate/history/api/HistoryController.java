package com.boilerplate.history.api;

import com.boilerplate.core.common.ApiResponse;
import com.boilerplate.history.dto.VisitorHistoryResponse;
import com.boilerplate.history.service.VisitorHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 방문 이력 조회 REST API 컨트롤러.
 */
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "History", description = "방문 이력 조회 API")
public class HistoryController {

    private final VisitorHistoryService visitorHistoryService;

    /**
     * 방문 상세 이력 목록을 페이지 단위로 조회합니다. (ADMIN만 가능)
     * 예: /api/history?page=0&size=10&sort=visitedAt,desc
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "방문 상세 이력 조회", description = "모든 사용자의 방문 상세 이력을 페이지 단위로 조회합니다. (ADMIN 권한 필요)")
    public ResponseEntity<ApiResponse<Page<VisitorHistoryResponse>>> getHistory(Pageable pageable) {
        Page<VisitorHistoryResponse> historyPage = visitorHistoryService.getHistory(pageable);
        return ResponseEntity.ok(ApiResponse.success(historyPage));
    }
}
