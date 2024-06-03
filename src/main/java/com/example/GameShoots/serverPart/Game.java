package com.example.GameShoots.serverPart;

import com.example.GameShoots.commonPart.entity.Bullet;
import com.example.GameShoots.commonPart.entity.GameState;
import com.example.GameShoots.commonPart.entity.Player;
import com.example.GameShoots.commonPart.entity.Target;

import java.util.ArrayList;

public class Game {
    public static Game currentGame;
    public final static Object gameLock = new Object();
    private final GameState state;

    public Game(double fieldWidth, double fieldHeight, Player[] players) {
        Target[] targets = new Target[] {
                new Target(20, fieldWidth * 0.66, fieldHeight * 0.5, fieldHeight - 30, 30, 1, 1),
                // 1я мишень
                new Target(10, fieldWidth * 0.9, fieldHeight * 0.5, fieldHeight - 30, 30, 2, 2),
                // 2я мишень создание
        };

        state = new GameState(fieldWidth, fieldHeight, targets, players.length);

        for(int i = 0; i < players.length; ++i) {
            state.getPlayers()[i] = players[i];
        }
    }

    public GameState getState() {
        return state;
    }

    public void step() {
        for (Player player : state.getPlayers()) {
            ArrayList<Integer> inds = new ArrayList<>();
            ArrayList<Bullet> bullets = player.getBullets();
            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).step();
                if (bullets.get(i).getX() > state.getFieldWidth()) {
                    inds.add(i);
                }
            }

            for (Integer ind : inds) {
                bullets.remove(bullets.get(ind));
            }
            inds.clear();

            for (Target target : state.getTargets()) {
                target.step();
                for (int i = 0; i < bullets.size(); i++) {
                    if (bullets.get(i) != null && target.isHit(bullets.get(i))) {
                        player.incrementScore(target.getScoreIncrement());
                        inds.add(i);

                        if(player.getScore() >= 10) {
                            this.getState().setIsFinished(true);
                            this.getState().setWinner(player);
                        }
                    }
                }
                for (Integer ind : inds) {
                    if (bullets.size() > ind)
                        bullets.remove(bullets.get(ind));
                }
            }
        }
    }
}
