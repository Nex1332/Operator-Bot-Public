package de.operator.bot.service.impl;

import de.operator.bot.PaymentStatus;
import de.operator.bot.cache.TaskCacheService;
import de.operator.bot.controller.TelegramBot;
import de.operator.bot.entity.AppUser;
import de.operator.bot.entity.Task;
import de.operator.bot.entity.TaskInfo;
import de.operator.bot.entity.enums.FileType;
import de.operator.bot.repository.TaskInfoRepository;
import de.operator.bot.repository.TaskRepository;
import de.operator.bot.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.*;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static de.operator.bot.entity.enums.FileType.*;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TelegramBot telegramBot;
    private final RestTemplate restTemplate;
    private final TaskRepository taskRepository;
    private final TaskCacheService taskCacheService;
    private final TaskInfoRepository taskInfoRepository;
    @Value("${bot.token}")
    private String botToken;

    @Override
    public Task processPaymentStatus(PaymentStatus paymentStatus) {
        Task task = taskCacheService.getTaskByAppUserId(paymentStatus.getTelegramUserId());

        if (paymentStatus.isPaid()) {
            if (task != null) {
                List<TaskInfo> taskInfos = task.getTaskInfos();

                task.setPaid(true);
                task.setPaymentIntent(paymentStatus.getPaymentIntent());

                for (TaskInfo taskInfo : taskInfos) {
                    taskInfo.setTask(task);
                }

                task.setTaskInfos(new ArrayList<>());
                taskRepository.save(task);
                taskInfoRepository.saveAll(taskInfos);
                return task;
            } else {
                return null;
            }
        } else {
            taskCacheService.deleteTask(task, paymentStatus.getTelegramUserId());

            return null;
        }
    }

    @Override
    public void processInfoState(Update update, AppUser appUser, FileType fileType) throws TelegramApiException, IOException {
        String taskInfo = processTask(update, fileType);
        saveTaskInfo(appUser, taskInfo, fileType);
    }

    @Override
    public Task processCancelTask(Integer taskId) {
        Task task = taskRepository.findByTaskId(taskId);

        task.setAppUserId(null);
        task.setOperatorId(null);
        task.setCanceled(true);

        taskRepository.save(task);

        return task;
    }

    private String processTask(Update update, FileType fileType) throws TelegramApiException, IOException {
        String info;

        if (Objects.requireNonNull(fileType) == TEXT) {
            info = update.getMessage().getText();
        } else {
            info = String.valueOf(getPathOfFile(update, fileType));
        }

        return info;
    }

    private void saveTaskInfo(AppUser appUser, String info, FileType fileType) {
        Task task = taskCacheService.getTaskByAppUserId(appUser.getTelegramUserId());

        TaskInfo taskInfo = TaskInfo.builder()
                .info(info)
                .fileType(fileType)
                .build();

        task.getTaskInfos().add(taskInfo);

        taskCacheService.putTask(task, appUser.getTelegramUserId());
    }

    private Path getPathOfFile(Update update, FileType fileType) throws TelegramApiException, IOException {
        String fileId = "";
        String fileName = "";

        switch (fileType) {
            case VOICE -> {
                fileId = update.getMessage().getVoice().getFileId();
                fileName = null;
            }
            case PHOTO -> {
                PhotoSize photoSize = update.getMessage().getPhoto().stream()
                        .max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElseThrow();

                fileId = photoSize.getFileId();
                fileName = null;
            }
            case DOCUMENT -> {
                fileId = update.getMessage().getDocument().getFileId();
                fileName = update.getMessage().getDocument().getFileName();
            }
        }

        return downloadFile(fileId, fileType, fileName);
    }

    private Path downloadFile(String fileId, FileType fileType, String fileName) throws TelegramApiException, IOException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);

        File telegramFile = telegramBot.execute(getFile);
        String filePath = telegramFile.getFilePath();
        String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + filePath;

        String directory;
        String localFileName = switch (fileType) {
            case VOICE -> {
                directory = "voices/";
                yield fileId + ".ogg";
            }
            case PHOTO -> {
                directory = "photos/";
                yield fileId + ".jpg";
            }
            case DOCUMENT -> {
                directory = "documents/";
                yield (fileName != null && !fileName.isEmpty()) ? fileName : fileId;
            }
            default -> throw new IllegalStateException("Unexpected file type: " + fileType);
        };

        Path path = Paths.get("/app/files/" + directory, localFileName);
        Files.createDirectories(path.getParent());

        byte[] fileBytes = restTemplate.getForObject(fileUrl, byte[].class);
        if (fileBytes != null) {
            Files.write(path, fileBytes);
        }
        return path.toAbsolutePath();
    }
}