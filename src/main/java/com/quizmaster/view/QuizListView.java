package com.quizmaster.view;

import com.quizmaster.model.Quiz;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

public class QuizListView extends VBox {

    private static final String BG3 = "#1a1a3e";
    private static final String BG4 = "#222244";
    private static final String FG = "#e8e8ff";
    private static final String FG2 = "#8888aa";
    private static final String ACCENT = "#7c4dff";
    private static final String ACCENT2 = "#651fff";
    private static final String GREEN = "#00e676";
    private static final String ERROR = "#ff5252";

    public QuizListView(Quiz quiz, Consumer<Quiz> onPlay, Consumer<Quiz> onEdit, Consumer<Quiz> onDelete) {
        setPadding(new Insets(14));
        setSpacing(8);
        setStyle("-fx-background-color: " + BG3 + "; -fx-background-radius: 10;");

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4);
        Label titleLabel = new Label(quiz.getTitle());
        titleLabel.setTextFill(Color.web(FG));
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));

        Label descLabel = new Label(quiz.getDescription() != null ? quiz.getDescription() : "");
        descLabel.setTextFill(Color.web(FG2));
        descLabel.setFont(Font.font("Segoe UI", 12));
        descLabel.setWrapText(true);

        info.getChildren().addAll(titleLabel, descLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topRow.getChildren().addAll(info, spacer);

        HBox bottomRow = new HBox(8);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        Label countLabel = new Label(quiz.getQuestions().size() + " preguntas");
        countLabel.setTextFill(Color.web(FG2));
        countLabel.setFont(Font.font("Segoe UI", 11));

        if (quiz.getTimePerQuestion() > 0) {
            Label timeLabel = new Label("⏱ " + quiz.getTimePerQuestion() + "s/pregunta");
            timeLabel.setTextFill(Color.web(FG2));
            timeLabel.setFont(Font.font("Segoe UI", 11));
            bottomRow.getChildren().add(timeLabel);
        }

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        javafx.scene.control.Button playBtn = createButton("▶ Jugar", ACCENT, "white");
        playBtn.setOnAction(e -> onPlay.accept(quiz));

        javafx.scene.control.Button editBtn = createButton("✏️ Editar", BG4, FG2);
        editBtn.setOnAction(e -> onEdit.accept(quiz));

        javafx.scene.control.Button deleteBtn = createButton("🗑", BG4, ERROR);
        deleteBtn.setOnAction(e -> onDelete.accept(quiz));

        bottomRow.getChildren().addAll(countLabel, spacer2, playBtn, editBtn, deleteBtn);

        getChildren().addAll(topRow, bottomRow);
    }

    private javafx.scene.control.Button createButton(String text, String bg, String fg) {
        javafx.scene.control.Button btn = new javafx.scene.control.Button(text);
        btn.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-text-fill: " + fg + ";" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6 12 6 12;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );
        return btn;
    }
}