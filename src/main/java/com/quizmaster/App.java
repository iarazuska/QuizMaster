package com.quizmaster;

import com.quizmaster.controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        MainController controller = new MainController(stage);
        BorderPane root = controller.getRoot();

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("QuizMaster");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(550);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}