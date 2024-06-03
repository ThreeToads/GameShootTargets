package com.example.GameShoots.serverPart.entity;

import com.example.GameShoots.commonPart.entity.Player;
import com.example.GameShoots.serverPart.storageSystem.Players;
import com.example.GameShoots.serverPart.storageSystem.dbPlayer;

public class ServerPlayer extends Player {
    private final Players repository;

    private final dbPlayer persistentData;

    public ServerPlayer(dbPlayer dbPlayer, Players players) {
        super(dbPlayer.getId(), 75.0, 105.0, dbPlayer.getNickname(), dbPlayer.getRank());
        this.persistentData = dbPlayer;
        this.repository = players;
    }
    public void setRank(int rank) {
        super.setRank(rank);
        persistentData.setRank(rank);
        repository.updatePlayer(this);
    }

    public dbPlayer getPersistentData() {
        return persistentData;
    }
}
