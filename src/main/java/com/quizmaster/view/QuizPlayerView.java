package com.quizmaster.view;

import com.quizmaster.model.Question;
import com.quizmaster.model.Quiz;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.function.BiConsumer;

public class QuizPlayerView extends VBox {

    private static final String BG = "#0d0d1a";
    private static final String BG2 = "#12122a";
    private static final String BG3 = "#1a1a3e";
    private static final String BG4 = "#222244";
    private static final String FG = "#e8e8ff";
    private static final String FG2 = "#8888aa";
    private static final String ACCENT = "#7c4dff";
    private static final String ACCENT2 = "#651fff";
    private static final String GREEN = "#00e676";
    private static final String ERROR = "#ff5252";
    private static final String WARNING = "#ffab40";

    private Quiz quiz;
    private int currentIndex = 0;
    private int score = 0;
    private BiConsumer<Integer, Integer> onFinish;
    private Runnable onExit;

    private Label questionLabel;
    private Label progressLabel;
    private VBox optionsBox;
    private ProgressBar timerBar;
    private Timeline timeline;
    private double timeRemaining;
    private boolean answered;

    public QuizPlayerView(Quiz quiz, BiConsumer<Integer, Integer> onFinish, Runnable onExit) {
        this.quiz = quiz;
        this.onFinish = onFinish;
        this.onExit = onExit;

        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: " + BG + ";");
        setAlignment(Pos.TOP_CENTER);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(quiz.getTitle());
        titleLabel.setTextFill(Color.web(FG));
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        progressLabel = new Label();
        progressLabel.setTextFill(Color.web(FG2));
        progressLabel.setFont(Font.font("Segoe UI", 13));

        Button exitBtn = new Button("✕ Salir");
        exitBtn.setStyle(
                "-fx-background-color: " + BG3 + ";" +
                        "-fx-text-fill: " + ERROR + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 6 12 6 12;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );
        exitBtn.setOnAction(e -> {
            if (timeline != null) timeline.stop();
            onExit.run();
        });

        header.getChildren().addAll(titleLabel, spacer, progressLabel, exitBtn);

        timerBar = new ProgressBar(1.0);
        timerBar.setMaxWidth(Double.MAX_VALUE);
        timerBar.setPrefHeight(8);
        timerBar.setStyle("-fx-accent: " + ACCENT + ";");

        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setMaxWidth(600);
        card.setStyle("-fx-background-color: " + BG2 + "; -fx-background-radius: 12;");

        questionLabel = new Label();
        questionLabel.setTextFill(Color.web(FG));
        questionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        questionLabel.setWrapText(true);

        optionsBox = new VBox(10);

        card.getChildren().addAll(questionLabel, optionsBox);

        getChildren().addAll(header, timerBar, card);

        loadQuestion();
    }

    private void loadQuestion() {
        if (timeline != null) timeline.stop();
        answered = false;

        Question q = quiz.getQuestions().get(currentIndex);
        questionLabel.setText(q.getText());
        progressLabel.setText((currentIndex + 1) + " / " + quiz.getQuestions().size() + "  —  Puntos: " + score);

        optionsBox.getChildren().clear();

        for (int i = 0; i < q.getOptions().size(); i++) {
            final int index = i;
            Button optBtn = new Button(q.getOptions().get(i));
            optBtn.setMaxWidth(Double.MAX_VALUE);
            optBtn.setWrapText(true);
            optBtn.setAlignment(Pos.CENTER_LEFT);
            optBtn.setStyle(getOptionStyle(BG3, FG));
            optBtn.setOnAction(e -> selectAnswer(index, q.getCorrectIndex(), optBtn));
            optionsBox.getChildren().add(optBtn);
        }

        if (quiz.getTimePerQuestion() > 0) {
            timerBar.setVisible(true);
            timeRemaining = quiz.getTimePerQuestion();
            timerBar.setProgress(1.0);

            timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
                timeRemaining -= 0.1;
                double ratio = timeRemaining / quiz.getTimePerQuestion();
                timerBar.setProgress(Math.max(0, ratio));

                if (ratio < 0.3) {
                    timerBar.setStyle("-fx-accent: " + ERROR + ";");
                } else if (ratio < 0.6) {
                    timerBar.setStyle("-fx-accent: " + WARNING + ";");
                } else {
                    timerBar.setStyle("-fx-accent: " + ACCENT + ";");
                }

                if (timeRemaining <= 0 && !answered) {
                    timeline.stop();
                    showCorrectAnswer(q.getCorrectIndex(), -1);
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        } else {
            timerBar.setVisible(false);
        }
    }

    private void selectAnswer(int selectedIndex, int correctIndex, Button selectedBtn) {
        if (answered) return;
        if (timeline != null) timeline.stop();
        showCorrectAnswer(correctIndex, selectedIndex);
    }

    private void showCorrectAnswer(int correctIndex, int selectedIndex) {
        answered = true;

        if (selectedIndex == correctIndex) {
            score++;
        }

        for (int i = 0; i < optionsBox.getChildren().size(); i++) {
            Button btn = (Button) optionsBox.getChildren().get(i);
            btn.setDisable(true);

            if (i == correctIndex) {
                btn.setStyle(getOptionStyle(GREEN, "white"));
            } else if (i == selectedIndex) {
                btn.setStyle(getOptionStyle(ERROR, "white"));
            } else {
                btn.setStyle(getOptionStyle(BG3, FG2));
            }
        }

        Button nextBtn = new Button(currentIndex < quiz.getQuestions().size() - 1 ? "Siguiente →" : "Finalizar");
        nextBtn.setMaxWidth(Double.MAX_VALUE);
        nextBtn.setStyle(
                "-fx-background-color: " + ACCENT + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );
        nextBtn.setOnAction(e -> nextQuestion());
        optionsBox.getChildren().add(nextBtn);
    }

    private void nextQuestion() {
        currentIndex++;
        if (currentIndex < quiz.getQuestions().size()) {
            loadQuestion();
        } else {
            onFinish.accept(score, quiz.getQuestions().size());
        }
    }

    private String getOptionStyle(String bg, String fg) {
        return "-fx-background-color: " + bg + ";" +
                "-fx-text-fill: " + fg + ";" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 12 16 12 16;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";
    }
}