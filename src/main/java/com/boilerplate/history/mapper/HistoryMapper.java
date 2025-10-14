package com.boilerplate.history.mapper;

import com.boilerplate.history.domain.VisitorHistory;
import com.boilerplate.history.dto.VisitorHistoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistoryMapper {
    VisitorHistoryResponse toDto(VisitorHistory visitorHistory);
}
