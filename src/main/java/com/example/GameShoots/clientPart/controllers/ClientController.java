package com.example.GameShoots.clientPart.controllers;

import com.example.GameShoots.clientPart.ClientApplication;
import com.example.GameShoots.clientPart.entity.Game;
import com.example.GameShoots.clientPart.entity.PlayerStatistics;
import com.example.GameShoots.clientPart.renderers.GameRenderer;
import com.example.GameShoots.clientPart.renderers.LeaderBoardRenderer;
import com.example.GameShoots.commonPart.entity.GameState;
import com.example.GameShoots.commonPart.entity.Player;
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

public class ClientController {
    public static final int FRAME_DELAY = 25; // Задержка кадров в миллисекундах

    @FXML
    private Label pauseLabel; // Метка для отображения паузы
    @FXML
    private Button startButton; // Кнопка для запуска игры
    @FXML
    private Button pauseButton; // Кнопка для паузы игры
    @FXML
    private Button shootButton; // Кнопка для стрельбы
    @FXML
    private Button joinButton; // Кнопка для присоединения к игре
    @FXML
    private TextField nicknameText; // Поле для ввода никнейма
    @FXML
    private BorderPane mainLayout; // Основной макет
    @FXML
    private ListView<Player> playerListView; // Список игроков
    @FXML
    private Pane fieldPane; // Панель для отображения игрового поля

    @FXML
    private Button leaderboardButton; // Кнопка для показа таблицы лидеров
    private GameRenderer renderer; // Рендерер для отрисовки игры
    private Thread gameRunner; // Поток для выполнения игровой логики

    private final Game client = new Game(); // Клиент для связи с сервером
    private final ObservableList<Player> playersList = FXCollections.observableArrayList(); // ObservableList для списка игроков

    public void initialize() {
        renderer = new GameRenderer(fieldPane); // Инициализация рендерера с игровой панелью
        playerListView.setCellFactory((ListView<Player> view) -> new PlayerStatistics()); // Установка фабрики для отображения статистики игроков
        playerListView.setItems(playersList); // Привязка списка игроков к ListView
        ClientApplication.addStopHandler(() -> { // Добавление обработчика остановки приложения
            if (gameRunner != null) {
                gameRunner.interrupt(); // Прерывание игрового потока
                try {
                    gameRunner.join(); // Ожидание завершения игрового потока
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex); // Обработка исключения
                }
                synchronized (client) {
                    if (client.getConnected()) {
                        client.disconnect(); // Отключение клиента от сервера
                    }
                }
            }
        });

        client.connect(); // Подключение клиента к серверу
        gameRunner = new Thread(() -> { // Создание игрового потока
            try {
                synchronized (client) {
                    client.wait(); // Ожидание уведомления от клиента
                }
                while (true) {
                    while (true) {
                        synchronized (client) {
                            client.updateGameStartedStatus(); // Обновление статуса начала игры
                            if (client.getGameStarted()) {
                                System.out.println("Game started"); // Вывод сообщения о начале игры
                                break;
                            }
                            client.updateRoom(); // Обновление состояния лобби

                            List<Player> players = client.getLobby().getPlayerList(); // Получение списка игроков
                            Platform.runLater(() -> {
                                playersList.setAll(players); // Обновление списка игроков на UI потоке
                            });
                        }
                        Thread.sleep(FRAME_DELAY * 2); // Задержка перед следующей итерацией
                    }
                    // Начало игры здесь
                    Platform.runLater(() -> {
                        shootButton.setDisable(false); // Включение кнопки стрельбы
                        pauseButton.setDisable(false); // Включение кнопки паузы
                    });
                    while (true) {
                        synchronized (client) {
                            client.updateState(); // Обновление состояния игры
                            GameState state = client.getState(); // Получение текущего состояния игры
                            Platform.runLater(() -> {
                                if (state == null) {
                                    renderer.clearGame(); // Очистка игры, если состояние null
                                    return;
                                }
                                if (state.getIsFinished()) {
                                    renderer.clearGame(); // Очистка игры, если игра завершена
                                    return;
                                }
                                renderer.renderState(client.getState()); // Отрисовка текущего состояния игры
                                playersList.setAll(state.getPlayers()); // Обновление списка игроков на UI потоке
                            });
                            if (client.getState() == null) {
                                Platform.runLater(() -> {
                                    new Alert(Alert.AlertType.INFORMATION, "Один игрок отключился, игра окончена :(").showAndWait(); // Уведомление о завершении игры
                                });
                                break;
                            }

                            if (client.getState().getIsFinished()) {
                                GameState savedState = client.getState(); // Сохранение состояния игры
                                client.clearState(); // Очистка состояния игры
                                Platform.runLater(() -> {
                                    new Alert(Alert.AlertType.INFORMATION, "Игрок " + savedState.getWinner() + " победил").showAndWait(); // Уведомление о победе игрока
                                });
                                break;
                            }
                            if (client.getState().getIsPaused()) {
                                Platform.runLater(() -> {
                                    pauseLabel.setVisible(true); // Отображение метки паузы
                                    pauseButton.setDisable(true); // Отключение кнопки паузы
                                    shootButton.setDisable(true); // Отключение кнопки стрельбы
                                });
                            } else {
                                Platform.runLater(() -> {
                                    pauseLabel.setVisible(false); // Скрытие метки паузы
                                    pauseButton.setDisable(false); // Включение кнопки паузы
                                    shootButton.setDisable(false); // Включение кнопки стрельбы
                                });
                            }
                        }
                        Thread.sleep(FRAME_DELAY); // Задержка перед следующей итерацией
                    }
                    Platform.runLater(() -> {
                        startButton.setDisable(false); // Включение кнопки старта
                        shootButton.setDisable(true); // Отключение кнопки стрельбы
                        pauseButton.setDisable(true); // Отключение кнопки паузы
                        pauseLabel.setVisible(false); // Скрытие метки паузы
                    });
                }
            } catch (InterruptedException ignored) {
            }
        });
        gameRunner.start(); // Запуск игрового потока
    }

    public void joinClick() {
        if (nicknameText.getText().isEmpty() || nicknameText.getText().isBlank()) {
            return; // Возврат, если текстовое поле никнейма пустое
        }
        synchronized (client) {
            System.out.println("sync"); // Вывод сообщения о синхронизации
            Boolean connected = client.join(nicknameText.getText()); // Присоединение к игре с указанным никнеймом
            if (connected) {
                nicknameText.setDisable(true); // Отключение текстового поля никнейма
                joinButton.setDisable(true); // Отключение кнопки присоединения
                startButton.setDisable(false); // Включение кнопки старта
                client.notifyAll(); // Уведомление всех потоков, ожидающих этого клиента
            }
        }
    }

    public void startGame() {
        synchronized (client) {
            client.sendReady(); // Отправка команды готовности
            if (client.getState() != null && client.getGameStarted()) {
                pauseButton.setDisable(false); // Включение кнопки паузы
            }
        }
        startButton.setDisable(true); // Отключение кнопки старта
    }

    public void pause() {
        synchronized (client) {
            client.sendNotReady(); // Отправка команды неготовности
        }
        pauseButton.setDisable(true); // Отключение кнопки паузы
        startButton.setDisable(false); // Включение кнопки старта
    }

    public void shoot() {
        synchronized (client) {
            client.shoot(); // Отправка команды выстрела
        }
    }

    public void showLeaderboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientController.class.getResource("../leaderboard-view.fxml")); // Загрузка FXML для отображения таблицы лидеров
            fxmlLoader.setController(new LeaderBoardRenderer(client)); // Установка контроллера
            Scene scene = new Scene(fxmlLoader.load()); // Создание новой сцены

            Stage stage = new Stage(); // Создание нового окна
            stage.setTitle("Leaderboard"); // Установка заголовка окна
            stage.setResizable(false); // Отключение возможности изменения размера окна
            stage.setScene(scene); // Установка сцены
            stage.show(); // Отображение окна
        } catch (IOException e) {
            throw new RuntimeException(e); // Обработка исключения
        }
    }
}
