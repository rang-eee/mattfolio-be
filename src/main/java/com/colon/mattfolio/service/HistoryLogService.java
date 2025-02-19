package com.colon.mattfolio.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colon.mattfolio.mapper.HistoryLogMapper;
import com.colon.mattfolio.model.HistoryLogRegisterRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoryLogService {
    private final HistoryLogMapper historyLogMapper;

    public void insertErrorLog(HistoryLogRegisterRequestDto request) {
        historyLogMapper.insertErrorLog(request);
    }
}
