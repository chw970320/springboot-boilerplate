package com.boilerplate.history.service;

import com.boilerplate.history.domain.VisitorHistory;
import com.boilerplate.history.domain.VisitorHistoryRepository;
import com.boilerplate.history.dto.VisitorHistoryResponse;
import com.boilerplate.history.mapper.HistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitorHistoryService {

    private final VisitorHistoryRepository visitorHistoryRepository;
    private final HistoryMapper historyMapper;

    /**
     * 방문 상세 이력을 비동기로 저장합니다.
     */
    @Async
    @Transactional
    public void saveVisitorHistory(String url, String method, String ipAddress, String userAgent, String username) {
        VisitorHistory history = VisitorHistory.builder()
                .url(url)
                .method(method)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .username(username)
                .build();
        visitorHistoryRepository.save(history);
    }

    /**
     * 방문 상세 이력 목록을 페이지 단위로 조회합니다.
     *
     * @param pageable 페이지 요청 정보 (page, size, sort)
     * @return Page<VisitorHistoryResponse> 페이지 단위의 방문 이력
     */
    public Page<VisitorHistoryResponse> getHistory(Pageable pageable) {
        return visitorHistoryRepository.findAll(pageable)
                .map(historyMapper::toDto);
    }
}
