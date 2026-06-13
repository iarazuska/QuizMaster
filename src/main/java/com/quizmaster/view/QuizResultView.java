package com.quizmaster.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class QuizResultView extends VBox {

    private static final String BG = "#0d0d1a";
    private static final String BG2 = "#12122a";
    private static final String FG = "#e8e8ff";
    private static final String FG2 = "#8888aa";
    private static final String ACCENT = "#7c4dff";
    private static final String GREEN = "#00e676";
    private static final String WARNING = "#ffab40";
    private static final String ERROR = "#ff5252";

    public QuizResultView(String quizTitle, int score, int total, Runnable onClose) {
        setSpacing(16);
        setPadding(new Insets(40));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: " + BG + ";");

        int percentage = total == 0 ? 0 : (int) Math.round((score * 100.0) / total);

        String emoji;
        String color;
        String message;

        if (percentage >= 80) {
            emoji = "🎉";
            color = GREEN;
            message = "¡Excelente trabajo!";
        } else if (percentage >= 50) {
            emoji = "👍";
            color = WARNING;
            message = "¡Buen intento!";
        } else {
            emoji = "📚";
            color = ERROR;
            message = "Sigue practicando";
        }

        javafx.scene.control.Label emojiLabel = new javafx.scene.control.Label(emoji);
        emojiLabel.setFont(Font.font(64));

        javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
        messageLabel.setTextFill(Color.web(FG));
        messageLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        javafx.scene.control.Label quizLabel = new javafx.scene.control.Label(quizTitle);
        quizLabel.setTextFill(Color.web(FG2));
        quizLabel.setFont(Font.font("Segoe UI", 14));

        VBox scoreCard = new VBox(8);
        scoreCard.setAlignment(Pos.CENTER);
        scoreCard.setPadding(new Insets(24, 40, 24, 40));
        scoreCard.setStyle("-fx-background-color: " + BG2 + "; -fx-background-radius: 12;");

        javafx.scene.control.Label scoreLabel = new javafx.scene.control.Label(score + " / " + total);
        scoreLabel.setTextFill(Color.web(color));
        scoreLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));

        javafx.scene.control.Label percentLabel = new javafx.scene.control.Label(percentage + "% correcto");
        percentLabel.setTextFill(Color.web(FG2));
        percentLabel.setFont(Font.font("Segoe UI", 13));

        scoreCard.getChildren().addAll(scoreLabel, percentLabel);

        Button closeBtn = new Button("Volver a quizzes");
        closeBtn.setStyle(
                "-fx-background-color: " + ACCENT + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 32 12 32;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> onClose.run());

        getChildren().addAll(emojiLabel, messageLabel, quizLabel, scoreCard, closeBtn);
    }
}