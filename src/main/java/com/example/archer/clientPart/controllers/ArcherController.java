package com.example.archer.clientPart.controllers;

import com.example.archer.clientPart.ArcherApplication;
import com.example.archer.clientPart.entity.Game;
import com.example.archer.clientPart.entity.PlayerStatistics;
import com.example.archer.clientPart.renderers.GameRenderer;
import com.example.archer.clientPart.renderers.LeaderBoardRenderer;
import com.example.archer.commonPart.entity.GameState;
import com.example.archer.commonPart.entity.Player;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ArcherController {
    public static final int FRAME_DELAY = 25;
    @FXML
    private Label pauseLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button shootButton;
    @FXML
    private Button joinButton;
    @FXML
    private TextField nicknameText;
    @FXML
    private BorderPane mainLayout;
    @FXML
    private ListView<Player> playerListView;
    @FXML
    private Pane fieldPane;

    @FXML
    private Button leaderboardButton;
    private GameRenderer renderer;
    private Thread gameRunner;

    private final Game client = new Game();
    private final ObservableList<Player> playersList = FXCollections.observableArrayList();


    public void initialize() {
        renderer = new GameRenderer(fieldPane);
        playerListView.setCellFactory((ListView<Player> view) -> new PlayerStatistics());
        playerListView.setItems(playersList);
        ArcherApplication.addStopHandler(() -> {
            if (gameRunner != null) {
                gameRunner.interrupt();
                try {
                    gameRunner.join();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                synchronized (client) {
                    if (client.getConnected()) {
                        client.disconnect();
                    }
                }
            }
        });

        client.connect();
        gameRunner = new Thread(() -> {
            try {
                synchronized (client) {
                    client.wait();
                }
                while (true) {
                    while (true) {
                        synchronized (client) {
                            client.updateGameStartedStatus();
                            if (client.getGameStarted()) {
                                System.out.println("Game started");
                                break;
                            }
                            client.updateRoom();

                            List<Player> players = client.getLobby().getPlayerList();
                            Platform.runLater(() -> {
                                playersList.setAll(players);
                            });
                        }
                        Thread.sleep(FRAME_DELAY * 2);
                    }
                    // Game starts here
                    Platform.runLater(() -> {
                        shootButton.setDisable(false);
                        pauseButton.setDisable(false);
                    });
                    while (true) {
                        synchronized (client) {
                            client.updateState();
                            GameState state = client.getState();
                            Platform.runLater(() -> {
                                if (state == null) {
                                    renderer.clearGame();
                                    return;
                                }
                                if (state.getIsFinished()) {
                                    renderer.clearGame();
                                    return;
                                }
                                renderer.renderState(client.getState());
                                playersList.setAll(state.getPlayers());
                            });
                            if (client.getState() == null) {
                                Platform.runLater(() -> {
                                    new Alert(Alert.AlertType.INFORMATION, "Один игрок отключился, игра окончена :(").showAndWait();
                                });
                                break;
                            }


                            if (client.getState().getIsFinished()) {
                                GameState savedState = client.getState();
                                client.clearState();
                                Platform.runLater(() -> {
                                    new Alert(Alert.AlertType.INFORMATION, "Игрок " + savedState.getWinner() + " победил").showAndWait();
                                });
                                break;
                            }
                            if (client.getState().getIsPaused()) {
                                Platform.runLater(() -> {
                                    pauseLabel.setVisible(true);
                                    pauseButton.setDisable(true);
                                    shootButton.setDisable(true);
                                });
                            } else {
                                Platform.runLater(() -> {
                                    pauseLabel.setVisible(false);
                                    pauseButton.setDisable(false);
                                    shootButton.setDisable(false);
                                });
                            }
                        }
                        Thread.sleep(FRAME_DELAY);
                    }
                    Platform.runLater(() -> {
                        startButton.setDisable(false);
                        shootButton.setDisable(true);
                        pauseButton.setDisable(true);
                        pauseLabel.setVisible(false);
                    });
                }
            } catch (InterruptedException ignored) {
            }
        });
        gameRunner.start();
    }

    public void joinClick() {
        if (nicknameText.getText().isEmpty() || nicknameText.getText().isBlank()) {
            return;
        }
        synchronized (client) {
            System.out.println("sync");
            Boolean connected = client.join(nicknameText.getText());
            if (connected) {
                nicknameText.setDisable(true);
                joinButton.setDisable(true);
                startButton.setDisable(false);
                client.notifyAll();
            }
        }
    }

    public void startGame() {
        synchronized (client) {
            client.sendReady();
            if (client.getState() != null && client.getGameStarted()) {
                pauseButton.setDisable(false);
            }
        }
        startButton.setDisable(true);

    }

    public void pause() {
        synchronized (client) {
            client.sendNotReady();
        }
        pauseButton.setDisable(true);
        startButton.setDisable(false);
    }

    public void shoot() {
        synchronized (client) {
            client.shoot();
        }
    }

    public void showLeaderboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ArcherController.class.getResource("../leaderboard-view.fxml"));
            fxmlLoader.setController(new LeaderBoardRenderer(client));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = new Stage();
            stage.setTitle("Leaderboard");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}