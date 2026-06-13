package com.quizmaster.model;

import java.time.LocalDateTime;

public class QuizAttempt {
    private String quizId;
    private String quizTitle;
    private int score;
    private int totalQuestions;
    private LocalDateTime date;

    public QuizAttempt() {
    }

    public QuizAttempt(String quizId, String quizTitle, int score, int totalQuestions) {
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.date = LocalDateTime.now();
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getPercentage() {
        if (totalQuestions == 0) return 0;
        return (int) Math.round((score * 100.0) / totalQuestions);
    }
}