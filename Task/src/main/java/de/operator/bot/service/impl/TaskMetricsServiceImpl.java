package de.operator.bot.service.impl;

import de.operator.bot.entity.enums.TaskType;
import de.operator.bot.service.TaskMetricService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class TaskMetricsServiceImpl implements TaskMetricService {
    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<TaskType, AtomicInteger> processingTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<TaskType, Counter> completedTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<TaskType, Counter> canceledTasks = new ConcurrentHashMap<>();

    @Override
    public void incrementProcessingTasks(TaskType taskType) {
        registerTaskTypeGauge(taskType, processingTasks);
        processingTasks.get(taskType).incrementAndGet();
    }

    @Override
    public void incrementCompletedTask(TaskType taskType) {
        registerTaskTypeCounter(taskType, completedTasks, "completed_tasks");
        completedTasks.get(taskType).increment();

        decrementProcessingTasks(taskType);
    }

    @Override
    public void incrementCanceledTask(TaskType taskType) {
        registerTaskTypeCounter(taskType, canceledTasks, "canceled_tasks");
        canceledTasks.get(taskType).increment();

        decrementProcessingTasks(taskType);
    }

    private void decrementProcessingTasks(TaskType taskType) {
        AtomicInteger processingTasksCounter = processingTasks.get(taskType);

        if (processingTasksCounter != null && processingTasksCounter.get() > 0) {
            processingTasksCounter.decrementAndGet();
        }
    }

    private void registerTaskTypeGauge(TaskType taskType, ConcurrentHashMap<TaskType, AtomicInteger> tasks) {
        tasks.computeIfAbsent(taskType, type -> {
            AtomicInteger counter = new AtomicInteger(0);
            meterRegistry.gauge("processing_tasks",
                    Tags.of("task_type", type.toString().toLowerCase()), counter);
            return counter;
        });
    }

    private void registerTaskTypeCounter(TaskType taskType, ConcurrentHashMap<TaskType, Counter> tasks, String metricName) {
        tasks.computeIfAbsent(taskType, type ->
                Counter.builder(metricName)
                        .tag("task_type", type.toString().toLowerCase())
                        .register(meterRegistry));
    }
}
