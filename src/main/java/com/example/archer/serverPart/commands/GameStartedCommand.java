package com.example.archer.serverPart.commands;

import com.example.archer.serverPart.Game;

import java.io.PrintWriter;

public class GameStartedCommand {
    public static void startGame(PrintWriter writer) {
        synchronized (Game.gameLock) {
            writer.println(!(Game.currentGame == null || Game.currentGame.getState().getIsFinished()));
        }
    }
}
