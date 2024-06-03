package com.example.GameShoots.clientPart;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientApplication extends Application {
    private static final List<Runnable> stopHandlers = new ArrayList<>();
    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("archer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Игра");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        //определение задачи, выполняемой в потоке
        for(Runnable r : stopHandlers) {
            r.run();
        }
    }
    public static void addStopHandler(Runnable r) {
        stopHandlers.add(r);
    }
    public static void removeStopHandler(Runnable r) {
        stopHandlers.remove(r);
    }
    public static Stage getMainStage() {
        return mainStage;
    }

    private static Stage mainStage;
    public static void main(String[] args) {
        launch();
    }
}