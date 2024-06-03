package com.example.GameShoots.clientPart.entity;

import com.example.GameShoots.clientPart.controllers.PlayerStatsController;
import com.example.GameShoots.commonPart.entity.Player;
import javafx.scene.control.ListCell;

public class PlayerStatistics  extends ListCell<Player> {
    PlayerStatsController view;

    public PlayerStatistics() {
        view = new PlayerStatsController();
        setGraphic(view.getBox());
    }

    @Override
    protected void updateItem(Player player, boolean b) {
        super.updateItem(player, b);
        view.setPlayer(player);
    }
}
