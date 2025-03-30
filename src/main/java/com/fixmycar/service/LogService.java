//package com.fixmycar.service;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.NoSuchElementException;
//import org.springframework.stereotype.Service;
//
//@Service
//public class LogService {
//
//    private static final String LOG_FILE_PATH = "logs/application.log";
//    private static final String LOGS_DIR = "logs/";
//
//    public String generateLogFileForDate(String date) throws IOException {
//        Path logPath = Paths.get(LOG_FILE_PATH);
//        if (!Files.exists(logPath)) {
//            throw new FileNotFoundException("Log file not found: " + LOG_FILE_PATH);
//        }
//        List<String> filteredLines;
//        try (var lines = Files.lines(logPath)) {
//            filteredLines = lines
//                    .filter(line -> line.startsWith(date))
//                    .toList();
//        }
//        if (filteredLines.isEmpty()) {
//            throw new NoSuchElementException("No logs found for the given date");
//        }
//        Files.createDirectories(Paths.get(LOGS_DIR));
//        String logFileName = LOGS_DIR + "logs-" + date + ".log";
//        Path logFilePath = Paths.get(logFileName);
//        Files.write(logFilePath, filteredLines);
//        return logFileName;
//    }
//}