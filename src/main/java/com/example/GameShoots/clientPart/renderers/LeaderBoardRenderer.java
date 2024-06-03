package com.example.GameShoots.clientPart.renderers;

import com.example.GameShoots.clientPart.entity.Game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class LeaderBoardRenderer {
    @FXML
    private TextArea leaderboardText;
    @FXML
    private Button refreshButton;
    private final Game client;

    public LeaderBoardRenderer(Game client) {
        this.client = client;
    }

    public void initialize() {
        refreshButton.setOnAction((e) -> refresh());
        refresh();
    }

    public void refresh() {
        if(client == null) {
            return;
        }
        synchronized (client) {
            var leaderboard = client.getLeaderboard();
            leaderboardText.setText("Таблица лидеров: ");
            for(var entry : leaderboard) {
                leaderboardText.setText(leaderboardText.getText() + "\n" + entry.playerName() + ": " + entry.rank() + " побед");
            }
        }
    }
}
