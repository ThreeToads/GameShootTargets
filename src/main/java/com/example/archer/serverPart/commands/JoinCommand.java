package com.example.archer.serverPart.commands;

import com.example.archer.commonPart.entity.Lobby;
import com.example.archer.commonPart.entity.Player;
import com.example.archer.serverPart.Game;
import com.example.archer.serverPart.storageSystem.Players;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class JoinCommand {
    public static Pair<Boolean, Player> join(Lobby lobby, PrintWriter writer, BufferedReader br, Player associatedPlayer, Players players) throws IOException {
        synchronized (lobby) {
            String nickname = br.readLine();
            synchronized (Game.gameLock) {
                if (Game.currentGame != null && !Game.currentGame.getState().getIsFinished()) {
                    writer.println("gamestarted");
                    return new Pair<>(true, associatedPlayer);
                }
            }
            if (associatedPlayer != null) {
                writer.println("alreadyloggedin");
                return new Pair<>(true, associatedPlayer);            }
            if (lobby.getPlayerList().size() >= 4) {
                writer.println("full");
                return new Pair<>(true, associatedPlayer);            }

            boolean flag = true;
            for (Player p : lobby.getPlayerList()) {
                if (p.getNickname().equals(nickname)) {
                    writer.println("playerexists");
                    flag = false;
                    break;
                }
            }
            if (!flag) return new Pair<>(false, associatedPlayer);;

            System.out.println(nickname + " logged in");
            associatedPlayer = players.getPlayer(nickname);
            double y = 105.0;
            lobby.addPlayer(associatedPlayer);
            for (Player p : lobby.getPlayerList()) {
                p.setY(y);
                y += 90.0;
            }
        }
        writer.println("ok");
        return new Pair<>(true, associatedPlayer);
    }

}
