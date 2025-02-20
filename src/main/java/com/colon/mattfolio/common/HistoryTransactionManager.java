package com.colon.mattfolio.common;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class HistoryTransactionManager {
    private static final ThreadLocal<List<Runnable>> HISTORY_OPERATIONS = ThreadLocal.withInitial(ArrayList::new);

    // 히스토리 저장 요청을 임시 저장 (바로 실행하지 않음)
    public void registerHistoryOperation(Runnable operation) {
        HISTORY_OPERATIONS.get()
            .add(operation);
    }

    // 트랜잭션 종료 시 호출 → 히스토리 저장 실행
    public void commit() {
        List<Runnable> operations = HISTORY_OPERATIONS.get();
        if (operations != null) {
            for (Runnable operation : operations) {
                operation.run();
            }
            operations.clear();
        }
        clear();
    }

    // 트랜잭션 롤백 시 호출 → 저장된 히스토리 삭제
    public void rollback() {
        List<Runnable> operations = HISTORY_OPERATIONS.get();

        // 실행 중인 히스토리가 있는 경우에만 롤백 실행
        if (operations != null && !operations.isEmpty()) {
            operations.clear();
            clear(); // ThreadLocal 제거
        }
    }

    // 스레드 종료 시 초기화
    public void clear() {
        HISTORY_OPERATIONS.remove();
    }
}
