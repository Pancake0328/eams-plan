package com.eams.common.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * MyBatis 批处理执行器
 */
@Component
public class MybatisBatchExecutor {

    public static final int DEFAULT_BATCH_SIZE = 500;

    public <T> void execute(List<T> items, Consumer<List<T>> operation) {
        execute(items, DEFAULT_BATCH_SIZE, operation);
    }

    public <T> void execute(List<T> items, int batchSize, Consumer<List<T>> operation) {
        Objects.requireNonNull(operation, "operation must not be null");
        if (items == null || items.isEmpty()) {
            return;
        }
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be greater than 0");
        }

        for (int i = 0; i < items.size(); i += batchSize) {
            int end = Math.min(i + batchSize, items.size());
            operation.accept(items.subList(i, end));
        }
    }
}
