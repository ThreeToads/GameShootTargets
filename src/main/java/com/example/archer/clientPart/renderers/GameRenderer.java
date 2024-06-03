package com.example.archer.clientPart.renderers;

import com.example.archer.commonPart.entity.Bullet;
import com.example.archer.commonPart.entity.GameState;
import com.example.archer.commonPart.entity.Player;
import com.example.archer.commonPart.entity.Target;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;


import java.util.ArrayList;
import java.util.List;

public class GameRenderer {
    private final Pane gamePane;

    private ArrayList<ArrayList<Circle>> bulletsCircles;
    private ArrayList<Circle> targetsCircles;
    private ArrayList<Polygon> playersPolygon;

    public GameRenderer(Pane gamePane) {
        this.gamePane = gamePane;
        this.bulletsCircles = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            bulletsCircles.add(new ArrayList<>());
        }
        this.targetsCircles = new ArrayList<>();
        this.playersPolygon = new ArrayList<>();
    }

    private Polygon createPolygon(Player player) {
        Polygon playerPolygon = new Polygon();
        playerPolygon.getPoints().addAll(player.getX(), player.getY(),
                player.getX() - 20, player.getY() - 20,
                player.getX() - 20, player.getY() + 20);
        playerPolygon.setFill(Color.BLACK);
        return playerPolygon;
    }

    private void renderTargets(Target[] targets) {
        for (int i = 0; i < targets.length; i++) {
            if (i >= targetsCircles.size()) {
                Circle circle = new Circle();
                circle.setCenterX(targets[i].getX());
                circle.setCenterY(targets[i].getY());
                circle.setRadius(targets[i].getRadius());
                circle.setFill(Color.MEDIUMPURPLE);
                targetsCircles.add(circle);
                gamePane.getChildren().add(circle);
            } else {
                targetsCircles.get(i).setCenterX(targets[i].getX());
                targetsCircles.get(i).setCenterY(targets[i].getY());
            }
        }
    }

    private void renderPlayers(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            if (i >= playersPolygon.size()) {
                Polygon polygon = createPolygon(player);
                playersPolygon.add(polygon);
                gamePane.getChildren().add(polygon);
                Label playerName = new Label(player.getNickname());
                playerName.setLayoutX(player.getX() - 20.0);
                playerName.setLayoutY(player.getY() - 35.0);
                gamePane.getChildren().add(playerName);
            }

            List<Bullet> bullets = player.getBullets();
            if(!player.getBullets().isEmpty()) {
                for (int j = 0; j < bullets.size(); j++) {
                    try {
                        bulletsCircles.get(i).get(j).setCenterX(bullets.get(j).getX());
                        bulletsCircles.get(i).get(j).setCenterY(bullets.get(j).getY());
                        bulletsCircles.get(i).get(j).setVisible(true);
                    } catch (IndexOutOfBoundsException e) {
                        renderBullet(bullets.get(j).getX(), bullets.get(j).getY(), bullets.get(j).getRadius(), i);
                    }
                }
            }
            for (int j = bullets.size(); j < bulletsCircles.get(i).size(); j++){
                System.out.println(bulletsCircles.get(i).size());
                bulletsCircles.get(i).get(j).setVisible(false);
            }
        }
    }

    private void renderBullet(double x, double y, int radius, int i) {
        Circle bullet = new Circle (x, y, radius, Color.BLACK);
        bulletsCircles.get(i).add(bullet);
        gamePane.getChildren().add(bullet);

    }

    public void renderState(GameState state) {
        if(state == null) {
            return;
        }
        renderTargets(state.getTargets());
        renderPlayers(state.getPlayers());
    }

    public void clearGame() {
        gamePane.getChildren().clear();
        bulletsCircles.clear();
    }
}
