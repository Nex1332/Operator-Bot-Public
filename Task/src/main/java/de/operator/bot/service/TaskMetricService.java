package de.operator.bot.service;

import de.operator.bot.entity.enums.TaskType;

public interface TaskMetricService {
    void incrementProcessingTasks(TaskType taskType);
    void incrementCompletedTask(TaskType taskType);
    void incrementCanceledTask(TaskType taskType);
}
