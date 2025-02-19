package com.colon.mattfolio.mapper;

import com.colon.mattfolio.model.HistoryLogRegisterRequestDto;

public interface HistoryLogMapper {

    int insertErrorLog(HistoryLogRegisterRequestDto request);

}