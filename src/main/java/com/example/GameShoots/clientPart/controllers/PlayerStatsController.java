package com.example.GameShoots.clientPart.controllers;

import com.example.GameShoots.commonPart.entity.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

import java.io.IOException;

public class PlayerStatsController {
    // Поле для хранения объекта игрока
    private Player player;

    // Метки для отображения информации об игроке
    @FXML
    private Label playerNameLabel;

    @FXML
    private Label playerScoreLabel;

    @FXML
    private Label getRank; // для отображения ранга игрока
    @FXML
    private Label playerShotsLabel;

    // Контейнер для размещения элементов интерфейса
    private VBox box;

    // Конструктор контроллера
    public PlayerStatsController() {
        // Создание загрузчика FXML и установка текущего контроллера
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../player-info-view.fxml"));
        fxmlLoader.setController(this);
        try {
            // Загрузка FXML файла и инициализация VBox
            box = fxmlLoader.load();
        } catch (IOException e) {
            // Обработка ошибки загрузки FXML файла
            throw new RuntimeException(e);
        }
    }

    // Метод для обновления полей меток на основе данных игрока
    public void updateFields() {
        if (player != null) {
            // Установка текста меток на основе данных игрока
            playerNameLabel.setText(player.getNickname());
            playerScoreLabel.setText(String.valueOf(player.getScore()));
            playerShotsLabel.setText(String.valueOf(player.getShotsCount()));
            getRank.setText(String.valueOf(player.getRank())); // обновление ранга игрока
            // Сделать VBox видимым, если данные игрока установлены
            this.getBox().setVisible(true);
        } else {
            // Скрыть VBox, если данных игрока нет
            this.getBox().setVisible(false);
        }
    }

    // Метод для установки объекта игрока и обновления полей
    public void setPlayer(Player player) {
        this.player = player;
        updateFields();
    }

    // Метод для получения VBox
    public VBox getBox() {
        return box;
    }
}

