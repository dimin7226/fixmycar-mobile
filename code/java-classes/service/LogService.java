package com.fixmycar.service;

import com.fixmycar.exception.ProcessingFileException;
import com.fixmycar.exception.ResourceNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final String LOG_FILE_PATH = "logs/fixmycar.log";
    private static final String LOGS_DIR = "logs/";

    private final Map<String, String> logFiles = new ConcurrentHashMap<>();
    private final Map<String, String> taskStatus = new ConcurrentHashMap<>();


    @Async
    public CompletableFuture<String> generateLogFileForDateAsync(String date) {
        String taskId = UUID.randomUUID().toString();
        taskStatus.put(taskId, "PROCESSING");

        CompletableFuture.runAsync(() -> {

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                taskStatus.put(taskId, "ERROR: Задача прервана");
                return;
            }

            try {
                Path sourcePath = Paths.get(LOG_FILE_PATH);
                if (!Files.exists(sourcePath)) {
                    throw new ProcessingFileException("Исходный лог-файл не найден");
                }

                List<String> filteredLines;
                try (Stream<String> lines = Files.lines(sourcePath)) {
                    filteredLines = lines
                            .filter(line -> line.startsWith(date))
                            .toList();
                }

                if (filteredLines.isEmpty()) {
                    taskStatus.put(taskId, "FAILED: Логи по указанной дате не найдены");
                    throw new ResourceNotFoundException("Логи по указанной дате не найдены");
                }

                Files.createDirectories(Paths.get(LOGS_DIR));
                String filename = LOGS_DIR + "logs-" + date + "-" + taskId + ".log";
                Files.write(Paths.get(filename), filteredLines);

                logFiles.put(taskId, filename);
                taskStatus.put(taskId, "COMPLETED");
            } catch (Exception e) {
                String errorMsg = e.getMessage();
                taskStatus.put(taskId, "FAILED: " + errorMsg);
            }
        });

        return CompletableFuture.completedFuture(taskId);
    }

    public String getLogFilePath(String taskId) {
        return logFiles.get(taskId);
    }

    public String getTaskStatus(String taskId) {
        String status = taskStatus.getOrDefault(taskId, "NOT FOUND TASK");
        if (status.equals("NOT FOUND TASK")) {
            throw new ResourceNotFoundException("Не найдено задачи с ID = " + taskId);
        }
        return status;
    }
}