package com.quizmaster.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quizmaster.model.Quiz;
import com.quizmaster.model.QuizAttempt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class QuizStorage {

    private static final Path STORAGE_DIR = Paths.get(System.getProperty("user.home"), ".quizmaster");
    private static final Path QUIZZES_FILE = STORAGE_DIR.resolve("quizzes.json");
    private static final Path ATTEMPTS_FILE = STORAGE_DIR.resolve("attempts.json");

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .registerModule(new JavaTimeModule());

    public static List<Quiz> loadQuizzes() {
        try {
            if (!Files.exists(QUIZZES_FILE)) {
                return new ArrayList<>();
            }
            Quiz[] quizzes = mapper.readValue(QUIZZES_FILE.toFile(), Quiz[].class);
            List<Quiz> list = new ArrayList<>();
            for (Quiz q : quizzes) {
                list.add(q);
            }
            return list;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveQuizzes(List<Quiz> quizzes) {
        try {
            ensureDir();
            mapper.writeValue(QUIZZES_FILE.toFile(), quizzes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<QuizAttempt> loadAttempts() {
        try {
            if (!Files.exists(ATTEMPTS_FILE)) {
                return new ArrayList<>();
            }
            QuizAttempt[] attempts = mapper.readValue(ATTEMPTS_FILE.toFile(), QuizAttempt[].class);
            List<QuizAttempt> list = new ArrayList<>();
            for (QuizAttempt a : attempts) {
                list.add(a);
            }
            return list;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveAttempts(List<QuizAttempt> attempts) {
        try {
            ensureDir();
            mapper.writeValue(ATTEMPTS_FILE.toFile(), attempts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ensureDir() throws IOException {
        if (!Files.exists(STORAGE_DIR)) {
            Files.createDirectories(STORAGE_DIR);
        }
    }
}