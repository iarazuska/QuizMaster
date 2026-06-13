package com.quizmaster.view;

import com.quizmaster.model.Question;
import com.quizmaster.model.Quiz;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class QuizFormDialog {

    private static final String BG = "#0d0d1a";
    private static final String BG2 = "#12122a";
    private static final String BG3 = "#1a1a3e";
    private static final String BG4 = "#222244";
    private static final String FG = "#e8e8ff";
    private static final String FG2 = "#8888aa";
    private static final String ACCENT = "#7c4dff";
    private static final String ACCENT2 = "#651fff";
    private static final String ERROR = "#ff5252";
    private static final String GREEN = "#00e676";

    private static List<QuestionFormRow> questionRows;
    private static VBox questionsContainer;

    public static void show(Stage owner, Quiz existing, Consumer<Quiz> onSave) {
        boolean editing = existing != null;
        questionRows = new ArrayList<>();

        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(editing ? "Editar quiz" : "Nuevo quiz");
        stage.setMinWidth(560);
        stage.setMinHeight(650);

        VBox root = new VBox(14);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: " + BG + ";");

        Label title = new Label(editing ? "Editar quiz" : "Nuevo quiz");
        title.setTextFill(Color.web(FG));
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        TextField titleField = createField("Título del quiz", editing ? existing.getTitle() : "");
        TextField descField = createField("Descripción", editing ? existing.getDescription() : "");

        HBox timeRow = new HBox(10);
        timeRow.setAlignment(Pos.CENTER_LEFT);
        Label timeLabel = new Label("Tiempo por pregunta (segundos, 0 = sin límite):");
        timeLabel.setTextFill(Color.web(FG2));
        timeLabel.setFont(Font.font("Segoe UI", 12));

        Spinner<Integer> timeSpinner = new Spinner<>(0, 300, editing ? existing.getTimePerQuestion() : 0, 5);
        timeSpinner.setEditable(true);
        timeSpinner.setPrefWidth(100);
        timeRow.getChildren().addAll(timeLabel, timeSpinner);

        Label questionsTitle = new Label("Preguntas");
        questionsTitle.setTextFill(Color.web(FG));
        questionsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        questionsContainer = new VBox(12);

        if (editing) {
            for (Question q : existing.getQuestions()) {
                addQuestionRow(q);
            }
        } else {
            addQuestionRow(null);
        }

        Button addQuestionBtn = new Button("+ Añadir pregunta");
        addQuestionBtn.setMaxWidth(Double.MAX_VALUE);
        addQuestionBtn.setStyle(getButtonStyle(BG3, ACCENT));
        addQuestionBtn.setOnAction(e -> addQuestionRow(null));

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web(ERROR));
        errorLabel.setFont(Font.font("Segoe UI", 12));
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);

        Button saveBtn = new Button("Guardar quiz");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle(getButtonStyle(ACCENT, "white"));

        saveBtn.setOnAction(e -> {
            String quizTitle = titleField.getText().trim();
            if (quizTitle.isEmpty()) {
                errorLabel.setText("⚠ El título no puede estar vacío");
                errorLabel.setVisible(true);
                return;
            }

            List<Question> questions = new ArrayList<>();
            for (QuestionFormRow row : questionRows) {
                String qText = row.questionField.getText().trim();
                if (qText.isEmpty()) continue;

                List<String> options = new ArrayList<>();
                for (TextField optField : row.optionFields) {
                    options.add(optField.getText().trim());
                }

                boolean allFilled = options.stream().noneMatch(String::isEmpty);
                if (!allFilled) {
                    errorLabel.setText("⚠ Completa todas las opciones de cada pregunta");
                    errorLabel.setVisible(true);
                    return;
                }

                int correctIndex = row.correctGroup.getToggles().indexOf(row.correctGroup.getSelectedToggle());
                if (correctIndex == -1) {
                    errorLabel.setText("⚠ Marca la respuesta correcta de cada pregunta");
                    errorLabel.setVisible(true);
                    return;
                }

                questions.add(new Question(qText, options, correctIndex));
            }

            if (questions.isEmpty()) {
                errorLabel.setText("⚠ Añade al menos una pregunta completa");
                errorLabel.setVisible(true);
                return;
            }

            Quiz quiz = editing ? existing : new Quiz();
            quiz.setTitle(quizTitle);
            quiz.setDescription(descField.getText().trim());
            quiz.setTimePerQuestion(timeSpinner.getValue());
            quiz.setQuestions(questions);

            onSave.accept(quiz);
            stage.close();
        });

        root.getChildren().addAll(
                title, titleField, descField, timeRow,
                questionsTitle, questionsContainer, addQuestionBtn,
                errorLabel, saveBtn
        );

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + ";");

        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private static void addQuestionRow(Question existingQuestion) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: " + BG2 + "; -fx-background-radius: 8;");

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        TextField questionField = createField("Pregunta", existingQuestion != null ? existingQuestion.getText() : "");
        HBox.setHgrow(questionField, Priority.ALWAYS);

        Button removeBtn = new Button("✕");
        removeBtn.setStyle(getButtonStyle(BG3, ERROR));

        header.getChildren().addAll(questionField, removeBtn);

        ToggleGroup correctGroup = new ToggleGroup();
        List<TextField> optionFields = new ArrayList<>();

        VBox optionsBox = new VBox(6);
        for (int i = 0; i < 4; i++) {
            HBox optRow = new HBox(8);
            optRow.setAlignment(Pos.CENTER_LEFT);

            RadioButton radio = new RadioButton();
            radio.setToggleGroup(correctGroup);
            radio.setStyle("-fx-text-fill: " + FG + ";");

            String optionValue = "";
            if (existingQuestion != null && i < existingQuestion.getOptions().size()) {
                optionValue = existingQuestion.getOptions().get(i);
                if (existingQuestion.getCorrectIndex() == i) {
                    radio.setSelected(true);
                }
            }

            TextField optField = createField("Opción " + (i + 1), optionValue);
            HBox.setHgrow(optField, Priority.ALWAYS);
            optionFields.add(optField);

            optRow.getChildren().addAll(radio, optField);
            optionsBox.getChildren().add(optRow);
        }

        Label hint = new Label("Selecciona el círculo de la respuesta correcta");
        hint.setTextFill(Color.web(FG2));
        hint.setFont(Font.font("Segoe UI", 10));

        card.getChildren().addAll(header, optionsBox, hint);

        QuestionFormRow row = new QuestionFormRow(card, questionField, optionFields, correctGroup);
        questionRows.add(row);

        removeBtn.setOnAction(e -> {
            questionsContainer.getChildren().remove(card);
            questionRows.remove(row);
        });

        questionsContainer.getChildren().add(card);
    }

    private static TextField createField(String prompt, String value) {
        TextField field = new TextField(value);
        field.setPromptText(prompt);
        field.setStyle(
                "-fx-background-color: " + BG3 + ";" +
                        "-fx-text-fill: " + FG + ";" +
                        "-fx-prompt-text-fill: " + FG2 + ";" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 8;" +
                        "-fx-font-size: 13px;"
        );
        return field;
    }

    private static String getButtonStyle(String bg, String fg) {
        return "-fx-background-color: " + bg + ";" +
                "-fx-text-fill: " + fg + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 14 8 14;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;";
    }

    private static class QuestionFormRow {
        VBox card;
        TextField questionField;
        List<TextField> optionFields;
        ToggleGroup correctGroup;

        QuestionFormRow(VBox card, TextField questionField, List<TextField> optionFields, ToggleGroup correctGroup) {
            this.card = card;
            this.questionField = questionField;
            this.optionFields = optionFields;
            this.correctGroup = correctGroup;
        }
    }
}