package com.example.GameShoots.clientPart.renderers;

import com.example.GameShoots.commonPart.entity.Bullet;
import com.example.GameShoots.commonPart.entity.GameState;
import com.example.GameShoots.commonPart.entity.Player;
import com.example.GameShoots.commonPart.entity.Target;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;


import java.util.ArrayList;
import java.util.List;

public class GameRenderer {
    // Поле для хранения панели игры
    private final Pane gamePane;

    // Списки для хранения кругов пуль, кругов целей и полигонов игроков
    private ArrayList<ArrayList<Circle>> bulletsCircles;
    private ArrayList<Circle> targetsCircles;
    private ArrayList<Polygon> playersPolygon;

    // Конструктор, инициализирующий панель игры и списки
    public GameRenderer(Pane gamePane) {
        this.gamePane = gamePane;
        this.bulletsCircles = new ArrayList<>(4); // Инициализация списка пуль для 4 игроков
        for (int i = 0; i < 4; i++) {
            bulletsCircles.add(new ArrayList<>()); // Добавление пустых списков для каждого игрока
        }
        this.targetsCircles = new ArrayList<>(); // Инициализация списка целей
        this.playersPolygon = new ArrayList<>(); // Инициализация списка полигонов игроков
    }

    // Метод для создания полигона игрока на основе его координат
    private Polygon createPolygon(Player player) {
        Polygon playerPolygon = new Polygon();
        playerPolygon.getPoints().addAll(player.getX(), player.getY(),
                player.getX() - 20, player.getY() - 20,
                player.getX() - 20, player.getY() + 20);
        playerPolygon.setFill(Color.BLACK); // Установка цвета полигона
        return playerPolygon;
    }

    // Метод для отрисовки целей на игровом поле
    private void renderTargets(Target[] targets) {
        for (int i = 0; i < targets.length; i++) {
            if (i >= targetsCircles.size()) { // Если новых целей больше, чем уже нарисованных
                Circle circle = new Circle();
                circle.setCenterX(targets[i].getX());
                circle.setCenterY(targets[i].getY());
                circle.setRadius(targets[i].getRadius());
                circle.setFill(Color.MEDIUMPURPLE); // Установка цвета цели
                targetsCircles.add(circle); // Добавление новой цели в список
                gamePane.getChildren().add(circle); // Добавление новой цели на игровое поле
            } else {
                // Обновление координат уже существующей цели
                targetsCircles.get(i).setCenterX(targets[i].getX());
                targetsCircles.get(i).setCenterY(targets[i].getY());
            }
        }
    }

    // Метод для отрисовки игроков на игровом поле
    private void renderPlayers(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            if (i >= playersPolygon.size()) { // Если новых игроков больше, чем уже нарисованных
                Polygon polygon = createPolygon(player);
                playersPolygon.add(polygon); // Добавление нового игрока в список
                gamePane.getChildren().add(polygon); // Добавление нового игрока на игровое поле
                Label playerName = new Label(player.getNickname());
                playerName.setLayoutX(player.getX() - 20.0);
                playerName.setLayoutY(player.getY() - 35.0);
                gamePane.getChildren().add(playerName); // Добавление имени игрока на игровое поле
            }

            // Обработка пуль игрока
            List<Bullet> bullets = player.getBullets();
            if(!player.getBullets().isEmpty()) {
                for (int j = 0; j < bullets.size(); j++) {
                    try {
                        bulletsCircles.get(i).get(j).setCenterX(bullets.get(j).getX());
                        bulletsCircles.get(i).get(j).setCenterY(bullets.get(j).getY());
                        bulletsCircles.get(i).get(j).setVisible(true); // Обновление координат пули и установка видимости
                    } catch (IndexOutOfBoundsException e) {
                        renderBullet(bullets.get(j).getX(), bullets.get(j).getY(), bullets.get(j).getRadius(), i); // Создание новой пули, если её ещё нет в списке
                    }
                }
            }
            for (int j = bullets.size(); j < bulletsCircles.get(i).size(); j++) {
                // Скрытие пуль, которые больше не активны
                bulletsCircles.get(i).get(j).setVisible(false);
            }
        }
    }

    // Метод для создания и отрисовки новой пули
    private void renderBullet(double x, double y, int radius, int i) {
        Circle bullet = new Circle (x, y, radius, Color.BLACK); // Создание новой пули
        bulletsCircles.get(i).add(bullet); // Добавление пули в список
        gamePane.getChildren().add(bullet); // Добавление пули на игровое поле
    }

    // Метод для отрисовки состояния игры
    public void renderState(GameState state) {
        if(state == null) {
            return; // Если состояние игры null, выход из метода
        }
        renderTargets(state.getTargets()); // Отрисовка целей
        renderPlayers(state.getPlayers()); // Отрисовка игроков
    }

    // Метод для очистки игрового поля
    public void clearGame() {
        gamePane.getChildren().clear(); // Очистка всех элементов на игровом поле
        bulletsCircles.clear(); // Очистка списка пуль
    }
}

