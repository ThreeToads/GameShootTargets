package com.example.archer.clientPart.controllers;

import com.example.archer.commonPart.entity.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PlayerStatsController {
    private Player player;

    @FXML
    private Label playerNameLabel;

    @FXML
    private Label playerScoreLabel;
    //@FXML
    //private Label getRank;//
    @FXML
    private Label playerShotsLabel;
    private VBox box;

    public PlayerStatsController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../player-info-view.fxml"));
        fxmlLoader.setController(this);
        try {
            box = fxmlLoader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateFields() {
        if(player != null) {
            playerNameLabel.setText(player.getNickname());
            playerScoreLabel.setText(String.valueOf(player.getScore()));
            playerShotsLabel.setText(String.valueOf(player.getShotsCount()));
            //getRank.setText(String.valueOf(player.getRank()));//
            this.getBox().setVisible(true);
        } else {
            this.getBox().setVisible(false);
        }
    }


    public void setPlayer(Player player) {
        this.player = player;
        updateFields();
    }

    public VBox getBox() {
        return box;
    }
}
