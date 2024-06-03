package com.example.archer.serverPart.commands;

import com.example.archer.commonPart.entity.Player;
import com.example.archer.serverPart.Game;

import java.io.PrintWriter;

public class ShootCommand {
    public static void shoot(PrintWriter writer, Player associatedPlayer) {
        if (associatedPlayer == null) {
            writer.println("noplayer");
            return;
        }
        synchronized (Game.gameLock) {
            if (Game.currentGame == null || Game.currentGame.getState().getIsFinished()) {
                writer.println("nogame");
                return;
            }
            associatedPlayer.shoot();
            writer.println("ok");
        }
    }
}
