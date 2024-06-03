package com.example.archer.serverPart.storageSystem;

import com.example.archer.commonPart.entity.LeaderBoard;
import com.example.archer.commonPart.entity.Player;
import com.example.archer.serverPart.entity.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class Players {
    public Player getPlayer(String nickname) {
        dbLoader dbLoader = new dbLoader();
        dbSaver dbSaver = new dbSaver();
        dbPlayer data = dbLoader.findByNickname(nickname);
        if(data == null) {
            data = new dbPlayer(nickname, 0);
            dbSaver.save(data);
        }
        return new ServerPlayer(data, this);
    }
    public List<LeaderBoard> getTopPlayers() {
        dbLoader dbLoader = new dbLoader();
        List<dbPlayer> data = dbLoader.getTopPlayers(10);
        List<LeaderBoard> leaderboard = new ArrayList<>();
        for(final var player : data) {
            leaderboard.add(new LeaderBoard(player.getNickname(), player.getRank()));
        }
        return leaderboard;
    }

    public void updatePlayer(ServerPlayer player) {
        dbSaver dbSaver = new dbSaver();
        dbSaver.update(player.getPersistentData());
    }
}
