package com.quizmaster.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Question {
    private String id;
    private String text;
    private List<String> options;
    private int correctIndex;

    public Question() {
        this.id = UUID.randomUUID().toString();
        this.options = new ArrayList<>();
    }

    public Question(String text, List<String> options, int correctIndex) {
        this();
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(int correctIndex) {
        this.correctIndex = correctIndex;
    }
}