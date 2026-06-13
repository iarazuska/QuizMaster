package com.quizmaster.controller;

import com.quizmaster.model.Quiz;
import com.quizmaster.model.QuizAttempt;
import com.quizmaster.util.QuizStorage;
import com.quizmaster.view.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    private static final String BG = "#0d0d1a";
    private static final String BG2 = "#12122a";
    private static final String BG3 = "#1a1a3e";
    private static final String BG4 = "#222244";
    private static final String FG = "#e8e8ff";
    private static final String FG2 = "#8888aa";
    private static final String ACCENT = "#7c4dff";
    private static final String ACCENT2 = "#651fff";
    private static final String GREEN = "#00e676";
    private static final String WARNING = "#ffab40";

    private final Stage stage;
    private List<Quiz> quizzes;
    private List<QuizAttempt> attempts;

    private BorderPane root;
    private TabPane tabPane;
    private VBox quizzesContainer;
    private VBox historyContainer;
    private VBox statsContainer;

    public MainController(Stage stage) {
        this.stage = stage;
        this.quizzes = QuizStorage.loadQuizzes();
        this.attempts = QuizStorage.loadAttempts();
    }

    public BorderPane getRoot() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + ";");

        root.setTop(buildHeader());
        root.setCenter(buildTabs());

        return root;
    }

    private VBox buildHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: " + BG3 + ";");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setPadding(new Insets(16, 20, 16, 20));

        Label title = new Label("🧠 QuizMaster");
        title.setTextFill(Color.web(FG));
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        Label subtitle = new Label("  —  Quiz Maker & Player");
        subtitle.setTextFill(Color.web(FG2));
        subtitle.setFont(Font.font("Segoe UI", 13));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Nuevo quiz");
        addBtn.setStyle(getButtonStyle(ACCENT, "white"));
        addBtn.setOnAction(e -> openAddDialog());

        topRow.getChildren().addAll(title, subtitle, spacer, addBtn);
        header.getChildren().add(topRow);

        return header;
    }

    private TabPane buildTabs() {
        tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: " + BG + ";");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab quizzesTab = new Tab("📋 Quizzes");
        quizzesContainer = new VBox(10);
        quizzesContainer.setPadding(new Insets(20));
        ScrollPane quizzesScroll = new ScrollPane(quizzesContainer);
        quizzesScroll.setFitToWidth(true);
        quizzesScroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + ";");
        quizzesTab.setContent(quizzesScroll);

        Tab historyTab = new Tab("🕐 Historial");
        historyContainer = new VBox(10);
        historyContainer.setPadding(new Insets(20));
        ScrollPane historyScroll = new ScrollPane(historyContainer);
        historyScroll.setFitToWidth(true);
        historyScroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + ";");
        historyTab.setContent(historyScroll);

        Tab statsTab = new Tab("📊 Estadísticas");
        statsContainer = new VBox(10);
        statsContainer.setPadding(new Insets(20));
        ScrollPane statsScroll = new ScrollPane(statsContainer);
        statsScroll.setFitToWidth(true);
        statsScroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + ";");
        statsTab.setContent(statsScroll);

        tabPane.getTabs().addAll(quizzesTab, historyTab, statsTab);

        refreshQuizzes();
        refreshHistory();
        refreshStats();

        return tabPane;
    }

    private void refreshQuizzes() {
        quizzesContainer.getChildren().clear();

        if (quizzes.isEmpty()) {
            Label empty = new Label("No tienes quizzes aún. Crea uno con '+ Nuevo quiz'");
            empty.setTextFill(Color.web(FG2));
            empty.setFont(Font.font("Segoe UI", 13));
            empty.setPadding(new Insets(40, 0, 0, 0));
            quizzesContainer.getChildren().add(empty);
            return;
        }

        for (Quiz quiz : quizzes) {
            QuizListView item = new QuizListView(
                    quiz,
                    this::playQuiz,
                    this::openEditDialog,
                    this::deleteQuiz
            );
            quizzesContainer.getChildren().add(item);
        }
    }

    private void refreshHistory() {
        historyContainer.getChildren().clear();

        if (attempts.isEmpty()) {
            Label empty = new Label("No has jugado ningún quiz aún");
            empty.setTextFill(Color.web(FG2));
            empty.setFont(Font.font("Segoe UI", 13));
            empty.setPadding(new Insets(40, 0, 0, 0));
            historyContainer.getChildren().add(empty);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<QuizAttempt> sorted = attempts.stream()
                .sorted(Comparator.comparing(QuizAttempt::getDate).reversed())
                .collect(Collectors.toList());

        for (QuizAttempt attempt : sorted) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle("-fx-background-color: " + BG3 + "; -fx-background-radius: 8;");

            VBox info = new VBox(2);
            Label titleLabel = new Label(attempt.getQuizTitle());
            titleLabel.setTextFill(Color.web(FG));
            titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

            Label dateLabel = new Label(attempt.getDate().format(formatter));
            dateLabel.setTextFill(Color.web(FG2));
            dateLabel.setFont(Font.font("Segoe UI", 11));

            info.getChildren().addAll(titleLabel, dateLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            int percentage = attempt.getPercentage();
            String color = percentage >= 80 ? GREEN : percentage >= 50 ? WARNING : "#ff5252";

            Label scoreLabel = new Label(attempt.getScore() + "/" + attempt.getTotalQuestions() + "  (" + percentage + "%)");
            scoreLabel.setTextFill(Color.web(color));
            scoreLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

            row.getChildren().addAll(info, spacer, scoreLabel);
            historyContainer.getChildren().add(row);
        }
    }

    private void refreshStats() {
        statsContainer.getChildren().clear();

        if (attempts.isEmpty()) {
            Label empty = new Label("Juega algún quiz para ver tus estadísticas");
            empty.setTextFill(Color.web(FG2));
            empty.setFont(Font.font("Segoe UI", 13));
            empty.setPadding(new Insets(40, 0, 0, 0));
            statsContainer.getChildren().add(empty);
            return;
        }

        int totalAttempts = attempts.size();
        double avgPercentage = attempts.stream().mapToInt(QuizAttempt::getPercentage).average().orElse(0);
        int bestScore = attempts.stream().mapToInt(QuizAttempt::getPercentage).max().orElse(0);

        HBox cards = new HBox(12);

        cards.getChildren().addAll(
                statCard("📝", "Quizzes jugados", String.valueOf(totalAttempts)),
                statCard("📊", "Media de aciertos", Math.round(avgPercentage) + "%"),
                statCard("🏆", "Mejor resultado", bestScore + "%"),
                statCard("📋", "Quizzes creados", String.valueOf(quizzes.size()))
        );

        statsContainer.getChildren().add(cards);
    }

    private VBox statCard(String icon, String label, String value) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + BG3 + "; -fx-background-radius: 10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(24));

        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.web(FG));
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        Label textLabel = new Label(label);
        textLabel.setTextFill(Color.web(FG2));
        textLabel.setFont(Font.font("Segoe UI", 11));

        card.getChildren().addAll(iconLabel, valueLabel, textLabel);
        return card;
    }

    private void playQuiz(Quiz quiz) {
        QuizPlayerView player = new QuizPlayerView(
                quiz,
                (score, total) -> finishQuiz(quiz, score, total),
                () -> root.setCenter(tabPane)
        );
        root.setCenter(player);
    }

    private void finishQuiz(Quiz quiz, int score, int total) {
        QuizAttempt attempt = new QuizAttempt(quiz.getId(), quiz.getTitle(), score, total);
        attempts.add(attempt);
        QuizStorage.saveAttempts(attempts);

        QuizResultView result = new QuizResultView(quiz.getTitle(), score, total, () -> {
            root.setCenter(tabPane);
            refreshHistory();
            refreshStats();
        });
        root.setCenter(result);
    }

    private void openAddDialog() {
        QuizFormDialog.show(stage, null, quiz -> {
            quizzes.add(quiz);
            QuizStorage.saveQuizzes(quizzes);
            refreshQuizzes();
        });
    }

    private void openEditDialog(Quiz quiz) {
        QuizFormDialog.show(stage, quiz, updated -> {
            QuizStorage.saveQuizzes(quizzes);
            refreshQuizzes();
        });
    }

    private void deleteQuiz(Quiz quiz) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar quiz");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar '" + quiz.getTitle() + "'? Esta acción no se puede deshacer.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                quizzes.remove(quiz);
                QuizStorage.saveQuizzes(quizzes);
                refreshQuizzes();
            }
        });
    }

    private String getButtonStyle(String bg, String fg) {
        return "-fx-background-color: " + bg + ";" +
                "-fx-text-fill: " + fg + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 14 8 14;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;";
    }
}