package com.example.archer.commonPart.entity;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    @Expose
    private final List<Player> playerList;

    public Lobby() {
        playerList = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        playerList.add(player);
    }
    public List<Player> getPlayerList() {
        return playerList;
    }

    public void removePlayer(Player player) {
        playerList.remove(player);
    }
    public Boolean isReady() {
        if(playerList.size() < 2) {
            return false;
        }
        for(Player p : playerList) {
            if(!p.isReady()) {
                return false;
            }
        }
        return true;
    }
}
