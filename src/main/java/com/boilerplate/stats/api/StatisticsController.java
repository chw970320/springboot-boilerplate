package com.boilerplate.stats.api;

import com.boilerplate.core.common.ApiResponse;
import com.boilerplate.stats.dto.DailyVisitorStatsResponse;
import com.boilerplate.stats.dto.MenuUsageStatsResponse;
import com.boilerplate.stats.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 통계 조회 REST API 컨트롤러.
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Statistics", description = "통계 조회 API")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 일일 방문자 수(DAU) 통계를 조회합니다. (ADMIN만 가능)
     */
    @GetMapping("/dau")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "일일 방문자 수(DAU) 통계 조회", description = "지정된 기간의 일일 순수 방문자 수를 조회합니다. (ADMIN 권한 필요)")
    public ResponseEntity<ApiResponse<List<DailyVisitorStatsResponse>>> getDailyVisitorStats(
            @Parameter(description = "조회 시작일 (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "조회 종료일 (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DailyVisitorStatsResponse> stats = statisticsService.getDailyVisitorStats(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * 메뉴별 사용 통계를 조회합니다. (ADMIN만 가능)
     */
    @GetMapping("/menu")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "메뉴별 사용 통계 조회", description = "지정된 기간의 메뉴별 사용 횟수를 조회합니다. (ADMIN 권한 필요)")
    public ResponseEntity<ApiResponse<List<MenuUsageStatsResponse>>> getMenuUsageStats(
            @Parameter(description = "조회 시작일 (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "조회 종료일 (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<MenuUsageStatsResponse> stats = statisticsService.getMenuUsageStats(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
