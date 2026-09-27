package de.operator.bot.service.impl;

import de.operator.bot.PaymentStatus;
import de.operator.bot.ProcessTaskRequest;
import de.operator.bot.TaskAnswerMessage;
import de.operator.bot.TaskProcessingErrorEvent;
import de.operator.bot.cache.TaskCacheService;
import de.operator.bot.entity.AppUser;
import de.operator.bot.entity.Task;
import de.operator.bot.entity.enums.FileType;
import de.operator.bot.entity.enums.TaskType;
import de.operator.bot.repository.AppUserRepository;
import de.operator.bot.service.MainService;
import de.operator.bot.service.ProducerService;
import de.operator.bot.service.TaskMetricService;
import de.operator.bot.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Optional;

import static de.operator.bot.entity.enums.TaskType.UNKNOWN_TYPE;
import static de.operator.bot.enums.ErrorType.*;

@Log4j
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final TaskService taskService;
    private final ProducerService producerService;
    private final TaskMetricService taskMetricService;
    private final TaskCacheService taskCacheService;
    private final AppUserRepository appUserRepository;
    @Value("${constant.limit-task-infos}")
    private Integer limitTaskInfos;

    @Override
    public void handlePaymentStatus(PaymentStatus paymentStatus) {
        Task task = taskService.processPaymentStatus(paymentStatus);

        if (task != null) {
            producerService.sendToOperatorNewTaskEvent(task);

            taskMetricService.incrementProcessingTasks(task.getTaskType());
        } else if (paymentStatus.isPaid()) {
            Optional<AppUser> appUserOpt = appUserRepository.findByTelegramUserId(paymentStatus.getTelegramUserId());

            Long chatId = appUserOpt.get().getChatId();

            TaskProcessingErrorEvent errorEvent = TaskProcessingErrorEvent.builder()
                    .chatId(chatId)
                    .errorType(TASK_PAYMENT_TIMEOUT)
                    .build();

            producerService.sendToNodeTaskErrorEvent(errorEvent);

            taskMetricService.incrementProcessingTasks(UNKNOWN_TYPE);
        }
    }

    @Override
    public void handleProcessTaskRequest(ProcessTaskRequest processTaskRequest) {
        Update update = processTaskRequest.getUpdate();
        AppUser appUser = processTaskRequest.getAppUser();
        FileType fileType = FileType.valueOf(processTaskRequest.getFileType());

        try {
            Task task = taskCacheService.getTaskByAppUserId(appUser.getTelegramUserId());

            if (task.getTaskInfos() == null || task.getTaskInfos().size() <= limitTaskInfos) {
                taskService.processInfoState(update, appUser, fileType);
            } else {
                TaskProcessingErrorEvent taskProcessingErrorEvent = TaskProcessingErrorEvent.builder()
                        .chatId(appUser.getChatId())
                        .errorType(TOO_MUCH_INFOS)
                        .build();

                producerService.sendToNodeTaskErrorEvent(taskProcessingErrorEvent);
            }
        } catch (TelegramApiException e) {
            String exception = "The file wasn't found in the Telegram Repository. By processing voice message by User with username " +
                    appUser.getUsername();

//            TODO обработку ошибки
        } catch (IOException e) {
            String exception = "The Exception by writing the file into the file. By processing voice message by User with username " +
                    appUser.getUsername() + "\n Error: " + e;

//            TODO Добавить обработку ошибки
        }
    }

    @Override
    public void handleRequestCancelTask(Integer taskId) {
        Task task = taskService.processCancelTask(taskId);
        producerService.sendToOperatorNewTaskEvent(task);

        taskMetricService.incrementCanceledTask(task.getTaskType());
    }

    @Override
    public void handleProcessTaskAnswerMessage(TaskAnswerMessage taskAnswerMessage) {
        TaskType taskType = taskAnswerMessage.getTaskType();
        taskMetricService.incrementCompletedTask(taskType);

        SendMessage sendMessage = taskAnswerMessage.getSendMessage();
        producerService.sendToNodeResponseSendMessage(sendMessage);
    }
}